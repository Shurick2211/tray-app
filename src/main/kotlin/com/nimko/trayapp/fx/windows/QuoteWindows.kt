package com.nimko.trayapp.fx.windows

import com.nimko.trayapp.i18n.FxmlSpringLoader
import com.nimko.trayapp.i18n.Translator
import com.nimko.trayapp.services.RandomLineService
import jakarta.annotation.PostConstruct
import javafx.animation.PauseTransition
import javafx.application.Platform
import javafx.event.EventHandler
import javafx.fxml.FXML
import javafx.scene.Scene
import javafx.scene.control.Label
import javafx.scene.image.Image
import javafx.scene.layout.AnchorPane
import javafx.scene.text.Text
import javafx.stage.Stage
import javafx.util.Duration
import org.springframework.context.ApplicationContext
import org.springframework.stereotype.Component
import java.awt.Toolkit
import java.awt.datatransfer.StringSelection

@Component
class QuoteWindows(
    private val randomLineService: RandomLineService,
    private val translator: Translator,
    private val context: ApplicationContext,
) {
    @FXML
    private lateinit var quote: Text
    @FXML
    private lateinit var anchor: AnchorPane

    private lateinit var stage: Stage

    @PostConstruct
    fun init() {
        Platform.setImplicitExit(false)
    }

    fun show() {
        val css = javaClass.getResource("/css/styles-notes.css").toExternalForm()
        Platform.runLater {
            stage = Stage().apply {
                title = translator.get("quote")
                val root =
                    FxmlSpringLoader.load(context, javaClass.getResource("/fxml/quote_view.fxml")!!)
                scene = Scene(root, 600.0, 100.0)
                scene.getStylesheets().add(css);
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
        quote.text = randomLineService.getRandomLine()
        quote.onMouseClicked = EventHandler {
           copyToClipboard(quote.text)
            showCopiedNotification(anchor)
        }
    }

    fun copyToClipboard(text: String) {
        val stringSelection = StringSelection(text)
        val clipboard = Toolkit.getDefaultToolkit().systemClipboard
        clipboard.setContents(stringSelection, null)
    }

    fun showCopiedNotification(root: AnchorPane) {
        val notification = Label("Copied!")
        notification.style = "-fx-background-color: green; -fx-text-fill: white; -fx-padding: 10 20 10 20; -fx-background-radius: 5;"

        notification.applyCss()
        notification.layout()

        AnchorPane.setBottomAnchor(notification, 10.0)

        root.children.add(notification)

        notification.layoutX = (root.width - 40) / 2

        val pause = PauseTransition(Duration.seconds(2.0))
        pause.setOnFinished {
            root.children.remove(notification)
        }
        pause.play()
    }

}