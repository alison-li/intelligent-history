package com.alisli.intelligenthistory.settings;

import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.project.Project;

import javax.swing.*;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

public class RegexSettingsComponent {
    private JPanel rootPanel;
    private JTextField documentationPatternField;
    private JTextField importPatternField;
    private JTextField annotationPatternField;
    private JLabel documentationPatternLabel;
    private JLabel importPatternLabel;
    private JLabel annotationPatternLabel;
    private JLabel descriptionLabel;
    private RegexConfig config;

    public RegexSettingsComponent() {
        rootPanel = new JPanel();
        rootPanel.setLayout(new BoxLayout(rootPanel, BoxLayout.Y_AXIS));

        descriptionLabel = new JLabel("Configure regex patterns for highlighting:");
        rootPanel.add(descriptionLabel);
        rootPanel.add(Box.createVerticalStrut(10));

        documentationPatternLabel = new JLabel("Documentation Pattern:");
        documentationPatternField = new JTextField(20);
        rootPanel.add(documentationPatternLabel);
        rootPanel.add(documentationPatternField);
        rootPanel.add(Box.createVerticalStrut(5));

        importPatternLabel = new JLabel("Import Pattern:");
        importPatternField = new JTextField(20);
        rootPanel.add(importPatternLabel);
        rootPanel.add(importPatternField);
        rootPanel.add(Box.createVerticalStrut(5));

        annotationPatternLabel = new JLabel("Annotation Pattern:");
        annotationPatternField = new JTextField(20);
        rootPanel.add(annotationPatternLabel);
        rootPanel.add(annotationPatternField);
    }

    public void createUIComponents(Project project) {
        this.config = RegexConfig.getInstance(project);
        documentationPatternField.setText(config != null ? config.getDocumentationPattern() : "");
        importPatternField.setText(config != null ? config.getImportPattern() : "");
        annotationPatternField.setText(config != null ? config.getAnnotationPattern() : "");
    }

    boolean isModified() {
        boolean modified = false;
        if (config != null) {
            modified |= !documentationPatternField.getText().equals(config.getDocumentationPattern());
            modified |= !importPatternField.getText().equals(config.getImportPattern());
            modified |= !annotationPatternField.getText().equals(config.getAnnotationPattern());
        }
        return modified;
    }

    public void apply() throws ConfigurationException {
        if (config != null) {
            // Validate the patterns
            try {
                Pattern.compile(documentationPatternField.getText());
                Pattern.compile(importPatternField.getText());
                Pattern.compile(annotationPatternField.getText());
            } catch (PatternSyntaxException e) {
                throw new ConfigurationException("Invalid regular expression: " + e.getMessage());
            }

            config.setDocumentationPattern(documentationPatternField.getText().trim());
            config.setImportPattern(importPatternField.getText().trim());
            config.setAnnotationPattern(annotationPatternField.getText().trim());
        }
    }

    public JPanel getContent() {
        return rootPanel;
    }
}