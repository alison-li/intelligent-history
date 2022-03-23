package com.github.alisonli.historyplugin.services;

import com.intellij.openapi.components.Service;
import com.intellij.openapi.project.Project;

/**
 * Project service for fetching the Jira information in a file history.
 */
@Service
public final class JiraFetcherService {
    private final Project project;

    public JiraFetcherService(Project project) {
        this.project = project;
    }
}
