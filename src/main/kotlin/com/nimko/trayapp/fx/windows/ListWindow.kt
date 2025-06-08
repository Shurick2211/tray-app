package com.nimko.trayapp.fx.windows

import com.nimko.trayapp.i18n.FxmlSpringLoader
import com.nimko.trayapp.i18n.Translator
import com.nimko.trayapp.model.PostEntity
import com.nimko.trayapp.services.PostService
import com.nimko.trayapp.services.notify.NotificationService
import com.nimko.trayapp.utils.dayShortName
import jakarta.annotation.PostConstruct
import javafx.application.Platform
import javafx.beans.property.SimpleObjectProperty
import javafx.beans.property.SimpleStringProperty
import javafx.collections.FXCollections
import javafx.event.EventHandler
import javafx.fxml.FXML
import javafx.scene.Scene
import javafx.scene.control.Button
import javafx.scene.control.TableCell
import javafx.scene.control.TableColumn
import javafx.scene.control.TableView
import javafx.scene.layout.HBox
import javafx.stage.Stage
import org.apache.commons.collections.CollectionUtils
import org.springframework.context.ApplicationContext
import org.springframework.stereotype.Component
import tornadofx.reloadViewsOnFocus


@Component
class ListWindow(
    private val context: ApplicationContext,
    private val notificationService: NotificationService,
    private val postService: PostService,
    private val translator: Translator,
    private val postWindow: PostWindow
) {

    @FXML
    private lateinit var nameCol: TableColumn<PostEntity, String>

    @FXML
    private lateinit var dateCol: TableColumn<PostEntity, String>

    @FXML
    private lateinit var textCol: TableColumn<PostEntity, String>

    @FXML
    private lateinit var activeCol: TableColumn<PostEntity, String>

    @FXML
    private lateinit var actionCol: TableColumn<PostEntity, PostEntity>

    @FXML
    private lateinit var tableView: TableView<PostEntity>

    private lateinit var stage: Stage

    private val postItems = FXCollections.observableArrayList<PostEntity>()

    @PostConstruct
    fun init() {
        Platform.setImplicitExit(false)
    }

    fun show() {
        Platform.runLater {
            stage = Stage().apply {
                title = translator.get("list.event")
                val root =
                    FxmlSpringLoader.load(context, javaClass.getResource("/fxml/list_view.fxml")!!)
                scene = Scene(root, 1024.0, 720.0)
                show()
            }
            stage.reloadViewsOnFocus()
        }
    }

    fun initialize() {
        tableView.items = postItems
        postWindow.addSaveListeners { refreshTable() }

        nameCol.setCellValueFactory { SimpleStringProperty(it.value.id.toString()) }
        dateCol.setCellValueFactory { SimpleStringProperty(
            it.value.date?.let {d -> d.toString() } ?: if (CollectionUtils.isEmpty(it.value.daysOfWeek)) {
                "${it.value.hours}h ${it.value.minutes}min"
            } else {
                "${it.value.hours}:${it.value.minutes}\n${
                    it.value.daysOfWeek.joinToString(", ") {
                        dayShortName(
                            it
                        )
                    }
                }"
            }
        ) }
        textCol.setCellValueFactory { SimpleStringProperty(it.value.text) }
        activeCol.setCellValueFactory { SimpleStringProperty(if (it.value.active) "Yes" else "No") }

        actionCol.setCellValueFactory { features -> SimpleObjectProperty(features.value) }

        actionCol.setCellFactory {
            object : TableCell<PostEntity, PostEntity>() {
                private val editButton = Button("\uD83D\uDD8D")
                private val deleteButton = Button("🗑")
                private val hBox = HBox(20.0, editButton, deleteButton)

                override fun updateItem(item: PostEntity?, empty: Boolean) {
                    super.updateItem(item, empty)
                    if (empty || item == null) {
                        graphic = null
                        return
                    }
                    editButton.onAction = EventHandler {
                        postWindow.show(item.id)
                    }
                    deleteButton.onAction = EventHandler {
                        postService.deleteById(item.id!!)
                        notificationService.notification("Deleted post ${item.id}")
                        refreshTable()
                    }
                    graphic = hBox
                }
            }
        }

        refreshTable()
    }

    private fun refreshTable() {
        val posts = postService.findAll()
        postItems.setAll(posts)
    }

}
