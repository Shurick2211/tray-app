package com.nimko.trayapp.fx.windows

import com.nimko.trayapp.i18n.FxmlSpringLoader
import com.nimko.trayapp.i18n.Translator
import com.nimko.trayapp.model.PostEntity
import com.nimko.trayapp.services.PostService
import com.nimko.trayapp.services.notify.NotificationService
import com.nimko.trayapp.utils.formatInstantToLocalDateTimeString
import com.nimko.trayapp.utils.toInstant
import com.nimko.trayapp.utils.toLocalDateTime
import jakarta.annotation.PostConstruct
import javafx.application.Platform
import javafx.event.EventHandler
import javafx.fxml.FXML
import javafx.scene.Scene
import javafx.scene.control.*
import javafx.stage.Stage
import org.apache.commons.lang3.StringUtils.lowerCase
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.ApplicationContext
import org.springframework.stereotype.Component

@Component
class PostWindow(
    private val context: ApplicationContext,
    private val notificationService: NotificationService,
    private val postService: PostService,
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
    private lateinit var hT: Spinner<Int>

    @FXML
    private lateinit var mT: Spinner<Int>

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

    private var isPeriod = false

    private lateinit var post: PostEntity

    private val log = LoggerFactory.getLogger(javaClass)

    private lateinit var stage: Stage

    @PostConstruct
    fun init() {
        Platform.setImplicitExit(false)
    }

    fun show(id: Long? = null) {
        id?.let {
            post = postService.findById(it) ?: run {
                notificationService.notification(translator.get("not.found"))
                return@show
            }
        } ?: run {
            post = PostEntity()
        }
        Platform.runLater {
           stage = Stage().apply {
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
                isPeriod = true
                tb.text = per
                datePicker.setVisible(false)
                dateLabel.text = translator.get("set.period")
            } else {
                tb.text = cal
                isPeriod = false
                datePicker.setVisible(true)
                dateLabel.text = translator.get("date")
            }
        }

        button.onMouseClicked = EventHandler {
            saveAction()
        }

        datePicker.onAction = EventHandler {
            textArea.text += datePicker.value.toString() + "\n"
        }

        minutesCh.valueProperty().addListener { observable, oldValue, newValue ->
            mT.valueFactory.value = newValue.toInt()
        }

        mT.valueFactory = SpinnerValueFactory.IntegerSpinnerValueFactory(0, 59, minutesCh.value.toInt())
        mT.valueProperty().addListener { observable, oldValue, newValue ->
            try {
                val min = newValue
                if (min >= 0 && min < 60) {
                    minutesCh.value = min.toDouble()
                } else {
                    throw NumberFormatException()
                }
            } catch (e: Exception) {
                mT.valueFactory.value = oldValue
                e.printStackTrace()
            }
        }

        hoursCh.valueProperty().addListener { observable, oldValue, newValue ->
            hT.valueFactory.value = newValue.toInt()
        }

        hT.valueFactory = SpinnerValueFactory.IntegerSpinnerValueFactory(0, 23, hoursCh.value.toInt())
        hT.valueProperty().addListener { observable, oldValue, newValue ->
            try {
                val hours = newValue.toInt()
                if (hours >= 0 && hours < 24) {
                    hoursCh.value = hours.toDouble()
                } else {
                    throw NumberFormatException()
                }
            } catch (e: Exception) {
                hT.valueFactory.value = oldValue
                e.printStackTrace()
            }
        }
        setText()
    }

    private fun saveAction() {
        post.text = textArea.text

        if (isPeriod) {
            post.date = null
        } else {
            datePicker.value?.let { date ->
                val dt =
                    toInstant(toLocalDateTime(date, hoursCh.value.toInt(), minutesCh.value.toInt()))
                post.date = dt
            } ?: run {
                return@saveAction
            }
        }

        log.info("For save {}", post)
        val saved = postService.saveOrUpdate(post)
        val dateTime = if (isPeriod) {
            "${translator.get("period")}: ${lowerCase(translator.get("hours"))} ${hT.value}, ${
                lowerCase(translator.get("minutes"))
            } ${mT.value} "
        } else {
           "${lowerCase(translator.get("date"))} ${formatInstantToLocalDateTimeString(saved.date)}"
        }
        notificationService.notification("$dateTime \n ${saved.text}")
        stage.close()
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
