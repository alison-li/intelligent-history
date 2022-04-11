package com.github.alisonli.historyplugin.actions;

import com.github.alisonli.historyplugin.services.DiffAnalyzerService;
import com.github.difflib.patch.AbstractDelta;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Condition;
import com.intellij.openapi.vcs.*;
import com.intellij.openapi.vcs.actions.VcsContextUtil;
import com.intellij.openapi.vcs.changes.ContentRevision;
import com.intellij.openapi.vcs.changes.ui.ChangesViewContentManager;
import com.intellij.openapi.vcs.history.VcsFileRevision;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowManager;
import com.intellij.ui.content.TabDescriptor;
import com.intellij.ui.content.TabGroupId;
import com.intellij.util.ContentUtilEx;
import com.intellij.vcs.log.*;
import com.intellij.vcs.log.history.FileHistoryUi;
import com.intellij.vcs.log.history.FileHistoryUiFactory;
import com.intellij.vcs.log.impl.HashImpl;
import com.intellij.vcs.log.impl.VcsLogContentUtil;
import com.intellij.vcs.log.impl.VcsLogManager;
import com.intellij.vcs.log.impl.VcsProjectLog;
import com.intellij.vcs.log.ui.VcsLogPanel;
import com.intellij.vcs.log.util.VcsLogUtil;
import com.intellij.vcs.log.visible.VisiblePack;
import com.intellij.vcs.log.visible.filters.VcsLogFilterObject;
import com.intellij.vcsUtil.VcsUtil;
import git4idea.GitContentRevision;
import git4idea.GitFileRevision;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.util.*;
import java.util.function.Function;

public class IntelligentHistoryAction extends AnAction {
    private static final Logger LOG = Logger.getInstance(IntelligentHistoryAction.class);
    private TabGroupId tabGroupId =
            new TabGroupId("intelligent-history", () -> "Intelligent History", false);

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
        Collections.reverse(revisionList);
        List<String> filteredRevisionList = new ArrayList<>();
        if (revisionList != null && revisionList.size() > 1) {
            for (int i = 0; i < revisionList.size(); i++) {
                GitFileRevision rev1 = (GitFileRevision) revisionList.get(i);
                GitFileRevision rev2 = (GitFileRevision) revisionList.get(i + 1);
                ContentRevision contentRev1 = GitContentRevision.createRevision(filePath, rev1.getRevisionNumber(), project);
                ContentRevision contentRev2 = GitContentRevision.createRevision(filePath, rev2.getRevisionNumber(), project);
                try {
                    String beforeContent = contentRev1.getContent();
                    String afterContent = contentRev2.getContent();
                    // TODO: Call into `DiffAnalyzerService`, receive RevisionMetadata
                    List<AbstractDelta<String>> patch = diffAnalyzerService.getDiffBetweenRevisions(beforeContent, afterContent);
                    if (true) {
                        filteredRevisionList.add(rev2.getHash());
                        break;
                    }
                } catch (VcsException | IOException ex) {
                    ex.printStackTrace();
                }
            }
        }

        List<FilePath> selectedFiles = VcsContextUtil.selectedFilePaths(e.getDataContext());
        showFileHistory(project, selectedFiles, null, filteredRevisionList);
    }

    /**
     * Adapted from {@link com.intellij.vcs.log.history.VcsLogFileHistoryProviderImpl}
     */
    private void showFileHistory(Project project, Collection<FilePath> paths, @Nullable String revisionNumber,
                                 Collection<String> hashList) {
        if (paths.size() != 1) return;

        List<FilePath> pathList = new ArrayList<>(paths);

        VirtualFile root = VcsLogUtil.getActualRoot(project, pathList.get(0));
        FilePath path = getCorrectedPath(project, pathList.get(0), root, revisionNumber != null);
        if (path.isDirectory()) return;

        Hash hash = revisionNumber == null ? null : HashImpl.build(revisionNumber);

        VcsLogManager logManager = VcsProjectLog.getInstance(project).getLogManager();
        Condition<FileHistoryUi> cond = (FileHistoryUi ui) -> ui.matches(path, hash);
        FileHistoryUi fileHistoryUi = VcsLogContentUtil.findAndSelect(project, FileHistoryUi.class, cond);

        String suffix = hash != null ? " (" + hash.toShortString() + ")" : "";
        Function<FileHistoryUi, String> fun = (__) -> path.getName() + suffix;
        ToolWindow toolWindow = ToolWindowManager.getInstance(project).getToolWindow(ChangesViewContentManager.TOOLWINDOW_ID);
        if (fileHistoryUi != null) {
            FileHistoryUi intelligentFileHistoryUi = new FileHistoryUiFactory(path, root, hash).createLogUi(project, fileHistoryUi.getLogData());
            VcsLogFilterCollection filters = VcsLogFilterObject.collection(VcsLogFilterObject.fromHashes(hashList));
            VisiblePack visiblePack = new VisiblePack(fileHistoryUi.getDataPack().getDataPack(), fileHistoryUi.getDataPack().getVisibleGraph(), false, filters);
            intelligentFileHistoryUi.setVisiblePack(visiblePack);
            ContentUtilEx.addTabbedContent(
                    toolWindow.getContentManager(),
                    tabGroupId,
                    new TabDescriptor(new VcsLogPanel(logManager, intelligentFileHistoryUi), () -> fun.apply(intelligentFileHistoryUi), intelligentFileHistoryUi),
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