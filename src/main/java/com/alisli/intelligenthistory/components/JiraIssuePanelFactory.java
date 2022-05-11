package com.alisli.intelligenthistory.components;

import com.alisli.intelligenthistory.model.JiraIssueMetadata;
import com.intellij.ide.util.RunOnceUtil;
import com.intellij.openapi.project.DumbAware;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowFactory;
import com.intellij.ui.components.JBScrollPane;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentFactory;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

public class JiraIssuePanelFactory implements ToolWindowFactory, DumbAware {
    @Override
    public void createToolWindowContent(@NotNull Project project, @NotNull ToolWindow toolWindow) {
        RunOnceUtil.runOnceForProject(project, "Jira Metadata Description", () -> {
            Content content = createJiraContent("Description", null, null);
            toolWindow.getContentManager().addContent(content);
        });
    }

    public static Content createJiraContent(String displayName, JiraIssueMetadata issueMetadata, String fileName) {
        JiraIssuePanel toolWindowBuilder = new JiraIssuePanel();
        if (issueMetadata != null) {
            setJiraMetadataContent(toolWindowBuilder, issueMetadata, fileName);
        }
        JBScrollPane toolWindowContent = new JBScrollPane(toolWindowBuilder.getContent());
        toolWindowContent.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        ContentFactory contentFactory = ContentFactory.SERVICE.getInstance();
        Content content = contentFactory.createContent(toolWindowContent, displayName, false);
        content.setPreferredFocusableComponent(toolWindowContent);
        content.setDisposer(toolWindowBuilder);
        return content;
    }

    private static void setJiraMetadataContent(JiraIssuePanel panel, JiraIssueMetadata issueMetadata, String fileName) {
        panel.setTitle(String.format("<html> " +
                        "<b>%s </b> " +
                        "<br>" +
                        "%s",
                issueMetadata.getTitle(),
                issueMetadata.getHash())
        );
        panel.setBodyText(String.format("<b>Assignee:</b> %s" +
                        "<br>" +
                        "<b>Reporter:</b> %s" +
                        "<br><br>" +
                        "<b>Description:</b> <br> %s" +
                        "<br><br>" +
                        "<b>Priority:</b> %s" +
                        "<br><br>" +
                        "<b>Issue Metadata:</b><br>" +
                        "Commit Author Comments: %s <br>" +
                        "Total Comments: %s <br>" +
                        "People Involved: %s <br>" +
                        "Watches: %s <br>" +
                        "Votes: %s <br>" +
                        "Issue Links: %s <br>" +
                        "Sub Tasks: %s",
                issueMetadata.getAssignee(),
                issueMetadata.getReporter(),
                issueMetadata.getDescription() == null ? "" : issueMetadata.getDescription(),
                issueMetadata.getPriority(),
                issueMetadata.getCommitAuthorComments(),
                issueMetadata.getComments(),
                issueMetadata.getPeopleInvolved(),
                issueMetadata.getWatches(),
                issueMetadata.getVotes(),
                issueMetadata.getIssueLinks(),
                issueMetadata.getSubTasks())
        );
        panel.createActionLink(issueMetadata, fileName);
    }
}
