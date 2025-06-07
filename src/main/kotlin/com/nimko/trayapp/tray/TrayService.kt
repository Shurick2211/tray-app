package com.nimko.trayapp.tray

import com.nimko.trayapp.fx.windows.HelloWindow
import com.nimko.trayapp.i18n.Translator
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
import java.util.Locale


@Service
class TrayService(
    private val helloWindow: HelloWindow,
    private val translator: Translator
) {
    private var trayIcon: TrayIcon? = null

    @PostConstruct
    fun init() {
        val icon = javaClass.getResource("/icons/icon.png")
        if (!SystemTray.isSupported()) {
            println("SystemTray is not supported on this platform.")
            return
        }
        val tray = SystemTray.getSystemTray()
        val image = Toolkit.getDefaultToolkit().getImage(icon)

        Platform.startup { }

        val popup = PopupMenu()

        val submenu = Menu("Options")
        val helloItem = MenuItem(translator.get("title.create")).apply {
            addActionListener {
                println("Hello from Kotlin TrayApp!")
                helloWindow.show()
            }
        }

        submenu.add(helloItem)

        val exitItem = MenuItem(translator.get("exit")).apply {
            addActionListener {
                tray.remove(trayIcon)
                println("Exiting Tray App...")
                System.exit(0)
            }
        }

//        val langItem = Menu(translator.get("language"))
//
//        val en = MenuItem("en").apply {
//            addActionListener {
//                Locale.setDefault(Locale.US)
//            }
//        }
//
//        val ru = MenuItem("ru").apply {
//            addActionListener {
//                Locale.setDefault(Locale.forLanguageTag("ru-RU"))
//            }
//        }
//
//        val ua = MenuItem("ua").apply {
//            addActionListener {
//                Locale.setDefault(Locale.forLanguageTag("ua-UA"))
//            }
//        }
//
//        langItem.add(en)
//        langItem.add(ru)
//        langItem.add(ua)

        popup.add(helloItem)
//        popup.add(langItem)
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