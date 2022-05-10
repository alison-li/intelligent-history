package com.alisli.intelligenthistory.listeners;

import com.alisli.intelligenthistory.services.UsageLoggingService;
import com.intellij.application.Topics;
import com.intellij.openapi.Disposable;
import com.intellij.openapi.actionSystem.ex.AnActionListener;
import com.intellij.vcs.log.Hash;
import com.intellij.vcs.log.graph.VisibleGraph;
import com.intellij.vcs.log.ui.table.VcsLogGraphTable;

import java.awt.*;
import java.awt.event.AWTEventListener;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.Collections;

public class MyCommitSelectionListener implements AWTEventListener, AnActionListener, Disposable {
    private boolean mouseDrag = false;

    public MyCommitSelectionListener() {
        Topics.subscribe(AnActionListener.TOPIC, this, this);
        long eventMask = AWTEvent.MOUSE_EVENT_MASK | AWTEvent.MOUSE_MOTION_EVENT_MASK | AWTEvent.WINDOW_EVENT_MASK | AWTEvent.WINDOW_STATE_EVENT_MASK;
        Toolkit.getDefaultToolkit().addAWTEventListener(this, eventMask);
    }

    @Override
    public void eventDispatched(AWTEvent e) {
        int id = e.getID();
        if (id == MouseEvent.MOUSE_DRAGGED) {
            mouseDrag = true;
            return;
        }
        if (id == MouseEvent.MOUSE_RELEASED && ((MouseEvent) e).getButton() == MouseEvent.BUTTON1) {
            if (!mouseDrag) {
                handleMouseEvent(e);
            }
            mouseDrag = false;
        }
    }

    private void handleMouseEvent(AWTEvent e) {
        final Object source = e.getSource();
        System.out.println(source);
        if (source instanceof VcsLogGraphTable) {
            VcsLogGraphTable table = (VcsLogGraphTable) source;
            VisibleGraph<Integer> visibleGraph = table.getVisibleGraph();
            Integer commitId = visibleGraph.getRowInfo(table.getSelectedRow()).getCommit();
            Hash commitHash = table.getLogData()
                    .getMiniDetailsGetter()
                    .getCommitData(commitId, Collections.singleton(commitId)).getId();

            UsageLoggingService loggingService = UsageLoggingService.getInstance();
            String fileName = new File(table.getId()).getName();
            loggingService.writeEventToLog(fileName, UsageLoggingService.LogEventType.COMMIT_SELECTION
                    + ": "
                    + commitHash.toShortString());
        }
    }

    @Override
    public void dispose() {

    }
}
