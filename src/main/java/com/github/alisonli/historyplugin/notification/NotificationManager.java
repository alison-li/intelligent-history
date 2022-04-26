package com.github.alisonli.historyplugin.notification;

import com.intellij.notification.Notification;
import com.intellij.notification.NotificationGroup;
import com.intellij.notification.NotificationGroupManager;
import com.intellij.notification.NotificationType;
import com.intellij.openapi.project.Project;
import icons.MyIcons;

public class NotificationManager {
    private static final NotificationGroup GROUP = NotificationGroupManager.getInstance().getNotificationGroup(
            "Intelligent History");

    public static void showIssueNotFound(Project project, String hash) {
        final Notification notification =
                GROUP.createNotification("Jira issue key not found",
                        "A Jira issue key could not be found for commit " + hash +".",
                        NotificationType.INFORMATION)
                        .setIcon(MyIcons.Jira);
        notification.notify(project);
    }

    public static void showJiraConfigNotFound(Project project) {
        final Notification notification =
                GROUP.createNotification("Jira configuration not set",
                        "Jira endpoint URL, username, and/or password not configured.",
                        NotificationType.WARNING)
                        .setIcon(MyIcons.Jira);
        notification.notify(project);
    }
}
