package com.alisli.intelligenthistory.actions;

import com.alisli.intelligenthistory.highlighters.HighlighterFactory;
import com.alisli.intelligenthistory.services.UsageLoggingService;
import com.intellij.openapi.actionSystem.*;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vcs.*;
import com.intellij.openapi.vcs.actions.VcsContextUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.vcs.log.VcsLogHighlighter;
import com.intellij.vcs.log.history.FileHistoryUi;
import com.intellij.vcs.log.ui.VcsLogInternalDataKeys;
import com.intellij.vcs.log.util.VcsLogUtil;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class HighlightImportantHistoryAction extends ToggleAction {

    @Override
    public void update(@NotNull AnActionEvent e) {
        boolean selected = isSelected(e);
        final Presentation presentation = e.getPresentation();
        Toggleable.setSelected(presentation, selected);
    }

    @Override
    public boolean isSelected(@NotNull AnActionEvent e) {
        return Toggleable.isSelected(e.getPresentation());
    }

    @Override
    public void setSelected(@NotNull AnActionEvent e, boolean state) {
        Project project = Objects.requireNonNull(e.getProject());
        List<FilePath> selectedFiles = VcsContextUtil.selectedFilePaths(e.getDataContext());
        FilePath path = selectedFiles.get(0);
        VirtualFile root = VcsLogUtil.getActualRoot(project, path);
        FileHistoryUi logUi = e.getRequiredData(VcsLogInternalDataKeys.FILE_HISTORY_UI);
        VcsLogHighlighter highlighter = HighlighterFactory.getHighlighter(project, root, path, logUi);

        UsageLoggingService loggingService = UsageLoggingService.getInstance();
        String fileName = e.getRequiredData(CommonDataKeys.VIRTUAL_FILE).getPresentableName();

        if (!state) {
            logUi.getTable().removeHighlighter(highlighter);
            HighlighterFactory.disposeHighlighter(path);
            loggingService.writeEventToLog(fileName, UsageLoggingService.LogEventType.HIGHLIGHT_TOGGLE + ": Toggle OFF");
        } else {
            logUi.getTable().addHighlighter(highlighter);
            loggingService.writeEventToLog(fileName, UsageLoggingService.LogEventType.HIGHLIGHT_TOGGLE + ": Toggle ON");
        }
        logUi.getMainComponent().updateUI();
    }
}