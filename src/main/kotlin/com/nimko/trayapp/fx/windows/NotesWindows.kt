package com.nimko.trayapp.fx.windows

import com.nimko.trayapp.i18n.FxmlSpringLoader
import com.nimko.trayapp.i18n.Translator
import com.nimko.trayapp.services.notify.NotificationService
import com.nimko.trayapp.utils.formatInstantToLocalDateTimeString
import jakarta.annotation.PostConstruct
import javafx.application.Platform
import javafx.fxml.FXML
import javafx.geometry.Insets
import javafx.scene.Scene
import javafx.scene.control.Button
import javafx.scene.control.Label
import javafx.scene.control.TextArea
import javafx.scene.image.Image
import javafx.scene.layout.FlowPane
import javafx.scene.layout.VBox
import javafx.stage.Stage
import org.springframework.context.ApplicationContext
import org.springframework.stereotype.Service
import java.time.Instant

@Service
class NotesWindows(
    private val context: ApplicationContext,
    private val notificationService: NotificationService,
    private val translator: Translator,
) {
    @FXML
    private lateinit var pane: FlowPane
    @FXML
    private lateinit var addButton: Button

    private lateinit var stage: Stage

    @PostConstruct
    fun init() {
        Platform.setImplicitExit(false)
    }

    fun show() {
        Platform.runLater {
            stage = Stage().apply {
                title = translator.get("notes")
                val root =
                    FxmlSpringLoader.load(context, javaClass.getResource("/fxml/notes_view.fxml")!!)
                scene = Scene(root, 800.0, 600.0)
                setOnCloseRequest {
                    println("Window onCloseRequest")
                    Platform.requestNextPulse()
                }
                val iconUrl = javaClass.getResource("/icons/icon.png")!!
                icons.add(Image(iconUrl.toExternalForm()))
                resizableProperty().value = false
                show()
            }
        }
    }

    fun initialize() {
        val addButton = Button("+").apply {
            style = "-fx-font-size: 24px; -fx-background-radius: 50%; -fx-pref-width: 50px; -fx-pref-height: 50px;"
            setOnAction {
                createCard("", Instant.now())
            }
        }
    }

    fun createCard(text: String, date: Instant) {
        val textArea = TextArea(text).apply {
            isWrapText = true
            prefWidth = 150.0
            prefHeight = 100.0
        }
        val label = Label(formatInstantToLocalDateTimeString(date)).apply {
            style = "-fx-font-size: 8px: -fx-font-color: lightgray; -fx-background-color:  #f4f4f4;"
        }

        val card = VBox(label, textArea).apply {
            style = "-fx-background-color: #f4f4f4; -fx-border-color: #ccc; -fx-border-radius: 5; -fx-background-radius: 5;"
            padding = Insets(5.0)
            spacing = 5.0
        }
        pane.children.add(card)

    }


}