package com.github.alisonli.historyplugin.listeners;

import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ex.ToolWindowManagerListener;
import org.jetbrains.annotations.NotNull;

/**
 * Listener to detect history window open.
 */
public class HistoryWindowListener implements ToolWindowManagerListener {

    /**
     * Invoked when tool window is shown.
     *
     * @param toolWindow shown tool window
     */
    @Override
    public void toolWindowShown(@NotNull ToolWindow toolWindow) {
        // TODO: Trying to get the "Show History" tool window ID
        Messages.showInfoMessage(toolWindow.getId(), "TOOL WINDOW ID");
    }
}
