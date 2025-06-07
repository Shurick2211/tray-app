package com.nimko.trayapp.tray

import com.nimko.trayapp.fx.windows.ListWindow
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
    private val translator: Translator,
    private val listWindow: ListWindow
) {
    private var trayIcon: TrayIcon? = null
    lateinit var postItem: MenuItem
    lateinit var langItem: Menu
    lateinit var exitItem: MenuItem
    lateinit var listItem: MenuItem

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
        postItem = MenuItem(translator.get("title.create")).apply {
            addActionListener {
                println("Hello from Kotlin TrayApp!")
                postWindow.show()
            }
        }

        submenu.add(postItem)

        listItem = MenuItem(translator.get("list.event")).apply {
            addActionListener {
                println("List event!")
                listWindow.show()
            }
        }

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

        popup.add(postItem)
        popup.add(listItem)
        popup.addSeparator()
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
        postItem.label = translator.get("title.create")
        langItem.label = translator.get("language")
        exitItem.label = translator.get("exit")
        listItem.label = translator.get("list.event")
        Platform.runLater {
            postWindow.setText()
        }
    }
}