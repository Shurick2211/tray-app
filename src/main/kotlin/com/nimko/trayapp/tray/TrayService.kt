package com.nimko.trayapp.tray

import com.nimko.trayapp.fx.windows.ListWindow
import com.nimko.trayapp.fx.windows.NotesWindows
import com.nimko.trayapp.fx.windows.PostWindow
import com.nimko.trayapp.fx.windows.QuoteWindows
import com.nimko.trayapp.i18n.Translator
import jakarta.annotation.PostConstruct
import javafx.application.Platform
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import java.awt.*
import java.util.*


@Service
class TrayService(
    private val postWindow: PostWindow,
    private val translator: Translator,
    private val listWindow: ListWindow,
    private val notesWindows: NotesWindows,
    private val quoteWindows: QuoteWindows,
) {
    private var trayIcon: TrayIcon? = null
    lateinit var postItem: MenuItem
    lateinit var langItem: Menu
    lateinit var exitItem: MenuItem
    lateinit var listItem: MenuItem
    lateinit var notesItem: MenuItem
    lateinit var quoteItem: MenuItem
    @Value("\${app.title}")
    var appTitle: String? = null

    private val trayFont = Font("DejaVu Serif", Font.PLAIN, 14)

    @PostConstruct
    fun init() {
        val icon = javaClass.getResource("/icons/icon.png")
        if (!SystemTray.isSupported()) {
            println("SystemTray is not supported on this platform.")
            return
        }
        val tray = SystemTray.getSystemTray()
        val image = Toolkit.getDefaultToolkit().getImage(icon)

        if (!Platform.isFxApplicationThread()) {
            Platform.startup { }
        }

        val popup = PopupMenu()

        val submenu = Menu("Options")
        postItem = MenuItem(translator.get("title.create")).apply {
            addActionListener {
                println("Post!")
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

        notesItem = MenuItem(translator.get("notes")).apply {
            addActionListener {
                println("Notes!")
                notesWindows.show()
            }
        }

        quoteItem = MenuItem(translator.get("quote")).apply {
            addActionListener {
                println("Quote!")
                quoteWindows.show()
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
        popup.add(notesItem)
        popup.add(quoteItem)
        popup.addSeparator()
        popup.add(langItem)
        popup.addSeparator()
        popup.add(exitItem)

        postItem.font = trayFont
        listItem.font = trayFont
        notesItem.font = trayFont
        exitItem.font = trayFont
        langItem.font = trayFont

        trayIcon = TrayIcon(image, appTitle, popup).apply {
            isImageAutoSize = true
        }

        try {
            tray.add(trayIcon)
        } catch (e: AWTException) {
            println("Failed to add tray icon: ${e.message}")
        }
    }

    fun setTexts() {
        postItem.label = translator.get("title.create")
        langItem.label = translator.get("language")
        exitItem.label = translator.get("exit")
        listItem.label = translator.get("list.event")
        notesItem.label = translator.get("notes")
        quoteItem.label = translator.get("quote")
        Platform.runLater {
            postWindow.setText()
        }
    }
}