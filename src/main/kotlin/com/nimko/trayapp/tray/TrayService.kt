package com.nimko.trayapp.tray

import com.nimko.trayapp.fx.windows.PostWindow
import com.nimko.trayapp.i18n.Translator
import jakarta.annotation.PostConstruct
import javafx.application.Platform
import org.springframework.stereotype.Service
import java.awt.*
import java.util.Locale


@Service
class TrayService(
    private val postWindow: PostWindow,
    private val translator: Translator
) {
    private var trayIcon: TrayIcon? = null
    lateinit var helloItem: MenuItem
    lateinit var langItem: Menu
    lateinit var exitItem: MenuItem

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
        helloItem = MenuItem(translator.get("title.create")).apply {
            addActionListener {
                println("Hello from Kotlin TrayApp!")
                postWindow.show()
            }
        }

        submenu.add(helloItem)

        exitItem = MenuItem(translator.get("exit")).apply {
            addActionListener {
                tray.remove(trayIcon)
                println("Exiting Tray App...")
                System.exit(0)
            }
        }

        langItem = Menu(translator.get("language"))
        val en = MenuItem("en").apply {
            addActionListener {
                Locale.setDefault(Locale.US)
                setTexts()
            }
        }
        val ru = MenuItem("ru").apply {
            addActionListener {
                Locale.setDefault(Locale.forLanguageTag("ru-UA"))
                setTexts()
            }
        }
        val ua = MenuItem("ua").apply {
            addActionListener {
                Locale.setDefault(Locale.forLanguageTag("ua-UA"))
                setTexts()
            }
        }
        langItem.add(en)
        langItem.add(ru)
        langItem.add(ua)

        popup.add(helloItem)
        popup.add(langItem)
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

    fun setTexts(){
        helloItem.label = translator.get("title.create")
        langItem.label = translator.get("language")
        exitItem.label = translator.get("exit")
        Platform.runLater {
            postWindow.setText()
        }
    }
}