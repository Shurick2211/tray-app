package com.nimko.trayapp.fx.windows

import com.nimko.trayapp.i18n.FxmlSpringLoader
import com.nimko.trayapp.i18n.Translator
import com.nimko.trayapp.services.NotesService
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
    private val translator: Translator,
    private val notesService: NotesService
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
        addButton.setOnAction {
            createCard("", Instant.now())
        }
    }

    fun createCard(text: String, date: Instant, id:String? = null) {
        val textArea = TextArea(text).apply {
            isWrapText = true
            prefWidth = 240.0
            prefHeight = 150.0
        }
        val label = Label(formatInstantToLocalDateTimeString(date)).apply {
            styleClass.add("label-date")
        }

        val card = VBox(label, textArea).apply {
            styleClass.add("card")
            padding = Insets(5.0)
            spacing = 5.0
        }
        pane.children.add(card)

    }


}