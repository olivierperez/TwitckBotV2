package fr.o80.twitckbot.extensions.overlay.graphics.utils

class LineSplitter {
    fun split(text: String, charsPerLine: Int): List<String> {
        val words = text.split(' ')

        val lines = mutableListOf<String>()
        var currentLine = 0

        words.forEach { word ->
            if (lines.size <= currentLine) {
                lines.add(word)
            } else {
                lines[currentLine] = lines[currentLine] + " " + word
                if (lines[currentLine].length >= charsPerLine) {
                    currentLine++
                }
            }
        }

        return lines
    }
}
