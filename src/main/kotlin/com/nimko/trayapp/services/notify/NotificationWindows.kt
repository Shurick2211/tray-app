package com.nimko.trayapp.services.notify

class NotificationWindows: Noticeable {
//    override fun notification(title: String, message: String) {
//        Runtime.getRuntime().exec(arrayOf("msg", "*", "$title: $message"))
//    }

override fun notification(title: String, message: String) {
    val command = """
            Add-Type -AssemblyName System.Windows.Forms
            [System.Windows.Forms.MessageBox]::Show('$message', '$title', 'OK', 'Information')
        """.trimIndent()

    Runtime.getRuntime().exec(arrayOf("powershell", "-Command", command))
}

//    override fun notification(title: String, message: String) {
//        val command = """
//            Add-Type -AssemblyName System.Windows.Forms
//            Add-Type -AssemblyName System.Drawing
//
//            ${'$'}notify = New-Object System.Windows.Forms.NotifyIcon
//            ${'$'}notify.Icon = [System.Drawing.SystemIcons]::Information
//            ${'$'}notify.Visible = ${'$'}true
//            ${'$'}notify.ShowBalloonTip(5000, '$title', '$message', [System.Windows.Forms.ToolTipIcon]::Info)
//            Start-Sleep -Seconds 6
//            ${'$'}notify.Dispose()
//        """.trimIndent()
//
//        Runtime.getRuntime().exec(arrayOf(
//            "powershell",
//            "-WindowStyle", "Hidden",
//            "-Command",
//            command
//        ))
//    }

}