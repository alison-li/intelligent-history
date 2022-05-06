package com.alisli.intelligenthistory.services;

import com.alisli.intelligenthistory.notification.NotificationManager;
import com.alisli.intelligenthistory.settings.JiraConfig;
import com.alisli.intelligenthistory.model.JiraIssueMetadata;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import com.intellij.vcs.log.VcsFullCommitDetails;
import net.rcarz.jiraclient.*;

import java.util.*;
import java.util.regex.MatchResult;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Project service for fetching the Jira information in a file history.
 */
public final class JiraFetcherService {
    private static final Logger LOG = Logger.getInstance(JiraFetcherService.class);
    private static final String BOT_NAME_PATTERN = "(.*)((\\b([Bb]ot|BOT))|(([Bb]ot|BOT)\\b))(.*)";
    private static final String JIRA_ISSUE_KEY = "([A-Z]+-\\d+)";
    private final Project project;
    private final JiraConfig config;
    private final JiraClient client;

    public JiraFetcherService(Project project) {
        this.project = project;
        this.config = JiraConfig.getInstance(project);
        if (config == null
                || isNullOrEmpty(config.getEndpointURL())
                || isNullOrEmpty(config.getUsername())
                || isNullOrEmpty(config.getPassword())) {
            this.client = null;
        } else {
            this.client = new JiraClient(Objects.requireNonNull(config).getEndpointURL(),
                    new BasicCredentials(config.getUsername(), config.getPassword()));
        }
    }

    public JiraIssueMetadata getJiraIssueMetadata(VcsFullCommitDetails detail) {
        String issueKey = extractJiraIssueKey(detail.getSubject());
        if (client == null) {
            NotificationManager.showJiraConfigNotFound(project);
        } else if (issueKey == null) {
            NotificationManager.showIssueNotFound(project, detail.getId().toShortString());
        } else {
            try {
                JiraIssueMetadata issueMetadata = new JiraIssueMetadata(detail);
                issueMetadata.setIssueKey(issueKey);
                Issue issue = client.getIssue(issueKey);
                List<Comment> commentsExcludeBots = new ArrayList<>();
                Set<String> people = new HashSet<>();
                people.add(issue.getAssignee().toString());
                int commitAuthorComments = 0;
                for (Comment comment : issue.getComments()) {
                    String commentAuthorDisplayName = comment.getAuthor().getDisplayName();
                    String commentAuthorName = comment.getAuthor().getName();
                    if (!commentAuthorDisplayName.matches(BOT_NAME_PATTERN)
                            || !commentAuthorName.matches(BOT_NAME_PATTERN)) {
                        commentsExcludeBots.add(comment);
                        people.add(commentAuthorName);
                        String commitAuthorName = String.valueOf(detail.getAuthor());
                        if (commentAuthorDisplayName.equals(commitAuthorName)) {
                            commitAuthorComments++;
                        }
                    }
                }
                issueMetadata.setComments(commentsExcludeBots.size());
                issueMetadata.setCommitAuthorComments(commitAuthorComments);
                issueMetadata.setPeopleInvolved(people.size());
                issueMetadata.setTitle(issue.getSummary());
                issueMetadata.setDescription(issue.getDescription());
                issueMetadata.setReporter(issue.getReporter().getDisplayName());
                issueMetadata.setAssignee(issue.getAssignee().getDisplayName());
                issueMetadata.setPriority(issue.getPriority().toString());
                issueMetadata.setUrl(config.getEndpointURL() + "/browse/" + issueKey);
                issueMetadata.setIssueLinks(issue.getIssueLinks().size());
                issueMetadata.setSubTasks(issue.getSubtasks().size());
                issueMetadata.setVotes(issue.getVotes().getVotes());
                issueMetadata.setWatches(issue.getWatches().getWatchCount());
                return issueMetadata;
            } catch (JiraException e) {
                LOG.error(e.getMessage());
                if (e.getCause() != null) {
                    LOG.error(e.getCause().getMessage());
                }
            }
        }
        return null;
    }

    private String extractJiraIssueKey(String commitSubject) {
        Pattern pattern = Pattern.compile(JIRA_ISSUE_KEY, Pattern.MULTILINE);
        List<MatchResult> results = pattern.matcher(commitSubject)
                .results()
                .collect(Collectors.toList());
        if (results.isEmpty()) {
            return null;
        }
        return results.get(0).group(1);
    }

    private boolean isNullOrEmpty(String s) {
        return s == null || s.length() == 0;
    }
}
