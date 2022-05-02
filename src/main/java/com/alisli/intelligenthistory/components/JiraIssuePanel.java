package com.alisli.intelligenthistory.components;

import com.alisli.intelligenthistory.IntelligentHistoryBundle;
import com.intellij.ide.BrowserUtil;
import com.intellij.openapi.Disposable;
import com.intellij.ui.components.ActionLink;
import com.intellij.uiDesigner.core.GridConstraints;
import com.intellij.util.ui.HTMLEditorKitBuilder;
import com.intellij.util.ui.JBUI;
import icons.MyIcons;

import javax.swing.*;


public class JiraIssuePanel implements Disposable {
    private JPanel rootPanel;
    private JLabel title;
    private JTextPane bodyText;

    public JiraIssuePanel() {
        rootPanel.setBorder(JBUI.Borders.empty(8));
        rootPanel.setFocusable(true);

        title.setText("");

        bodyText.putClientProperty(JEditorPane.HONOR_DISPLAY_PROPERTIES, true);
        bodyText.setEditorKit(new HTMLEditorKitBuilder().withWordWrapViewFactory().build());
        bodyText.setText(IntelligentHistoryBundle.message("ih.panel.startup.text"));
    }

    public void setTitle(String title) {
        this.title.setText(title);
        this.title.setIcon(MyIcons.Commit);
    }

    public void setBodyText(String bodyText) {
        this.bodyText.setText(bodyText);
    }

    public void createActionLink(String url) {
        ActionLink externalLink = new ActionLink(IntelligentHistoryBundle.message("ih.panel.external.jira.button.title"), e -> {
            BrowserUtil.browse(url);
        });
        externalLink.setExternalLinkIcon();
        GridConstraints constraints = new GridConstraints();
        constraints.setAnchor(GridConstraints.ANCHOR_SOUTHEAST);
        constraints.setUseParentLayout(true);
        rootPanel.add(externalLink, constraints);
    }

    public JPanel getContent() {
        return rootPanel;
    }

    @Override
    public void dispose() { }
}
