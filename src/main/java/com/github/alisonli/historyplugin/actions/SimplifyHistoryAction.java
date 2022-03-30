package com.github.alisonli.historyplugin.actions;

import com.github.alisonli.historyplugin.services.DiffAnalyzerService;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vcs.FilePath;
import com.intellij.openapi.vcs.VcsDataKeys;
import com.intellij.openapi.vcs.VcsException;
import com.intellij.openapi.vcs.VcsKey;
import com.intellij.openapi.vcs.changes.Change;
import com.intellij.openapi.vcs.changes.ContentRevision;
import com.intellij.openapi.vcs.history.VcsFileRevision;
import com.intellij.vcs.log.VcsLogDataKeys;
import com.intellij.vcs.log.VcsLogUi;
import git4idea.GitFileRevision;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class SimplifyHistoryAction extends AnAction {

    private static final Logger LOG = Logger.getInstance(SimplifyHistoryAction.class);

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

        DiffAnalyzerService diffAnalyzerService = new DiffAnalyzerService(project);

        List<VcsFileRevision> revisionList = diffAnalyzerService.getFileHistoryRevisionList(vcsKey, filePath);
        List<List<Change>> changesList = new ArrayList<>();
        if (revisionList != null && revisionList.size() > 1) {
            try {
                for (int i = 0; i < revisionList.size() - 1; i++) {
                    // Assume the VCS is git
                    GitFileRevision rev1 = (GitFileRevision) revisionList.get(i);
                    GitFileRevision rev2 = (GitFileRevision) revisionList.get(i + 1);
                    List<Change> changes = diffAnalyzerService.getChangesBetweenRevisions(filePath, rev1, rev2);
                    changesList.add(changes);
                }
            } catch (VcsException ex) {
                ex.printStackTrace();
            }
        }

        for (List<Change> changes : changesList) {
            Change change = changes.get(0);
            Change.Type type = change.getType();
            ContentRevision contentRevisionBefore = change.getBeforeRevision();
            ContentRevision contentRevisionAfter = change.getAfterRevision();
            try {
                // TODO: Now we have retrieved the before and after as strings - do something with them.
                String beforeContent = contentRevisionBefore.getContent();
                String afterContent = contentRevisionAfter.getContent();
            } catch (VcsException ex) {
                ex.printStackTrace();
            }
        }
    }
}
