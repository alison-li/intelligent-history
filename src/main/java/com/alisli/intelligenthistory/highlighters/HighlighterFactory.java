package com.alisli.intelligenthistory.highlighters;

import com.alisli.intelligenthistory.services.DiffAnalyzerService;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vcs.FilePath;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.vcs.log.VcsLogHighlighter;
import com.intellij.vcs.log.data.VcsLogData;
import com.intellij.vcs.log.history.FileHistoryUi;
import com.intellij.vcs.log.impl.VcsProjectLog;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

public class HighlighterFactory {
    private static final Map<FilePath, VcsLogHighlighter> HIGHLIGHTER_CACHE = new HashMap<>();

    private HighlighterFactory() {
    }

    public static VcsLogHighlighter getHighlighter(Project project, VirtualFile root, FilePath path,
                                                   FileHistoryUi logUi) {
        return HIGHLIGHTER_CACHE.computeIfAbsent(path, k -> {
            VcsLogData logData = Objects.requireNonNull(VcsProjectLog.getInstance(project).getLogManager()).getDataManager();
            Set<Integer> importantCommits = DiffAnalyzerService.getImportantCommits(project, root, logUi);
            return new ImportantCommitsHighlighter(logUi, logData, root, importantCommits);
        });
    }

    public static void disposeHighlighter(FilePath path) {
        HIGHLIGHTER_CACHE.remove(path);
    }
}
