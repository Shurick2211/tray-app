package com.nimko.trayapp.tray

import com.nimko.trayapp.fx.windows.HelloWindow
import jakarta.annotation.PostConstruct
import org.springframework.stereotype.Service
import java.awt.AWTException
import java.awt.Menu
import java.awt.MenuItem
import java.awt.PopupMenu
import java.awt.SystemTray
import java.awt.Toolkit
import java.awt.TrayIcon
import javafx.application.Platform


@Service
class TrayService {
    private var trayIcon: TrayIcon? = null

    @PostConstruct
    fun init() {
        if (!SystemTray.isSupported()) {
            println("SystemTray is not supported on this platform.")
            return
        }
        val tray = SystemTray.getSystemTray()
        val image = Toolkit.getDefaultToolkit().getImage(javaClass.getResource("/icons/icon.png"))

        Platform.startup {  }

        val popup = PopupMenu()

        val submenu = Menu("Options")
        val helloItem = MenuItem("Say Hello").apply {
            addActionListener {
                println("Hello from Kotlin TrayApp!")
                Platform.runLater {
                    val window = HelloWindow
                    window.show()
                }
            }
        }

        submenu.add(helloItem)

        val exitItem = MenuItem("Exit").apply {
            addActionListener {
                tray.remove(trayIcon)
                println("Exiting Tray App...")
                System.exit(0)
            }
        }

        popup.add(submenu)
        popup.addSeparator()
        popup.add(exitItem)

        trayIcon = TrayIcon(image, "Kotlin Spring Tray App", popup).apply {
            isImageAutoSize = true
        }

        try {
            tray.add(trayIcon)
        } catch (e: AWTException) {
            println("Failed to add tray icon: ${e.message}")
        }
    }
}