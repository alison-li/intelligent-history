package com.alisli.intelligenthistory.actions;

import com.alisli.intelligenthistory.components.JiraIssuePanelFactory;
import com.alisli.intelligenthistory.services.JiraFetcherService;
import com.alisli.intelligenthistory.model.JiraIssueMetadata;
import com.alisli.intelligenthistory.services.UsageLoggingService;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowManager;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentManager;
import com.intellij.vcs.log.VcsFullCommitDetails;
import com.intellij.vcs.log.data.DataGetter;
import com.intellij.vcs.log.history.FileHistoryUi;
import com.intellij.vcs.log.ui.actions.history.FileHistorySingleCommitAction;
import org.jetbrains.annotations.NotNull;

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
            UsageLoggingService loggingService = UsageLoggingService.getInstance();
            loggingService.writeEventToLog(e.getRequiredData(CommonDataKeys.VIRTUAL_FILE).getPresentableName(),
                    UsageLoggingService.LogEventType.JIRA_METADATA_INVOKE
                            + ": "
                            + issueMetadata.getIssueKey()
                            + " for commit "
                            + issueMetadata.getHash());

            ToolWindow toolWindow = ToolWindowManager.getInstance(project).getToolWindow("Jira Metadata");
            String fileName = e.getRequiredData(CommonDataKeys.VIRTUAL_FILE).getPresentableName();
            Content content = JiraIssuePanelFactory.createJiraContent(issueMetadata.getIssueKey(), issueMetadata, fileName);
            ContentManager contentManager = Objects.requireNonNull(toolWindow).getContentManager();
            boolean contentAlreadyExists = false;
            for (Content c : contentManager.getContents()) {
                if (c.getDisplayName().equals(content.getDisplayName())) {
                    contentAlreadyExists = true;
                    contentManager.setSelectedContent(c);
                    break;
                }
            }
            if (!contentAlreadyExists) {
                contentManager.addContent(content);
                contentManager.setSelectedContent(content);
            }
            Objects.requireNonNull(toolWindow).show();
        }
    }
}
