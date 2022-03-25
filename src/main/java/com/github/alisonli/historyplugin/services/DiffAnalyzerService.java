package com.github.alisonli.historyplugin.services;

import com.intellij.openapi.components.Service;
import com.intellij.openapi.project.Project;

/**
 * Project service for analyzing diffs between commits.
 */
@Service
public final class DiffAnalyzerService {
    private final Project project;

    public DiffAnalyzerService(Project project) {
        this.project = project;
    }
}
