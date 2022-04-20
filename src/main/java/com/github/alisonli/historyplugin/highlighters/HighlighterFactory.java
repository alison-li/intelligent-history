package com.github.alisonli.historyplugin.highlighters;

import com.github.alisonli.historyplugin.services.DiffAnalyzerService;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.vcs.log.VcsLogHighlighter;
import com.intellij.vcs.log.data.VcsLogData;
import com.intellij.vcs.log.history.FileHistoryUi;
import com.intellij.vcs.log.impl.VcsProjectLog;

import java.util.Objects;
import java.util.Set;

public class HighlighterFactory {
    private final Project myProject;
    private final VirtualFile myRoot;
    private final FileHistoryUi myUi;

    public HighlighterFactory(Project project, VirtualFile root, FileHistoryUi logUi) {
        myProject = project;
        myRoot = root;
        myUi = logUi;
    }

    public VcsLogHighlighter getHighlighter(Type type) {
        // Can switch highlighter types based on input type when more highlighters are added
        VcsLogData logData = Objects.requireNonNull(VcsProjectLog.getInstance(myProject).getLogManager()).getDataManager();
        Set<Integer> importantCommits = DiffAnalyzerService.getImportantCommits(myProject, myRoot, myUi);
        return ImportantCommitsHighlighter.getInstance(myUi, logData, myRoot, importantCommits);
    }

    public enum Type {
        IMPORTANT
    }
}
