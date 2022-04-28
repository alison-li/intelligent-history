package com.alisli.intelligenthistory.settings;

import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.options.SearchableConfigurable;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

public class JiraConfigurable implements SearchableConfigurable {
    private final Project project;
    private JiraSettingsComponent settings;

    public JiraConfigurable(Project project) {
        this.project = project;
    }

    @Override
    public @NotNull @NonNls String getId() {
        return "com.alisli.intelligenthistory";
    }

    @Override
    public String getDisplayName() {
        return "Intelligent History";
    }

    @Override
    public @Nullable JComponent createComponent() {
        settings = new JiraSettingsComponent();
        settings.createUIComponents(project);
        return settings.getContent();
    }

    @Override
    public boolean isModified() {
        return settings.isModified();
    }

    @Override
    public void apply() throws ConfigurationException {
        settings.apply();
    }
}
