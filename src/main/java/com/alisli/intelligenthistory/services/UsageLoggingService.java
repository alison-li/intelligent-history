package com.alisli.intelligenthistory.services;

import com.intellij.openapi.application.PathManager;
import com.intellij.openapi.diagnostic.Logger;

import java.awt.*;
import java.awt.event.AWTEventListener;
import java.awt.event.MouseEvent;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Timestamp;

public class UsageLoggingService implements AWTEventListener {
    private static final Logger LOG = Logger.getInstance(UsageLoggingService.class);
    private static UsageLoggingService instance;
    String pluginLogPath = PathManager.getLogPath() + "/intelligent-history/intelligent-history-log.txt";
    private boolean mouseDrag = false;

    private UsageLoggingService() {
        File file = new File(pluginLogPath);
        if (file.exists()) {
            file.delete();
        }
        if (file.getParentFile().mkdir()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                LOG.error(e);
            }
        }
    }

    public static UsageLoggingService getInstance() {
        if (instance == null) {
            instance = new UsageLoggingService();
        }
        return instance;
    }

    public void writeEventToLog(String fileName, String message) {
        BufferedWriter writer;
        try {
            writer = new BufferedWriter(new FileWriter(pluginLogPath));
            String formattedMessage = String.format("[%s][%s] %s",
                    fileName, new Timestamp(System.currentTimeMillis()), message);
            writer.write(formattedMessage);
            writer.close();
        } catch (IOException e) {
            LOG.error(e);
        }
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

    // TODO: Listen to selected commits when in file history UI
    private void handleMouseEvent(AWTEvent e) {
        System.out.println(e.getSource());
    }

    public enum LogEventType {
        HIGHLIGHT_TOGGLE,
        DIFF_METADATA_INVOKE,
        JIRA_METADATA_INVOKE,
        JIRA_EXTERNAL_LINK,
        COMMIT_SELECTION
    }
}