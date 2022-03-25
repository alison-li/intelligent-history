package com.github.alisonli.historyplugin.listeners;

import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ex.ToolWindowManagerListener;
import org.jetbrains.annotations.NotNull;

/**
 * Listener to detect history window open.
 */
public class HistoryWindowListener implements ToolWindowManagerListener {

    private static final Logger LOG = Logger.getInstance(HistoryWindowListener.class);

    /**
     * Invoked when tool window is shown.
     *
     * @param toolWindow shown tool window
     */
    @Override
    public void toolWindowShown(@NotNull ToolWindow toolWindow) {
        LOG.info("TOOL WINDOW SHOWN: " +
                toolWindow.getTitle() + ", " +
                toolWindow.getId());
    }
}
