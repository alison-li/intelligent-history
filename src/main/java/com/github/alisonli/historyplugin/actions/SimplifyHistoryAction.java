package com.github.alisonli.historyplugin.actions;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Condition;
import com.intellij.openapi.vcs.FilePath;
import com.intellij.openapi.vcs.actions.VcsContextUtil;
import com.intellij.openapi.vcs.changes.ui.ChangesViewContentManager;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowManager;
import com.intellij.ui.content.TabDescriptor;
import com.intellij.ui.content.TabGroupId;
import com.intellij.util.ContentUtilEx;
import com.intellij.vcs.log.Hash;
import com.intellij.vcs.log.VcsLogDataKeys;
import com.intellij.vcs.log.VcsLogUi;
import com.intellij.vcs.log.history.FileHistoryUi;
import com.intellij.vcs.log.impl.HashImpl;
import com.intellij.vcs.log.impl.VcsLogContentUtil;
import com.intellij.vcs.log.impl.VcsLogManager;
import com.intellij.vcs.log.impl.VcsProjectLog;
import com.intellij.vcs.log.ui.VcsLogPanel;
import com.intellij.vcs.log.util.VcsLogUtil;
import com.intellij.vcsUtil.VcsUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.Function;

public class SimplifyHistoryAction extends AnAction {

    private static final Logger LOG = Logger.getInstance(SimplifyHistoryAction.class);
    private TabGroupId tabGroupId =
            new TabGroupId("intelligent-history", () -> "Intelligent History", false);

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
//        VcsKey vcsKey = Objects.requireNonNull(e.getData(VcsDataKeys.VCS));
//        FilePath filePath = Objects.requireNonNull(e.getData(VcsDataKeys.FILE_PATH));
//
//        DiffAnalyzerService diffAnalyzerService = new DiffAnalyzerService(project);
//
//        List<VcsFileRevision> revisionList = diffAnalyzerService.getFileHistoryRevisionList(vcsKey, filePath);
//        // TODO Populate these lists?
//        List<VcsFileRevision> trivialRevisionList = new ArrayList<>();
//        List<VcsFileRevision> filteredRevisionList = new ArrayList<>();
//        if (revisionList != null && revisionList.size() > 1) {
//            try {
//                for (int i = 0; i < revisionList.size() - 1; i++) {
//                    // Assume the VCS is git
//                    GitFileRevision rev1 = (GitFileRevision) revisionList.get(i);
//                    GitFileRevision rev2 = (GitFileRevision) revisionList.get(i + 1);
//                    List<Change> changes = diffAnalyzerService.getChangesBetweenRevisions(filePath, rev1, rev2);
//                    for (Change change : changes) {
//                        Change.Type type = change.getType();
//                        ContentRevision contentRevisionBefore = change.getBeforeRevision();
//                        ContentRevision contentRevisionAfter = change.getAfterRevision();
//                        try {
//                            String beforeContent = contentRevisionBefore.getContent();
//                            String afterContent = contentRevisionAfter.getContent();
//                            // TODO: Call into service
//                        } catch (VcsException ex) {
//                            ex.printStackTrace();
//                        }
//                    }
//                }
//            } catch (VcsException ex) {
//                ex.printStackTrace();
//            }
//        }

        // Version 1
//        VcsLogUi logUi = e.getData(VcsLogDataKeys.VCS_LOG_UI);
//        VcsLogFilterCollection filters;
//        if (Registry.is("vcs.log.copy.filters.to.new.tab") && logUi != null) {
//            filters = logUi.getFilterUi().getFilters();
//        }
//        else {
//            filters = VcsLogFilterObject.collection();
//        }
//
//        MainVcsLogUi ui = VcsProjectLog.getInstance(project).openLogTab(filters);
//        ui.getChangesBrowser().add(new JLabel("ldaf"));

        // Version 2 Working with James to get file history
        List<FilePath> selectedFiles = VcsContextUtil.selectedFilePaths(e.getDataContext());
        showFileHistory(project, selectedFiles, null);
    }

    private void showFileHistory(Project project, Collection<FilePath> paths, @Nullable String revisionNumber) {
        if (paths.size() != 1) return;

        List<FilePath> pathList = new ArrayList<>(paths);

        VirtualFile root = VcsLogUtil.getActualRoot(project, pathList.get(0));
        FilePath path = getCorrectedPath(project, pathList.get(0), root, revisionNumber != null);
        if (path.isDirectory()) return;

        Hash hash = revisionNumber == null ? null : HashImpl.build(revisionNumber);

        VcsLogManager logManager = VcsProjectLog.getInstance(project).getLogManager();
        Condition<FileHistoryUi> cond = (FileHistoryUi ui) -> ui.matches(path, hash);
        FileHistoryUi fileHistoryUi = VcsLogContentUtil.findAndSelect(project, FileHistoryUi.class,  cond);

        String suffix = hash != null ? " (" + hash.toShortString() + ")" : "";
        Function<FileHistoryUi, String> fun = (__) -> path.getName() + suffix;
        ToolWindow toolWindow = ToolWindowManager.getInstance(project).getToolWindow(ChangesViewContentManager.TOOLWINDOW_ID);
        if (fileHistoryUi != null) {
            // TODO: for now, I'm passing in the same `fileHistoryUi`. You'll likely need to create a component/modify it to your needs.
            ContentUtilEx.addTabbedContent(
                    toolWindow.getContentManager(),
                    tabGroupId,
                    new TabDescriptor(new VcsLogPanel(logManager, fileHistoryUi),() -> fun.apply(fileHistoryUi), fileHistoryUi),
                    true
            );
            toolWindow.activate(null);
        }
    }

    private FilePath getCorrectedPath(Project project, FilePath path, VirtualFile root, boolean isRevisionHistory) {
        FilePath correctedPath = path;
        if (root.equals(VcsUtil.getVcsRootFor(project, correctedPath)) && correctedPath.isDirectory()) {
            correctedPath = VcsUtil.getFilePath(correctedPath.getPath(), false);
        }
        if (!isRevisionHistory) {
            return VcsUtil.getLastCommitPath(project, correctedPath);
        }
        return correctedPath;
    }
}