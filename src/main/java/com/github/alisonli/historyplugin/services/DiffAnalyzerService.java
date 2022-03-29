package com.github.alisonli.historyplugin.services;

import com.intellij.openapi.components.Service;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vcs.*;
import com.intellij.openapi.vcs.changes.Change;
import com.intellij.openapi.vcs.history.VcsFileRevision;
import com.intellij.openapi.vcs.history.VcsHistoryProvider;
import com.intellij.openapi.vcs.history.VcsHistorySession;
import com.intellij.openapi.vfs.VirtualFile;
import git4idea.GitFileRevision;
import git4idea.GitUtil;
import git4idea.changes.GitChangeUtils;
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

    /**
     * Adapted from {@link git4idea.history.GitDiffFromHistoryHandler}
     *
     */
    public List<Change> getChangesBetweenRevisions(@NotNull FilePath path, @NotNull GitFileRevision rev1,
                                                    @Nullable GitFileRevision rev2)
            throws VcsException {
        VirtualFile root = GitUtil.getRootForFile(this.project, path);
        String hash1 = rev1.getHash();

        if (rev2 == null) {
            return new ArrayList<>(GitChangeUtils.getDiffWithWorkingDir(this.project, root, hash1,
                    Collections.singleton(path), false));
        }

        String hash2 = rev2.getHash();
        return new ArrayList<>(GitChangeUtils.getDiff(this.project, root, hash1, hash2,
                Collections.singletonList(path)));
    }

    @Nullable
    public List<VcsFileRevision> getFileHistoryRevisionList(@NotNull VcsKey vcsKey, @NotNull FilePath filePath) {
        AbstractVcs vcs = Objects.requireNonNull(getVcs(vcsKey));
        VcsHistoryProvider historyProvider = Objects.requireNonNull(vcs.getVcsHistoryProvider());
        try {
            VcsHistorySession historySession = Objects.requireNonNull(historyProvider.createSessionFor(filePath));
            return historySession.getRevisionList();
        } catch (VcsException ex) {
            ex.printStackTrace();
        }
        return null;
    }

    @Nullable
    private AbstractVcs getVcs(@Nullable VcsKey vcsKey) {
        return vcsKey == null ? null : ProjectLevelVcsManager.getInstance(this.project).findVcsByName(vcsKey.getName());
    }
}
