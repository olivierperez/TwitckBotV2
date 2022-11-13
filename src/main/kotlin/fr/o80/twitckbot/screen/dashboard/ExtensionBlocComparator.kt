package fr.o80.twitckbot.screen.dashboard

import fr.o80.twitckbot.system.ExtensionBloc

class ExtensionBlocComparator : Comparator<ExtensionBloc> {
    override fun compare(bloc1: ExtensionBloc?, bloc2: ExtensionBloc?): Int {
        return when {
            bloc1 == null || bloc2 == null -> 0
            bloc1.priority != bloc2.priority -> bloc2.priority - bloc1.priority
            else -> bloc1.id.compareTo(bloc2.id)
        }
    }
}
