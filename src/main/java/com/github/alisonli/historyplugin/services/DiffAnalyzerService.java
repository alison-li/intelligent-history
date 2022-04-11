package com.github.alisonli.historyplugin.services;

import com.github.difflib.DiffUtils;
import com.github.difflib.patch.AbstractDelta;
import com.github.difflib.patch.Patch;
import com.intellij.openapi.components.Service;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vcs.*;
import com.intellij.openapi.vcs.history.VcsFileRevision;
import com.intellij.openapi.vcs.history.VcsHistoryProvider;
import com.intellij.openapi.vcs.history.VcsHistorySession;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
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

    public List<AbstractDelta<String>> getDiffBetweenRevisions(String beforeContent, String afterContent) throws IOException {
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