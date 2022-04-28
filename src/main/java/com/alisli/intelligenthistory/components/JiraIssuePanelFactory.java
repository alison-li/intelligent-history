package com.alisli.intelligenthistory.components;

import com.intellij.openapi.project.DumbAware;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowFactory;
import com.intellij.ui.components.JBScrollPane;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentFactory;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

public class JiraIssuePanelFactory implements ToolWindowFactory, DumbAware {
    @Override
    public void createToolWindowContent(@NotNull Project project, @NotNull ToolWindow toolWindow) {
        JiraIssuePanel toolWindowBuilder = new JiraIssuePanel();
        JBScrollPane toolWindowContent = new JBScrollPane(toolWindowBuilder.getContent());
        toolWindowContent.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        ContentFactory contentFactory = ContentFactory.SERVICE.getInstance();
        Content content = contentFactory.createContent(toolWindowContent, "Description", false);
        content.setPreferredFocusableComponent(toolWindowContent);
        content.setDisposer(toolWindowBuilder);
        toolWindow.getContentManager().addContent(content);
    }
}
