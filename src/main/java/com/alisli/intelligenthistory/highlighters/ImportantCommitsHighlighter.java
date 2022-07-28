package com.alisli.intelligenthistory.highlighters;

import com.alisli.intelligenthistory.services.DiffAnalyzerService;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.ui.Gray;
import com.intellij.ui.JBColor;
import com.intellij.vcs.log.*;
import com.intellij.vcs.log.data.VcsLogData;
import com.intellij.vcs.log.history.FileHistoryUi;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.Set;

public class ImportantCommitsHighlighter implements VcsLogHighlighter {
    public static final JBColor TRIVIAL_COMMIT_FOREGROUND = JBColor.namedColor(
            "VersionControl.Log.Commit.unmatchedForeground",
            new JBColor(Gray._128, Gray._96));
    private final FileHistoryUi myUi;
    private final VcsLogData myLogData;
    private final VirtualFile myRoot;
    private Set<Integer> myImportantCommits;

    public ImportantCommitsHighlighter(FileHistoryUi ui, @Nullable VcsLogData logData,
                                       VirtualFile root, @Nullable Set<Integer> commits) {
        myUi = ui;
        myLogData = logData;
        myRoot = root;
        myImportantCommits = commits;
    }

    @Override
    public @NotNull VcsCommitStyle getStyle(int commitId, @NotNull VcsShortCommitDetails commitDetails,
                                            int column, boolean isSelected) {
        if (!myImportantCommits.contains(commitId)) {
            return VcsCommitStyleFactory.foreground(TRIVIAL_COMMIT_FOREGROUND);
        }
        return VcsCommitStyle.DEFAULT;
    }

    @Override
    public void update(@NotNull VcsLogDataPack dataPack, boolean refreshHappened) {
        myImportantCommits = DiffAnalyzerService.getImportantCommits(Objects.requireNonNull(myLogData).getProject(), myRoot, myUi);
    }
}
