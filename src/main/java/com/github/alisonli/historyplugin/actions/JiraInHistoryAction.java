package com.github.alisonli.historyplugin.actions;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.Project;
import com.intellij.vcs.log.VcsLogDataKeys;
import com.intellij.vcs.log.VcsLogUi;
import org.jetbrains.annotations.NotNull;

public class JiraInHistoryAction extends AnAction {

    /**
     * Enable action only when a project and a file from the project is currently open.
     *
     * @param e Carries information on the invocation place.
     */
    @Override
    public void update(@NotNull AnActionEvent e) {
        Project project = e.getProject();
        VcsLogUi ui = e.getData(VcsLogDataKeys.VCS_LOG_UI);
        e.getPresentation().setEnabledAndVisible(project != null && ui != null);
    }

    /**
     * Interleaves Jira issue information with file history.
     *
     * @param e Carries information on the invocation place.
     */
    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        // TODO: Implement
        // See: https://plugins.jetbrains.com/docs/intellij/basic-action-system.html
    }
}
