package fr.o80.twitckbot.service.system

import javax.inject.Inject

class CopyToClipboard @Inject constructor() {

    operator fun invoke(content: String) {
        val clipboard = java.awt.Toolkit.getDefaultToolkit().systemClipboard
        clipboard.setContents(java.awt.datatransfer.StringSelection(content), null)
    }
}
