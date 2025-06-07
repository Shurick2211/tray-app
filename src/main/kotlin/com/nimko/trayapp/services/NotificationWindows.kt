package com.nimko.trayapp.services

class NotificationWindows: Noticeable {
    override fun notification(title: String, message: String) {
        Runtime.getRuntime().exec(arrayOf("powershell", "-command",
            "[Windows.UI.Notifications.ToastNotificationManager, Windows.UI.Notifications, ContentType = WindowsRuntime] > \$null;" +
                    "\$template = [Windows.UI.Notifications.ToastNotificationManager]::GetTemplateContent([Windows.UI.Notifications.ToastTemplateType]::ToastText02);" +
                    "\$textNodes = \$template.GetElementsByTagName('text');" +
                    "\$textNodes.Item(0).AppendChild(\$template.CreateTextNode('Hello')); " +
                    "\$textNodes.Item(1).AppendChild(\$template.CreateTextNode('Message from your app.'));" +
                    "\$toast = [Windows.UI.Notifications.ToastNotification]::new(\$template);" +
                    "\$notifier = [Windows.UI.Notifications.ToastNotificationManager]::CreateToastNotifier('$title');" +
                    "\$notifier.Show($message);"))

    }
}