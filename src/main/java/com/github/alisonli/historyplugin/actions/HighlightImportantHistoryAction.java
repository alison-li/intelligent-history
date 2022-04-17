package com.github.alisonli.historyplugin.actions;

import com.github.alisonli.historyplugin.ImportantCommitsHighlighter;
import com.github.alisonli.historyplugin.services.DiffAnalyzerService;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vcs.*;
import com.intellij.openapi.vcs.actions.VcsContextUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.vcs.log.data.VcsLogData;
import com.intellij.vcs.log.history.FileHistoryUi;
import com.intellij.vcs.log.impl.VcsProjectLog;
import com.intellij.vcs.log.ui.VcsLogInternalDataKeys;
import com.intellij.vcs.log.util.VcsLogUtil;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class HighlightImportantHistoryAction extends AnAction {
    private static final Logger LOG = Logger.getInstance(HighlightImportantHistoryAction.class);

    @Override
    public void update(@NotNull AnActionEvent e) {
        Project project = e.getProject();
        e.getPresentation().setVisible(project != null && ProjectLevelVcsManager.getInstance(project).hasActiveVcss());
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        Project project = Objects.requireNonNull(e.getProject());
        FileHistoryUi logUi = e.getRequiredData(VcsLogInternalDataKeys.FILE_HISTORY_UI);
        List<FilePath> selectedFiles = VcsContextUtil.selectedFilePaths(e.getDataContext());

        VirtualFile root = VcsLogUtil.getActualRoot(project, selectedFiles.get(0));

        highlightImportantCommits(project, root, logUi, DiffAnalyzerService.getImportantCommits(project, root, logUi));
    }

    private void highlightImportantCommits(Project project, VirtualFile root, FileHistoryUi ui,
                                           Set<Integer> importantCommits) {
        VcsLogData logData = VcsProjectLog.getInstance(project).getLogManager().getDataManager();
        ui.getTable().addHighlighter(new ImportantCommitsHighlighter(ui, logData, root, importantCommits));
    }
}