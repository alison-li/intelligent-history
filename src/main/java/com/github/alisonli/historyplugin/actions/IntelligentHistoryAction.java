package com.github.alisonli.historyplugin.actions;

import com.github.alisonli.historyplugin.model.RevisionMetadata;
import com.github.alisonli.historyplugin.services.DiffAnalyzerService;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
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
import com.intellij.vcs.log.data.DataPack;
import com.intellij.vcs.log.data.DataPackBase;
import com.intellij.vcs.log.data.VcsLogStorage;
import com.intellij.vcs.log.graph.PermanentGraph;
import com.intellij.vcs.log.graph.VisibleGraph;
import com.intellij.vcs.log.history.FileHistoryUi;
import com.intellij.vcs.log.history.FileHistoryUiFactory;
import com.intellij.vcs.log.impl.HashImpl;
import com.intellij.vcs.log.impl.VcsLogManager;
import com.intellij.vcs.log.impl.VcsProjectLog;
import com.intellij.vcs.log.ui.VcsLogInternalDataKeys;
import com.intellij.vcs.log.ui.VcsLogPanel;
import com.intellij.vcs.log.util.VcsLogUtil;
import com.intellij.vcs.log.visible.VisiblePack;
import com.intellij.vcs.log.visible.filters.VcsLogFilterObject;
import com.intellij.vcsUtil.VcsUtil;
import git4idea.GitContentRevision;
import git4idea.GitFileRevision;
import git4idea.GitRevisionNumber;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

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
        FileHistoryUi logUi = e.getRequiredData(VcsLogInternalDataKeys.FILE_HISTORY_UI);

        DiffAnalyzerService diffAnalyzerService = new DiffAnalyzerService(project);

        List<ContentRevision> contentRevisions = diffAnalyzerService.getFileHistoryRevisionList(logUi);

        // The revisions are ordered from most recent to oldest commit.
        // Need to reverse the order when considering `before` and `after` versions of a file.
        Collections.reverse(contentRevisions);
        List<String> filteredRevisionList = new ArrayList<>();
        if (contentRevisions != null && contentRevisions.size() > 1) {
            for (int i = 0; i < contentRevisions.size(); i++) {
                ContentRevision contentRev1 = contentRevisions.get(i);
                ContentRevision contentRev2 = contentRevisions.get(i + 1);
                try {
                    String beforeContent = contentRev1.getContent();
                    String afterContent = contentRev2.getContent();
                    RevisionMetadata metadata = diffAnalyzerService.getRevisionMetadata(beforeContent, afterContent);
                    // TODO: Use metadata to add to `filteredRevisionList`
                    if (true) {
                        filteredRevisionList.add(((GitRevisionNumber) contentRev2.getRevisionNumber()).getRev());
                        break;
                    }
                } catch (VcsException ex) {
                    ex.printStackTrace();
                }
            }
        }

        List<FilePath> selectedFiles = VcsContextUtil.selectedFilePaths(e.getDataContext());
        showFileHistory(project, logUi, selectedFiles, null, filteredRevisionList);
    }

    /**
     * Adapted from {@link com.intellij.vcs.log.history.VcsLogFileHistoryProviderImpl}
     */
    private void showFileHistory(Project project, FileHistoryUi originalUi, List<FilePath> paths,
                                 @Nullable String revisionNumber,
                                 Collection<String> hashes) {
        if (paths.size() != 1) return;

        VirtualFile root = VcsLogUtil.getActualRoot(project, paths.get(0));
        FilePath path = getCorrectedPath(project, paths.get(0), root, revisionNumber != null);
        if (path.isDirectory()) return;

        Hash hash = revisionNumber == null ? null : HashImpl.build(revisionNumber);

        String suffix = hash != null ? " (" + hash.toShortString() + ")" : "";
        Function<FileHistoryUi, String> fun = (__) -> path.getName() + suffix;
        ToolWindow toolWindow = ToolWindowManager.getInstance(project).getToolWindow(ChangesViewContentManager.TOOLWINDOW_ID);

        // TODO: Use my own factory?
        FileHistoryUi intelligentFileHistoryUi = new FileHistoryUiFactory(path, root, hash).createLogUi(project, originalUi.getLogData());
        // TODO: Need to look into this filter collection
        VcsLogFilterCollection filters = VcsLogFilterObject.collection(VcsLogFilterObject.fromHashes(hashes));

        VcsLogManager logManager = VcsProjectLog.getInstance(project).getLogManager();

        Set<Integer> matchedCommits = new HashSet<>();
        for (String s : hashes) {
            Hash h = HashImpl.build(s);
            VcsLogStorage logStorage = logManager.getDataManager().getStorage();
            matchedCommits.add(logStorage.getCommitIndex(h, root));
        }

        DataPackBase dataPackBase = originalUi.getDataPack().getDataPack();
        PermanentGraph<Integer> permanentGraph = ((DataPack) dataPackBase).getPermanentGraph();
        VisibleGraph<Integer> visibleGraph = permanentGraph.createVisibleGraph(PermanentGraph.SortType.Normal,
                null, matchedCommits);
        VisiblePack visiblePack = new VisiblePack(dataPackBase, visibleGraph, false, filters);
        intelligentFileHistoryUi.setVisiblePack(visiblePack);

        ContentUtilEx.addTabbedContent(
                toolWindow.getContentManager(),
                tabGroupId,
                new TabDescriptor(new VcsLogPanel(logManager, intelligentFileHistoryUi),
                        () -> fun.apply(intelligentFileHistoryUi), intelligentFileHistoryUi),
                true
        );
        toolWindow.activate(null);
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