package fr.o80.twitckbot.internal.storage

import javax.inject.Inject

class FileNameSanitizer @Inject constructor() {
    operator fun invoke(filename: String): String {
        return filename
            .replace("[^\\w\\d]".toRegex(), "_")
            .replace("_{2,}".toRegex(), "_")
    }
}
