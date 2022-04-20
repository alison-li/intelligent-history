package com.github.alisonli.historyplugin.highlighters;

import com.github.alisonli.historyplugin.services.DiffAnalyzerService;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.ui.JBColor;
import com.intellij.vcs.log.*;
import com.intellij.vcs.log.data.VcsLogData;
import com.intellij.vcs.log.history.FileHistoryUi;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.awt.*;
import java.util.Arrays;
import java.util.Set;

public class ImportantCommitsHighlighter implements VcsLogHighlighter {
    private static ImportantCommitsHighlighter INSTANCE;
    private final FileHistoryUi myUi;
    private final VcsLogData myLogData;
    private final VirtualFile myRoot;
    private Set<Integer> myImportantCommits;

    private static final Color REGULAR_TEXT_COLOR = new Color(235, 98, 52);
    private static final Color DARK_TEXT_COLOR = new Color(235, 210, 52);

    private ImportantCommitsHighlighter(FileHistoryUi ui, @Nullable VcsLogData logData,
                                       VirtualFile root, @Nullable Set<Integer> commits) {
        myUi = ui;
        myLogData = logData;
        myRoot = root;
        myImportantCommits = commits;
    }

    public static ImportantCommitsHighlighter getInstance(FileHistoryUi ui, @Nullable VcsLogData logData,
                                                   VirtualFile root, @Nullable Set<Integer> commits) {
        if (INSTANCE == null) {
            INSTANCE = new ImportantCommitsHighlighter(ui, logData, root, commits);
        }
        return INSTANCE;
    }

    public static void disposeInstance() {
        INSTANCE = null;
    }

    @Override
    public @NotNull VcsCommitStyle getStyle(int commitId, @NotNull VcsShortCommitDetails commitDetails,
                                            boolean isSelected) {
        if (myImportantCommits.contains(commitId)) {
            VcsCommitStyle foregroundStyle = VcsCommitStyleFactory.foreground(new JBColor(REGULAR_TEXT_COLOR, DARK_TEXT_COLOR));
            return VcsCommitStyleFactory.combine(Arrays.asList(foregroundStyle, VcsCommitStyleFactory.bold()));
        }
        return VcsCommitStyle.DEFAULT;
    }

    @Override
    public void update(@NotNull VcsLogDataPack dataPack, boolean refreshHappened) {
        myImportantCommits = DiffAnalyzerService.getImportantCommits(myLogData.getProject(), myRoot, myUi);
    }
}
