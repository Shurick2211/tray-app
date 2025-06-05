package com.nimko.trayapp.fx.windows


import javafx.application.Platform
import javafx.scene.Scene
import javafx.scene.control.Label
import javafx.scene.layout.VBox
import javafx.stage.Stage

object HelloWindow {
    fun show() {
        Platform.runLater {
            val stage = Stage()
            stage.title = "Hello"
            val label = Label("Hello from JavaFX!")
            val layout = VBox(10.0, label)
            stage.scene = Scene(layout, 200.0, 100.0)
            stage.show()
        }
    }
}
