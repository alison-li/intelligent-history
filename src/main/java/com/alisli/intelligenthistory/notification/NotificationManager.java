package com.alisli.intelligenthistory.notification;

import com.alisli.intelligenthistory.IntelligentHistoryBundle;
import com.intellij.notification.Notification;
import com.intellij.notification.NotificationGroup;
import com.intellij.notification.NotificationGroupManager;
import com.intellij.notification.NotificationType;
import com.intellij.openapi.project.Project;
import icons.MyIcons;

public class NotificationManager {
    private static final NotificationGroup GROUP = NotificationGroupManager.getInstance().getNotificationGroup(
            IntelligentHistoryBundle.message("ih.notification.group"));

    public static void showIssueNotFound(Project project, String hash) {
        final Notification notification =
                GROUP.createNotification(IntelligentHistoryBundle.message("ih.notification.issue.error.content", hash),
                        NotificationType.INFORMATION)
                        .setIcon(MyIcons.Jira);
        notification.notify(project);
    }

    public static void showJiraConfigNotFound(Project project) {
        final Notification notification =
                GROUP.createNotification(IntelligentHistoryBundle.message("ih.notification.config.error.content"),
                        NotificationType.WARNING)
                        .setIcon(MyIcons.Jira);
        notification.notify(project);
    }
}
