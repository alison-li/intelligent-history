package com.github.alisonli.historyplugin.actions;

import com.github.alisonli.historyplugin.components.JiraIssuePanel;
import com.github.alisonli.historyplugin.model.JiraIssueMetadata;
import com.github.alisonli.historyplugin.services.JiraFetcherService;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowManager;
import com.intellij.ui.components.JBScrollPane;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentFactory;
import com.intellij.vcs.log.VcsFullCommitDetails;
import com.intellij.vcs.log.data.DataGetter;
import com.intellij.vcs.log.history.FileHistoryUi;
import com.intellij.vcs.log.ui.actions.history.FileHistorySingleCommitAction;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.util.List;
import java.util.Objects;

public class ShowJiraMetadataAction extends FileHistorySingleCommitAction<VcsFullCommitDetails> {

    @Override
    protected @NotNull List<VcsFullCommitDetails> getSelection(@NotNull FileHistoryUi ui) {
        return ui.getVcsLog().getSelectedDetails();
    }

    @Override
    protected @NotNull DataGetter<VcsFullCommitDetails> getDetailsGetter(@NotNull FileHistoryUi ui) {
        return ui.getLogData().getCommitDetailsGetter();
    }

    @Override
    protected void performAction(@NotNull Project project, @NotNull FileHistoryUi ui,
                                 @NotNull VcsFullCommitDetails detail, @NotNull AnActionEvent e) {
        JiraFetcherService jiraService = new JiraFetcherService(project);
        JiraIssueMetadata issueMetadata = jiraService.getJiraIssueMetadata(detail);
        if (issueMetadata != null) {
            ToolWindow toolWindow = ToolWindowManager.getInstance(project).getToolWindow("Jira");
            JiraIssuePanel toolWindowBuilder = new JiraIssuePanel();
            toolWindowBuilder.setTitle(issueMetadata.getTitle());
            toolWindowBuilder.setBodyText(issueMetadata.getDescription());
            JBScrollPane toolWindowContent = new JBScrollPane(toolWindowBuilder.getContent());
            toolWindowContent.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
            ContentFactory contentFactory = ContentFactory.SERVICE.getInstance();
            Content content = contentFactory.createContent(toolWindowContent, issueMetadata.getIssueKey(), false);
            content.setPreferredFocusableComponent(toolWindowContent);
            content.setDisposer(toolWindowBuilder);
            Objects.requireNonNull(toolWindow).getContentManager().addContent(content);
            Objects.requireNonNull(toolWindow).show();
        }
    }
}
