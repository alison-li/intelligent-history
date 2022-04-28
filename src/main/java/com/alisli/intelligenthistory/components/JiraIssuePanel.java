package com.alisli.intelligenthistory.components;

import com.alisli.intelligenthistory.IntelligentHistoryBundle;
import com.intellij.openapi.Disposable;

import javax.swing.*;

public class JiraIssuePanel implements Disposable {
    private JPanel rootPanel;
    private JLabel title;
    private JTextPane bodyText;

    public JiraIssuePanel() {
        this.setTitle("");
        this.setBodyText(IntelligentHistoryBundle.message("ih.panel.startup.text"));
    }

    public void setTitle(String title) {
        this.title.setText(title);
    }

    public void setBodyText(String bodyText) {
        this.bodyText.setText(bodyText);
    }

    public JPanel getContent() {
        return rootPanel;
    }

    @Override
    public void dispose() { }
}
