package com.nimko.trayapp.fx.windows

import com.nimko.trayapp.i18n.FxmlSpringLoader
import com.nimko.trayapp.i18n.Translator
import com.nimko.trayapp.model.PostEntity
import com.nimko.trayapp.services.PostService
import com.nimko.trayapp.services.notify.NotificationService
import com.nimko.trayapp.utils.dayShortName
import com.nimko.trayapp.utils.formatInstantToLocalDateTimeString
import com.nimko.trayapp.utils.instantToLocalDate
import com.nimko.trayapp.utils.toInstant
import com.nimko.trayapp.utils.toLocalDateTime
import jakarta.annotation.PostConstruct
import javafx.application.Platform
import javafx.event.EventHandler
import javafx.fxml.FXML
import javafx.scene.Scene
import javafx.scene.control.*
import javafx.scene.layout.AnchorPane
import javafx.scene.layout.HBox
import javafx.stage.Stage
import org.apache.commons.collections.CollectionUtils
import org.apache.commons.lang3.StringUtils.lowerCase
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.context.ApplicationContext
import org.springframework.stereotype.Component
import java.time.DayOfWeek

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

    @FXML
    private lateinit var periodAnchor: AnchorPane

    @FXML
    private lateinit var dayBox: HBox

    @FXML
    private lateinit var onlyCh: CheckBox

    @FXML
    private lateinit var mnL: Label

    @FXML
    private lateinit var tuL: Label

    @FXML
    private lateinit var weL: Label

    @FXML
    private lateinit var thL: Label

    @FXML
    private lateinit var frL: Label

    @FXML
    private lateinit var stL: Label

    @FXML
    private lateinit var suL: Label

    @FXML
    private lateinit var mnCh: CheckBox

    @FXML
    private lateinit var tuCh: CheckBox

    @FXML
    private lateinit var weCh: CheckBox

    @FXML
    private lateinit var thCh: CheckBox

    @FXML
    private lateinit var frCh: CheckBox

    @FXML
    private lateinit var stCh: CheckBox

    @FXML
    private lateinit var suCh: CheckBox

    @Autowired
    private lateinit var translator: Translator

    private var isPeriod = false

    private lateinit var post: PostEntity

    private val log = LoggerFactory.getLogger(javaClass)

    private lateinit var stage: Stage

    private val listeners = HashSet<Runnable>()

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
                    FxmlSpringLoader.load(context, javaClass.getResource("/fxml/post_view.fxml")!!)
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
        periodAnchor.isVisible = isPeriod
        dayBox.isVisible = onlyCh.isSelected

        onlyCh.onAction = EventHandler {
            dayBox.isVisible = onlyCh.isSelected
        }

        tb.onAction = EventHandler {
            val cal = translator.get("calendar")
            val per = translator.get("period")
            if (tb.text == cal) {
                isPeriod = true
                tb.text = per
                datePicker.isVisible = false
                periodAnchor.isVisible = isPeriod
                dateLabel.text = translator.get("set.period")
            } else {
                tb.text = cal
                isPeriod = false
                datePicker.isVisible = true
                periodAnchor.isVisible = isPeriod
                dateLabel.text = translator.get("date")
            }
        }

        button.onMouseClicked = EventHandler {
            saveAction()
        }

        datePicker.onAction = EventHandler { date ->
            val dt =
                toInstant(
                    toLocalDateTime(
                        (date.source as DatePicker).value,
                        hoursCh.value.toInt(),
                        minutesCh.value.toInt()
                    )
                )
            post.date = dt
        }

        minutesCh.valueProperty().addListener { observable, oldValue, newValue ->
            mT.valueFactory.value = newValue.toInt()
        }

        mT.valueFactory =
            SpinnerValueFactory.IntegerSpinnerValueFactory(0, 59, minutesCh.value.toInt())
        mT.valueProperty().addListener { observable, oldValue, newValue ->
            try {
                val min = newValue
                if (min >= 0 && min < 60) {
                    minutesCh.value = min.toDouble()
                    post.minutes = min.toInt()
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

        hT.valueFactory =
            SpinnerValueFactory.IntegerSpinnerValueFactory(0, 23, hoursCh.value.toInt())
        hT.valueProperty().addListener { observable, oldValue, newValue ->
            try {
                val hours = newValue.toInt()
                if (hours >= 0 && hours < 24) {
                    hoursCh.value = hours.toDouble()
                    post.hours = hours
                } else {
                    throw NumberFormatException()
                }
            } catch (e: Exception) {
                hT.valueFactory.value = oldValue
                e.printStackTrace()
            }
        }

        dayCheckBoxInit()

        setText()
        if (post.id != null) {
            setPost()
        }
    }

    private fun dayCheckBoxInit() {
        if (CollectionUtils.isNotEmpty(post.daysOfWeek)) {
            onlyCh.fire()
            post.daysOfWeek!!.forEach { dayOfWeek ->
                when {
                    dayOfWeek == 1 -> {
                        mnCh.fire()
                    }
                    dayOfWeek == 2 -> {
                        tuCh.fire()
                    }
                    dayOfWeek == 3 -> {
                        weCh.fire()
                    }
                    dayOfWeek == 4 -> {
                        thCh.fire()
                    }
                    dayOfWeek == 5 -> {
                        frCh.fire()
                    }
                    dayOfWeek == 6 -> {
                        stCh.fire()
                    }
                    dayOfWeek == 7 -> {
                        suCh.fire()
                    }
                }
            }

        }

        mnCh.selectedProperty().addListener { _, oldValue, newValue ->
            if (newValue) {
                post.daysOfWeek.add(DayOfWeek.MONDAY.value)
            } else {
                post.daysOfWeek.remove(DayOfWeek.MONDAY.value)
            }
        }
        tuCh.selectedProperty().addListener { _, oldValue, newValue ->
            if (newValue) {
                post.daysOfWeek.add(DayOfWeek.TUESDAY.value)
            } else {
                post.daysOfWeek.remove(DayOfWeek.TUESDAY.value)
            }
        }
        weCh.selectedProperty().addListener { _, oldValue, newValue ->
            if (newValue) {
                post.daysOfWeek.add(DayOfWeek.WEDNESDAY.value)
            } else {
                post.daysOfWeek.remove(DayOfWeek.WEDNESDAY.value)
            }
        }
        thCh.selectedProperty().addListener { _, oldValue, newValue ->
            if (newValue) {
                post.daysOfWeek.add(DayOfWeek.THURSDAY.value)
            } else {
                post.daysOfWeek.remove(DayOfWeek.THURSDAY.value)
            }
        }
        frCh.selectedProperty().addListener { _, oldValue, newValue ->
            if (newValue) {
                post.daysOfWeek.add(DayOfWeek.FRIDAY.value)
            } else {
                post.daysOfWeek.remove(DayOfWeek.FRIDAY.value)
            }
        }
        stCh.selectedProperty().addListener { _, oldValue, newValue ->
            if (newValue) {
                post.daysOfWeek.add(DayOfWeek.SATURDAY.value)
            } else {
                post.daysOfWeek.remove(DayOfWeek.SATURDAY.value)
            }
        }
        suCh.selectedProperty().addListener { _, oldValue, newValue ->
            if (newValue) {
                post.daysOfWeek.add(DayOfWeek.SUNDAY.value)
            } else {
                post.daysOfWeek.remove(DayOfWeek.SUNDAY.value)
            }
        }
    }

    private fun setPost() {
        textArea.text = post.text
        mT.valueFactory.value = post.minutes
        hT.valueFactory.value = post.hours
        tb.isSelected = post.date == null
        if (post.date != null) {
            datePicker.value = instantToLocalDate(post.date!!)
        } else {
            tb.fire()
        }
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
        listeners.forEach { it.run() }
        stage.close()
    }

    fun setText() {
        tb.text = translator.get("calendar")
        dateLabel.text = translator.get("date")
        button.text = translator.get("save")
        textLabel.text = translator.get("text")
        hoursLabel.text = translator.get("hours")
        minutesLabel.text = translator.get("minutes")
        onlyCh.text = translator.get("by.week.day")
        mnL.text = dayShortName(1)
        tuL.text = dayShortName(2)
        weL.text = dayShortName(3)
        thL.text = dayShortName(4)
        frL.text = dayShortName(5)
        stL.text = dayShortName(6)
        suL.text = dayShortName(7)
    }

    fun addSaveListeners(runnable: Runnable) {
        listeners.add(runnable)
    }
}
