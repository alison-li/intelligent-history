package com.alisli.intelligenthistory.model;

import com.intellij.vcs.log.VcsFullCommitDetails;

public class JiraIssueMetadata {
    private final VcsFullCommitDetails detail;
    private String issueKey;
    private String title;
    private String description;
    private String reporter;
    private String assignee;
    private String priority;
    private String url;
    private int issueLinks;
    private int subTasks;
    private int votes;
    private int watches;
    private int comments; // excludes bot comments
    private int commitAuthorComments; // proportion of comments that are by the commit author
    private int peopleInvolved; // total number of people involved in issue, including commenters and assignee

    public JiraIssueMetadata(VcsFullCommitDetails detail) {
        this.detail = detail;
    }

    public String getHash() {
        return detail.getId().toShortString();
    }

    public String getIssueKey() {
        return issueKey;
    }

    public void setIssueKey(String issueKey) {
        this.issueKey = issueKey;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getReporter() {
        return reporter;
    }

    public void setReporter(String reporter) {
        this.reporter = reporter;
    }

    public String getAssignee() {
        return assignee;
    }

    public void setAssignee(String assignee) {
        this.assignee = assignee;
    }

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public int getIssueLinks() {
        return issueLinks;
    }

    public void setIssueLinks(int issueLinks) {
        this.issueLinks = issueLinks;
    }

    public int getSubTasks() {
        return subTasks;
    }

    public void setSubTasks(int subTasks) {
        this.subTasks = subTasks;
    }

    public int getVotes() {
        return votes;
    }

    public void setVotes(int votes) {
        this.votes = votes;
    }

    public int getWatches() {
        return watches;
    }

    public void setWatches(int watches) {
        this.watches = watches;
    }

    public int getComments() {
        return comments;
    }

    public void setComments(int comments) {
        this.comments = comments;
    }

    public int getCommitAuthorComments() {
        return commitAuthorComments;
    }

    public void setCommitAuthorComments(int commitAuthorComments) {
        this.commitAuthorComments = commitAuthorComments;
    }

    public int getPeopleInvolved() {
        return peopleInvolved;
    }

    public void setPeopleInvolved(int peopleInvolved) {
        this.peopleInvolved = peopleInvolved;
    }
}
