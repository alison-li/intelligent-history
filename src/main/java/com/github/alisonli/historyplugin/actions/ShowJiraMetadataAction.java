package com.github.alisonli.historyplugin.actions;

import com.github.alisonli.historyplugin.model.JiraIssueMetadata;
import com.github.alisonli.historyplugin.services.JiraFetcherService;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowManager;
import com.intellij.vcs.log.VcsFullCommitDetails;
import com.intellij.vcs.log.data.DataGetter;
import com.intellij.vcs.log.history.FileHistoryUi;
import com.intellij.vcs.log.ui.actions.history.FileHistorySingleCommitAction;
import org.jetbrains.annotations.NotNull;

import java.util.List;

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
        JiraIssueMetadata jiraIssueMetadata = jiraService.getJiraIssueMetadata(detail);
        ToolWindow panel = ToolWindowManager.getInstance(project).getToolWindow("Jira");
    }
}
