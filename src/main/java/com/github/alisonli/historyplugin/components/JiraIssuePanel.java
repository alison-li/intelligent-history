package com.github.alisonli.historyplugin.components;

import com.intellij.openapi.Disposable;

import javax.swing.*;

public class JiraIssuePanel implements Disposable {
    private static final String DEFAULT_BODY =
            "If available for a commit, this is where Jira issue information will appear.\n" +
            "Please ensure your credentials are configured in plugin settings";
    private JPanel rootPanel;
    private JLabel title;
    private JTextPane bodyText;

    public JiraIssuePanel() {
        this.setTitle("");
        this.setBodyText(DEFAULT_BODY);
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
