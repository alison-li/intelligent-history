package com.github.alisonli.historyplugin.actions;

import com.github.alisonli.historyplugin.model.RevisionDiffMetadata;
import com.github.alisonli.historyplugin.services.DiffAnalyzerService;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.popup.Balloon;
import com.intellij.openapi.ui.popup.JBPopupFactory;
import com.intellij.openapi.ui.popup.JBPopupListener;
import com.intellij.openapi.ui.popup.LightweightWindowEvent;
import com.intellij.openapi.util.Disposer;
import com.intellij.openapi.vcs.VcsException;
import com.intellij.ui.Gray;
import com.intellij.ui.JBColor;
import com.intellij.ui.awt.RelativePoint;
import com.intellij.ui.components.JBLabel;
import com.intellij.vcs.log.VcsFullCommitDetails;
import com.intellij.vcs.log.data.DataGetter;
import com.intellij.vcs.log.history.FileHistoryUi;
import com.intellij.vcs.log.ui.actions.history.FileHistorySingleCommitAction;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.util.List;

public class ShowDiffMetadataAction extends FileHistorySingleCommitAction<VcsFullCommitDetails> {
    private static final Logger LOG = Logger.getInstance(ShowDiffMetadataAction.class);
    private static final JBColor BACKGROUND = new JBColor(Gray._252,   new Color(49, 52, 53));
    private static final JBColor BORDER = new JBColor(Gray._252,   new Color(49, 52, 53));

    @Override
    protected @NotNull List<VcsFullCommitDetails> getSelection(@NotNull FileHistoryUi ui) {
        return ui.getVcsLog().getSelectedDetails();
    }

    @Override
    protected @NotNull DataGetter<VcsFullCommitDetails> getDetailsGetter(@NotNull FileHistoryUi ui) {
        return ui.getLogData().getCommitDetailsGetter();
    }

    @Override
    protected void performAction(@NotNull Project project, @NotNull FileHistoryUi ui,
                                 @NotNull VcsFullCommitDetails detail, @NotNull AnActionEvent e) {
        try {
            int id = ui.getLogData().getStorage().getCommitIndex(detail.getId(), detail.getRoot());
            RevisionDiffMetadata diffMetadata = DiffAnalyzerService.getRevisionMetadataForId(project, detail.getRoot(), ui, id);
            JBLabel popupContent = new JBLabel("<html>" + diffMetadata + "</html>");
            Balloon balloon = JBPopupFactory.getInstance()
                    .createBalloonBuilder(popupContent)
                    .setTitle("Metadata for Commit " + detail.getId().toShortString())
                    .setFillColor(BACKGROUND)
                    .setBorderColor(BORDER)
                    .setAnimationCycle(200)
                    .setCloseButtonEnabled(true)
                    .setHideOnCloseClick(true)
                    .createBalloon();
            balloon.addListener(new JBPopupListener() {
                @Override
                public void onClosed(@NotNull LightweightWindowEvent event) {
                    if (!balloon.isDisposed()) {
                        Disposer.dispose(balloon);
                    }
                }
            });
            balloon.show(RelativePoint.getNorthWestOf(ui.getMainComponent()), Balloon.Position.above);
        } catch (VcsException ex) {
            LOG.error(e);
        }
    }
}
