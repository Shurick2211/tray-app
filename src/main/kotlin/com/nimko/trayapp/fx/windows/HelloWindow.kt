package com.nimko.trayapp.fx.windows


import javafx.application.Platform
import javafx.event.EventHandler
import javafx.fxml.FXML
import javafx.fxml.FXMLLoader
import javafx.scene.Scene
import javafx.scene.control.Button
import javafx.scene.control.DatePicker
import javafx.scene.control.Slider
import javafx.stage.Stage
import org.springframework.stereotype.Component
import javafx.scene.control.TextArea
import javafx.scene.control.TextField
import javafx.scene.control.ToggleButton
import javafx.scene.control.ToggleGroup
import javafx.scene.layout.AnchorPane

@Component
class HelloWindow {
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
    private lateinit var calendarCh: AnchorPane

    fun show() {
        Platform.runLater {
            val stage = Stage()
            stage.title = "Hello"
            val loader = FXMLLoader(javaClass.getResource("/fxml/hello_view.fxml"))
            val root = loader.load<javafx.scene.Parent>()
            val scene = Scene(root, 600.0, 400.0)
            stage.scene = scene
            stage.show()
        }
    }

    fun initialize() {
        tb.onAction = EventHandler {
            val cal = "Calendar"
            val per = "Period"
            if(tb.text == cal){
                tb.text = per
            } else {
                tb.text = cal
            }
        }

        button.onMouseClicked = EventHandler {
            textArea.text += "Hello\n"
        }

        datePicker.onAction = EventHandler {
            textArea.text += datePicker.value.toString() + "\n"
        }

        minutesCh.valueProperty().addListener { observable, oldValue, newValue ->
            mT.text = newValue.toInt().toString()
        }
        mT.text = minutesCh.value.toInt().toString()
        mT.textProperty().addListener  { observable, oldValue, newValue ->
            try{
                val min = newValue.toInt()
                if (min >= 0 && min < 60) {
                    minutesCh.value = min.toDouble()
                } else {
                    throw NumberFormatException()
                }
            } catch (e:Exception){
                mT.text = oldValue.toString()
                e.printStackTrace()
            }
        }

        hoursCh.valueProperty().addListener { observable, oldValue, newValue ->
            hT.text = newValue.toInt().toString()
        }
        hT.text = hoursCh.value.toInt().toString()
        hT.textProperty().addListener  { observable, oldValue, newValue ->
            try{
                val hours = newValue.toInt()
                if (hours >= 0 && hours < 24){
                    hoursCh.value = hours.toDouble()
                } else {
                    throw NumberFormatException()
                }
            } catch (e:Exception){
                hT.text = oldValue.toString()
                e.printStackTrace()
            }
        }
    }
}
