package com.alisli.intelligenthistory.components;

import com.alisli.intelligenthistory.IntelligentHistoryBundle;
import com.intellij.openapi.Disposable;
import com.intellij.util.ui.JBUI;
import icons.MyIcons;

import javax.swing.*;

public class JiraIssuePanel implements Disposable {
    private JPanel rootPanel;
    private JLabel title;
    private JEditorPane bodyText;

    public JiraIssuePanel() {
        rootPanel.setBorder(JBUI.Borders.empty(8));
        title.setText("");
        bodyText.setText(IntelligentHistoryBundle.message("ih.panel.startup.text"));
    }

    public void setTitle(String title) {
        this.title.setText(title);
        this.title.setIcon(MyIcons.Commit);
    }

    public void setBodyText(String bodyText) {
        this.bodyText.setContentType("text/html");
        this.bodyText.setText(bodyText);
    }

    public JPanel getContent() {
        return rootPanel;
    }

    @Override
    public void dispose() { }
}
