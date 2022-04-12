package com.github.alisonli.historyplugin.services;

import com.github.alisonli.historyplugin.model.RevisionMetadata;
import com.github.difflib.DiffUtils;
import com.github.difflib.patch.AbstractDelta;
import com.github.difflib.patch.Patch;
import com.intellij.openapi.components.Service;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vcs.*;
import com.intellij.openapi.vcs.changes.ContentRevision;
import com.intellij.openapi.vcs.history.VcsFileRevision;
import com.intellij.openapi.vcs.history.VcsHistoryProvider;
import com.intellij.openapi.vcs.history.VcsHistorySession;
import com.intellij.vcs.log.Hash;
import com.intellij.vcs.log.graph.VisibleGraph;
import com.intellij.vcs.log.history.FileHistoryPaths;
import com.intellij.vcs.log.history.FileHistoryUi;
import com.intellij.vcs.log.ui.VcsLogInternalDataKeys;
import git4idea.log.GitLogDiffHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Project service for analyzing diffs between revisions.
 */
@Service
public final class DiffAnalyzerService {
    private final Project project;

    public DiffAnalyzerService(Project project) {
        this.project = project;
    }

    public List<ContentRevision> getFileHistoryRevisionList(FileHistoryUi logUi) {
        List<ContentRevision> revisionList = new ArrayList<>();
        GitLogDiffHandler diffHandler = new GitLogDiffHandler(this.project);
        VisibleGraph<Integer> visibleGraph = logUi.getDataPack().getVisibleGraph();
        int rowsCount = visibleGraph.getVisibleCommitCount();
        for (int i = 0; i < rowsCount; i++) {
            Integer commitId = visibleGraph.getRowInfo(i).getCommit();
            FilePath filePath = FileHistoryPaths.filePath(logUi.getDataPack(), commitId);
            Hash commitHash = logUi.getLogData()
                    .getMiniDetailsGetter()
                    .getCommitData(commitId, Collections.singleton(commitId)).getId();
            ContentRevision contentRevision = diffHandler.createContentRevision(filePath, commitHash);
            revisionList.add(contentRevision);
        }
        return revisionList;
    }

    public RevisionMetadata getRevisionMetadata(String beforeContent, String afterContent) {
        List<AbstractDelta<String>> deltas = getDiffBetweenRevisions(beforeContent, afterContent);
        for (AbstractDelta<String> delta : deltas) {
            // TODO
        }
        return null;
    }

    private List<AbstractDelta<String>> getDiffBetweenRevisions(String beforeContent, String afterContent) {
        List<String> left = new ArrayList<>(Collections.singleton(""));
        if (beforeContent != null) {
            left = List.of(beforeContent.split("\n"));
        }
        List<String> right = new ArrayList<>(Collections.singleton(""));
        if (afterContent != null) {
            right = List.of(afterContent.split("\n"));
        }
        Patch<String> patch = DiffUtils.diff(left, right);
        return patch.getDeltas();
    }
}