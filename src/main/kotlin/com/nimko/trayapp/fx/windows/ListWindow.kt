package com.nimko.trayapp.fx.windows

import com.nimko.trayapp.i18n.FxmlSpringLoader
import com.nimko.trayapp.i18n.Translator
import com.nimko.trayapp.model.PostEntity
import com.nimko.trayapp.services.PostService
import com.nimko.trayapp.services.notify.NotificationService
import com.nimko.trayapp.utils.dateString
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
import javafx.scene.image.Image
import javafx.scene.layout.HBox
import javafx.stage.Stage
import org.springframework.context.ApplicationContext
import org.springframework.stereotype.Component
import tornadofx.reloadViewsOnFocus
import tornadofx.tooltip


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
                val iconUrl = javaClass.getResource("/icons/icon.png")!!
                icons.add(Image(iconUrl.toExternalForm()))
                reloadViewsOnFocus()
                resizableProperty().value = false
                show()
            }
        }
    }

    fun initialize() {
        tableView.items = postItems
        postWindow.addSaveListeners { refreshTable() }

        nameCol.setCellValueFactory { SimpleStringProperty(it.value.id.toString()) }
        dateCol.setCellValueFactory { SimpleStringProperty(dateString(it.value)) }
        textCol.setCellValueFactory { SimpleStringProperty(it.value.text) }
        activeCol.setCellValueFactory { SimpleStringProperty(if (it.value.active) translator.get("is.active") else translator.get("no.active")) }

        actionCol.setCellValueFactory { features -> SimpleObjectProperty(features.value) }

        actionCol.setCellFactory {
            object : TableCell<PostEntity, PostEntity>() {
                private val editButton = Button("\uD83D\uDD8D")
                private val deleteButton = Button("🗑")
                private val disableButton = Button("❌")
                private val hBox = HBox(20.0, editButton, disableButton, deleteButton)

                override fun updateItem(item: PostEntity?, empty: Boolean) {
                    super.updateItem(item, empty)
                    if (empty || item == null) {
                        graphic = null
                        return
                    }
                    editButton.onAction = EventHandler {
                        postWindow.show(item.id)
                    }
                    editButton.tooltip(translator.get("edit"))

                    if (item.date != null && !item.active) {
                        disableButton.isDisable = true
                    }

                    if (item.active) {
                        disableButton.text = "❌"
                        disableButton.tooltip(translator.get("disable"))
                    } else {
                        disableButton.text = "✔"
                        disableButton.tooltip(translator.get("enable"))
                    }

                    disableButton.onAction = EventHandler {
                        if (item.active) {
                            item.active = false
                            postService.saveOrUpdate(item)
                            Thread.sleep(300)
                            disableButton.text = "✔"
                            disableButton.tooltip(translator.get("enable"))
                            if (item.date != null) {
                                disableButton.isDisable = true
                            }
                        } else {
                            item.active = true
                            postService.saveOrUpdate(item)
                            Thread.sleep(300)
                            disableButton.text = "❌"
                            disableButton.tooltip(translator.get("disable"))
                        }
                        refreshTable()
                    }

                    deleteButton.onAction = EventHandler {
                        postService.deleteById(item.id!!)
                        notificationService.notification("Deleted post ${item.id}")
                        refreshTable()
                    }
                    deleteButton.tooltip(translator.get("delete"))
                    graphic = hBox
                }
            }
        }
        setTranslation()
        refreshTable()
    }

    private fun setTranslation() {
        nameCol.text = translator.get("grid.name")
        dateCol.text = translator.get("grid.period")
        textCol.text = translator.get("grid.text")
        activeCol.text = translator.get("grid.active")
        actionCol.text = translator.get("grid.action")
    }

    private fun refreshTable() {
        val posts = postService.findAll().sortedByDescending { it.active }
        postItems.setAll(posts)
    }

}
