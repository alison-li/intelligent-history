package com.github.alisonli.historyplugin.actions;

import com.intellij.ide.BrowserUtil;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import org.jetbrains.annotations.NotNull;

public class JiraInHistoryAction extends AnAction {

    @Override
    public void update(@NotNull AnActionEvent e) {
        // TODO: Enable action only when file history tool window is open
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        // TODO: Implement
        // See: https://plugins.jetbrains.com/docs/intellij/basic-action-system.html
        BrowserUtil.browse("https://www.atlassian.com/software/jira");
    }
}
