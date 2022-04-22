package com.github.alisonli.historyplugin.model;

import com.intellij.vcs.log.VcsFullCommitDetails;

public class JiraIssueMetadata {
    private final VcsFullCommitDetails detail;
    private String title;
    private String description;
    private String priority;
    private String url;
    private int issueLinks;
    private int subTasks;
    private int votes;
    private int watches;
    private int comments; // excludes bot comments
    private int commitAuthorComments; // proportion of comments that are by the commit author
    private int peopleInvolved; // total number of people involved in issue, including commenters and assignee
    private boolean isEmpty;

    public JiraIssueMetadata(VcsFullCommitDetails detail) {
        this.detail = detail;
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

    public boolean isEmpty() {
        return isEmpty;
    }

    public void setEmpty(boolean isEmpty) {
        this.isEmpty = isEmpty;
    }

    @Override
    public String toString() {
        // TODO: Need better formatting
        String issueLinksFormatted = String.format("Issue Links: %d", this.getIssueLinks());
        String subTasksFormatted = String.format("Sub-tasks: %d", this.getSubTasks());
        String votesFormatted = String.format("Votes: %d", this.getVotes());
        String watchesFormatted = String.format("Watches: %d", this.getWatches());
        String commentsFormatted = String.format("Comments: %d", this.getComments());
        String commitAuthorCommentsFormatted = String.format("Commit Author Comments: %d", this.getCommitAuthorComments());
        String peopleInvolvedFormatted = String.format("People Involved: %d", this.getPeopleInvolved());
        return String.format("<b> %s </b> <br/> <b>Priority:<b> %s <br/> %s <br/> <br/> " +
                        "%s <br/> %s <br/> %s <br/> %s <br/> %s <br/> %s <br/> %s <br/> <br/> %s",
                this.getTitle(), this.getPriority(), this.getDescription(),
                issueLinksFormatted, subTasksFormatted, votesFormatted, watchesFormatted, commentsFormatted,
                commitAuthorCommentsFormatted, peopleInvolvedFormatted, this.getUrl());
    }
}
