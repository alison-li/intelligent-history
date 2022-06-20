package com.alisli.intelligenthistory.settings;

import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.project.Project;

import javax.swing.*;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Arrays;

public class JiraSettingsComponent {
    private JPanel rootPanel;
    private JTextField endpointURLField;
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JiraConfig config;

    public void createUIComponents(Project project) {
        this.config = JiraConfig.getInstance(project);
        endpointURLField.setText(config != null ? config.getEndpointURL() : "");
        usernameField.setText(config.getUsername());
        passwordField.setText(config.getPassword());
    }

    boolean isModified() {
        boolean modified;
        modified = !endpointURLField.getText().equals(config.getEndpointURL());
        modified |= !usernameField.getText().equals(config.getUsername());
        String currentPassword = config.getPassword();
        if (currentPassword != null) {
            modified |= !Arrays.equals(passwordField.getPassword(), currentPassword.toCharArray());
        }
        return modified;
    }

    public void apply() throws ConfigurationException {
        try {
            URL url = new URL(endpointURLField.getText());
            url.toURI();
        } catch (MalformedURLException | URISyntaxException e) {
            throw new ConfigurationException("Invalid URL");
        }
        config.setEndpointURL(endpointURLField.getText().trim());
        config.setUsername(usernameField.getText().trim());
        config.setPassword(String.valueOf(passwordField.getPassword()).trim());
    }

    public JPanel getContent() {
        return rootPanel;
    }
}
