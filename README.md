# Intelligent History

![Build](https://github.com/Alison-Li/history-plugin/workflows/Build/badge.svg)
[![Version](https://img.shields.io/jetbrains/plugin/v/PLUGIN_ID.svg)](https://plugins.jetbrains.com/plugin/PLUGIN_ID)
[![Downloads](https://img.shields.io/jetbrains/plugin/d/PLUGIN_ID.svg)](https://plugins.jetbrains.com/plugin/PLUGIN_ID)

<!-- Plugin description -->
Intelligent History is a plugin for [IntelliJ IDEA](https://www.jetbrains.com/idea/) that aims to support more efficient exploration of a Java class' commit history by:
1) Highlighting the commit log for a file to distinguish important commits from less important commits;
2) Extracting referenced Jira issue keys from commit messages and providing the Jira issue information directly in IntelliJ.
<!-- Plugin description end -->

## Features

Commit highlighting is determined by the diff between two commits and using simple regex patterns to check for commits
that solely contain documentation changes, certain annotations like `@Deprecated`, import statements, and newlines.
Currently, the regex patterns used as heuristics for determining potentially less interesting or trivial commits are
specific to Java.

![Commit Highlighting and Diff Metadata Teaser](/doc/demo/highlight-diff-metadata-action.gif)

The Diff Metadata information for a commit is described using the following categories for changes:
* **Documentation:** The number of affected lines involving documentation and comments.
* **Annotation:** The number of affected lines involving `@Deprecated` and `@Suppress` annotations.
* **Import:** The number of affected lines involving `import`.
* **Newline:** The number of affected lines involving newlines.
* **Other:** All other changes that do not fall into the above categories.

For Jira integration, navigate to <kbd>Settings/Preferences</kbd> > <kbd>Tools</kbd> > <kbd>Intelligent History</kbd>
to enter your Jira endpoint URL and credentials.
The purpose of this Jira integration is for projects which have a convention of referencing one Jira issue key
per commit.
For example, given a selected commit with the message: `MYPROJECT-1010: Introduce new feature X`, Intelligent History
will use regex to extract the Jira issue key `MYPROJECT-1010` and display information about the Jira issue within IntelliJ.

![Jira Issue Metadata Teaser](/doc/demo/jira-action.gif)

The Jira Metadata information for a commit and its referenced Jira issue is described here:
* **Commit Author Comments:** The number of commit author comments in the Jira issue. 
    Matches the commit author's name with the Jira issue reporter's display name.
* **Total Comments:** The total number of comments in the Jira issue, excluding bot comments, which is determined based on display name using regex.
* **People Involved:** The total number of unique people involved in a Jira issue, excluding bots.
* **Watches:** The number of people watching the Jira issue.
* **Votes:** The number of votes on the issue.
* **Issue Links:** The number of other issues linked to the Jira issue.
* **Sub Tasks:** The number of sub-tasks the Jira issue has.

## Installation

Intelligent History is currently supported for IntelliJ 2022.1.

- Using IDE built-in plugin system (currently not available):
  
  <kbd>Settings/Preferences</kbd> > <kbd>Plugins</kbd> > <kbd>Marketplace</kbd> > <kbd>Search for "Intelligent History"</kbd> >
  <kbd>Install Plugin</kbd>
  
- Manually:

  Download the [latest release](https://github.com/Alison-Li/history-plugin/releases/latest) and install it manually using
  <kbd>Settings/Preferences</kbd> > <kbd>Plugins</kbd> > <kbd>⚙️</kbd> > <kbd>Install plugin from disk...</kbd>


---
Plugin based on the [IntelliJ Platform Plugin Template][template].

[template]: https://github.com/JetBrains/intellij-platform-plugin-template
