package com.nimko.trayapp.i18n

import javafx.fxml.FXMLLoader
import javafx.scene.Parent
import javafx.util.Callback
import org.springframework.context.ApplicationContext
import java.net.URL


object FxmlSpringLoader {
    fun load(context: ApplicationContext, url: URL): Parent {
        val loader = FXMLLoader(url)
        loader.controllerFactory = Callback { clazz -> context.getBean(clazz) }
        return loader.load()
    }
}