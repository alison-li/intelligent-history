package com.github.alisonli.historyplugin.actions;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vcs.*;
import com.intellij.openapi.vcs.history.VcsFileRevision;
import com.intellij.openapi.vcs.history.VcsHistoryProvider;
import com.intellij.openapi.vcs.history.VcsHistorySession;
import com.intellij.vcs.log.VcsLogDataKeys;
import com.intellij.vcs.log.VcsLogUi;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Objects;

public class SimplifyHistoryAction extends AnAction {
    /**
     * Enable action only when a project and a file from the project is currently open.
     *
     * @param e Carries information on the invocation place.
     */
    @Override
    public void update(@NotNull AnActionEvent e) {
        Project project = e.getProject();
        VcsLogUi ui = e.getData(VcsLogDataKeys.VCS_LOG_UI);
        e.getPresentation().setEnabledAndVisible(project != null && ui != null);
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        Project project = Objects.requireNonNull(e.getProject());
        VcsKey vcsKey = Objects.requireNonNull(e.getData(VcsDataKeys.VCS));
        FilePath filePath = Objects.requireNonNull(e.getData(VcsDataKeys.FILE_PATH));

        List<VcsFileRevision> revisionList = getFileHistoryRevisionList(project, vcsKey, filePath);
    }

    @Nullable
    private static List<VcsFileRevision> getFileHistoryRevisionList(@NotNull Project project, @NotNull VcsKey vcsKey,
                                                                    @NotNull FilePath filePath) {
        AbstractVcs vcs = Objects.requireNonNull(getVcs(project, vcsKey));
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
    private static AbstractVcs getVcs(@NotNull Project project, @Nullable VcsKey vcsKey) {
        return vcsKey == null ? null : ProjectLevelVcsManager.getInstance(project).findVcsByName(vcsKey.getName());
    }
}
