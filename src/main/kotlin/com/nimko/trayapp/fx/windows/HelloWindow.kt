package com.nimko.trayapp.fx.windows

import com.nimko.trayapp.i18n.FxmlSpringLoader
import com.nimko.trayapp.i18n.Translator
import com.nimko.trayapp.services.notify.NotificationService
import jakarta.annotation.PostConstruct
import javafx.application.Platform
import javafx.event.EventHandler
import javafx.fxml.FXML
import javafx.scene.Scene
import javafx.scene.control.*
import javafx.stage.Stage
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.ApplicationContext
import org.springframework.stereotype.Component

@Component
class HelloWindow(
    private val context: ApplicationContext,
    private val notificationService: NotificationService,
) {
    @FXML
    private lateinit var textArea: TextArea

    @FXML
    private lateinit var datePicker: DatePicker

    @FXML
    private lateinit var button: Button

    @FXML
    private lateinit var tb: ToggleButton

    @FXML
    private lateinit var hoursCh: Slider

    @FXML
    private lateinit var minutesCh: Slider

    @FXML
    private lateinit var hT: TextField

    @FXML
    private lateinit var mT: TextField

    @FXML
    private lateinit var dateLabel: Label

    @FXML
    private lateinit var textLabel: Label

    @FXML
    private lateinit var hoursLabel: Label

    @FXML
    private lateinit var minutesLabel: Label


    @Autowired
    private lateinit var translator: Translator

    @PostConstruct
    fun init() {
        Platform.setImplicitExit(false)
    }

    fun show() {
        Platform.runLater {
            Stage().apply {
                title = translator.get("title.create")
                val root =
                    FxmlSpringLoader.load(context, javaClass.getResource("/fxml/hello_view.fxml")!!)
                scene = Scene(root, 600.0, 400.0)
                setOnCloseRequest {
                    println("Window onCloseRequest")
                    Platform.requestNextPulse()
                }
                show()
            }
        }
    }


    fun initialize() {
        tb.onAction = EventHandler {
            val cal = translator.get("calendar")
            val per = translator.get("period")
            if (tb.text == cal) {
                tb.text = per
                datePicker.setVisible(false)
                dateLabel.text = translator.get("set.period")
            } else {
                tb.text = cal
                datePicker.setVisible(true)
                dateLabel.text = translator.get("date")
            }
        }

        button.onMouseClicked = EventHandler {
            notificationService.notification(textArea.text)
        }

        datePicker.onAction = EventHandler {
            textArea.text += datePicker.value.toString() + "\n"
        }

        minutesCh.valueProperty().addListener { observable, oldValue, newValue ->
            mT.text = newValue.toInt().toString()
        }
        mT.text = minutesCh.value.toInt().toString()
        mT.textProperty().addListener { observable, oldValue, newValue ->
            try {
                val min = newValue.toInt()
                if (min >= 0 && min < 60) {
                    minutesCh.value = min.toDouble()
                } else {
                    throw NumberFormatException()
                }
            } catch (e: Exception) {
                mT.text = oldValue.toString()
                e.printStackTrace()
            }
        }

        hoursCh.valueProperty().addListener { observable, oldValue, newValue ->
            hT.text = newValue.toInt().toString()
        }
        hT.text = hoursCh.value.toInt().toString()
        hT.textProperty().addListener { observable, oldValue, newValue ->
            try {
                val hours = newValue.toInt()
                if (hours >= 0 && hours < 24) {
                    hoursCh.value = hours.toDouble()
                } else {
                    throw NumberFormatException()
                }
            } catch (e: Exception) {
                hT.text = oldValue.toString()
                e.printStackTrace()
            }
        }
        setText()
    }

    fun setText() {
        tb.text = translator.get("calendar")
        dateLabel.text = translator.get("date")
        button.text = translator.get("save")
        textLabel.text = translator.get("text")
        hoursLabel.text = translator.get("hours")
        minutesLabel.text = translator.get("minutes")
    }
}
