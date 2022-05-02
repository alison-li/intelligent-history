package com.alisli.intelligenthistory.components;

import com.alisli.intelligenthistory.IntelligentHistoryBundle;
import com.intellij.ide.BrowserUtil;
import com.intellij.openapi.Disposable;
import com.intellij.util.ui.JBUI;
import icons.MyIcons;

import javax.swing.*;
import javax.swing.event.HyperlinkEvent;

public class JiraIssuePanel implements Disposable {
    private JPanel rootPanel;
    private JLabel title;
    private JTextPane bodyText;

    public JiraIssuePanel() {
        rootPanel.setBorder(JBUI.Borders.empty(8));
        rootPanel.setFocusable(true);
        title.setText("");
        bodyText.putClientProperty(JEditorPane.HONOR_DISPLAY_PROPERTIES, true);
        bodyText.setText(IntelligentHistoryBundle.message("ih.panel.startup.text"));
    }

    public void setTitle(String title) {
        this.title.setText(title);
        this.title.setIcon(MyIcons.Commit);
    }

    public void setBodyText(String bodyText) {
        this.bodyText.setContentType("text/html");
        this.bodyText.setText(bodyText);
        this.bodyText.addHyperlinkListener(e -> {
            if (e.getEventType() == HyperlinkEvent.EventType.ACTIVATED) {
                BrowserUtil.open(String.valueOf(e.getURL()));
            }
        });
    }

    public JPanel getContent() {
        return rootPanel;
    }

    @Override
    public void dispose() { }
}
