package fr.o80.twitckbot.service.system

import java.awt.Desktop
import java.net.URI
import javax.inject.Inject

class BrowseUrl @Inject constructor() {

    operator fun invoke(url: String): Boolean {
        return if (Desktop.isDesktopSupported() &&
            Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)
        ) {
            Desktop.getDesktop().browse(URI(url))
            true
        } else {
            System.err.println("System cannot open URL: $url")
            false
        }
    }
}
