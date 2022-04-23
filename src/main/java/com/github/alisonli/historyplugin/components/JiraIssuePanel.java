package com.github.alisonli.historyplugin.components;

import javax.swing.*;

public class JiraIssuePanel {
    private static final String DEFAULT_BODY = "This is where Jira issue information will appear.";
    private JPanel rootPanel;
    private JLabel title;
    private JTextPane bodyText;
    private JScrollBar scrollBar;

    public JiraIssuePanel() {
        bodyText.setText(DEFAULT_BODY);
    }

    public void createUIComponents(String title, String bodyText) {
        this.title.setText(title);
        this.bodyText.setText(bodyText);
    }

    public JPanel getContent() {
        return rootPanel;
    }
}
