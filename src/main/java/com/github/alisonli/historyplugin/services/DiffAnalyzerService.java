package com.github.alisonli.historyplugin.services;

import com.github.alisonli.historyplugin.model.RevisionDiffMetadata;
import com.github.difflib.DiffUtils;
import com.github.difflib.patch.AbstractDelta;
import com.github.difflib.patch.DeltaType;
import com.github.difflib.patch.Patch;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.text.LineTokenizer;
import com.intellij.openapi.vcs.*;
import com.intellij.openapi.vcs.changes.ContentRevision;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.vcs.log.Hash;
import com.intellij.vcs.log.data.VcsLogStorage;
import com.intellij.vcs.log.graph.VisibleGraph;
import com.intellij.vcs.log.history.FileHistoryPaths;
import com.intellij.vcs.log.history.FileHistoryUi;
import com.intellij.vcs.log.impl.HashImpl;
import git4idea.GitRevisionNumber;
import git4idea.log.GitLogDiffHandler;

import java.util.*;

/**
 * Project service for analyzing diffs between revisions.
 */
public final class DiffAnalyzerService {
    private static final Logger LOG = Logger.getInstance(DiffAnalyzerService.class);

    public static Set<Integer> getImportantCommits(Project project, VirtualFile root, FileHistoryUi logUi) {
        Set<Integer> importantCommits = new HashSet<>();
        VcsLogStorage logStorage = logUi.getLogData().getStorage();
        List<ContentRevision> contentRevisions = getFileHistoryRevisionList(project, logUi);
        // The revisions are ordered from most recent to the oldest commit.
        // Need to reverse the order when considering `before` and `after` versions of a file.
        Collections.reverse(contentRevisions);
        for (int i = 0; i < contentRevisions.size() - 1; i++) {
            ContentRevision contentRev1 = contentRevisions.get(i);
            ContentRevision contentRev2 = contentRevisions.get(i + 1);
            try {
                String beforeContent = contentRev1.getContent();
                String afterContent = contentRev2.getContent();
                RevisionDiffMetadata metadata = getRevisionMetadata(beforeContent, afterContent);
                if (metadata.getOther() >= 1) {
                    Hash hash = HashImpl.build(
                            ((GitRevisionNumber) contentRev2.getRevisionNumber()).getRev()
                    );
                    int commitIndex = logStorage.getCommitIndex(hash, root);
                    importantCommits.add(commitIndex);
                }
            } catch (VcsException ex) {
                LOG.error(ex);
            }
        }
        return importantCommits;
    }

    private static List<ContentRevision> getFileHistoryRevisionList(Project project, FileHistoryUi logUi) {
        List<ContentRevision> revisionList = new ArrayList<>();
        GitLogDiffHandler diffHandler = new GitLogDiffHandler(project);
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

    private static RevisionDiffMetadata getRevisionMetadata(String beforeContent, String afterContent) {
        List<AbstractDelta<String>> deltas = getDeltas(beforeContent, afterContent);
        RevisionDiffMetadata revisionMetadata = new RevisionDiffMetadata(beforeContent, afterContent);
        for (AbstractDelta<String> delta : deltas) {
            RevisionDiffMetadata tempMetadata = new RevisionDiffMetadata(beforeContent, afterContent);
            DeltaType deltaType = delta.getType();
            if (deltaType == DeltaType.CHANGE) {
                List<String> sourceLines = delta.getSource().getLines();
                List<String> targetLines = delta.getTarget().getLines();
                RevisionDiffMetadata sourceMetadata = evaluateDeltaByLine(sourceLines);
                RevisionDiffMetadata targetMetadata = evaluateDeltaByLine(targetLines);
                tempMetadata.setDocs(Math.max(sourceMetadata.getDocs(), targetMetadata.getDocs()));
                tempMetadata.setAnnotations(Math.max(sourceMetadata.getAnnotations(), targetMetadata.getAnnotations()));
                tempMetadata.setImports(Math.max(sourceMetadata.getImports(), targetMetadata.getImports()));
                tempMetadata.setNewlines(Math.max(sourceMetadata.getNewlines(), targetMetadata.getNewlines()));
                tempMetadata.setOther(Math.max(sourceMetadata.getOther(), targetMetadata.getOther()));
            } else if (deltaType == DeltaType.DELETE || deltaType == DeltaType.INSERT) {
                List<String> lineList = deltaType == DeltaType.DELETE ? delta.getSource().getLines() : delta.getTarget().getLines();
                tempMetadata = evaluateDeltaByLine(lineList);
            }
            revisionMetadata.mergeMetadata(tempMetadata);
        }
        return revisionMetadata;
    }

    private static RevisionDiffMetadata evaluateDeltaByLine(List<String> lineList) {
        int numDocs = 0, numAnnotations = 0, numImports = 0, numNewLines = 0, numOther = 0;
        for (String line : lineList) {
            if (line.matches("(.*)\\*(.*)")
                    || line.matches("(.*)/\\*(.*)")
                    || line.matches("(.*)//(.*)")) {
                numDocs++;
            } else if (line.matches("(.*)import(.*)")) {
                numImports++;
            } else if (line.matches("(.*)@[A-Za-z]+(.*)")) {
                numAnnotations++;
            } else if (line.isEmpty()) {
                numNewLines++;
            } else {
                numOther++;
            }
        }
        return new RevisionDiffMetadata("", "", numDocs, numAnnotations, numImports, numNewLines, numOther);
    }

    private static List<AbstractDelta<String>> getDeltas(String beforeContent, String afterContent) {
        List<String> left = new ArrayList<>(Collections.singleton(""));
        if (beforeContent != null) {
            left = LineTokenizer.tokenizeIntoList(beforeContent, false);
        }
        List<String> right = new ArrayList<>(Collections.singleton(""));
        if (afterContent != null) {
            right = LineTokenizer.tokenizeIntoList(afterContent, false);
        }
        Patch<String> patch = DiffUtils.diff(left, right);
        return patch.getDeltas();
    }
}