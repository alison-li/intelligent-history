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
            Content content = createJiraContent("Description", null);
            toolWindow.getContentManager().addContent(content);
        });
    }

    public static Content createJiraContent(String displayName, JiraIssueMetadata issueMetadata) {
        JiraIssuePanel toolWindowBuilder = new JiraIssuePanel();
        if (issueMetadata != null) {
            toolWindowBuilder.setTitle(String.format("<html> " +
                            "<b> %s </b> " +
                            "<br>" +
                            "%s",
                    issueMetadata.getTitle(),
                    issueMetadata.getHash())
            );
            toolWindowBuilder.setBodyText(String.format("Assignee: %s <br> Reporter: %s <br><br> %s ", issueMetadata.getAssignee(), issueMetadata.getReporter(),
                    issueMetadata.getDescription()));
        }
        JBScrollPane toolWindowContent = new JBScrollPane(toolWindowBuilder.getContent());
        toolWindowContent.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        ContentFactory contentFactory = ContentFactory.SERVICE.getInstance();
        Content content = contentFactory.createContent(toolWindowContent, displayName, false);
        content.setPreferredFocusableComponent(toolWindowContent);
        content.setDisposer(toolWindowBuilder);
        return content;
    }
}
