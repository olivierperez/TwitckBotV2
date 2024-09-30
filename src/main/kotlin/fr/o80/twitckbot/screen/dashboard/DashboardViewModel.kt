package fr.o80.twitckbot.screen.dashboard

import fr.o80.twitckbot.di.SessionScope
import fr.o80.twitckbot.screen.ViewModel
import fr.o80.twitckbot.service.connectable.ConnectableStatus
import fr.o80.twitckbot.system.ConnectablesManager
import fr.o80.twitckbot.system.Extension
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flatMapMerge
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.merge
import kotlinx.coroutines.flow.scan
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.transform
import kotlinx.coroutines.launch
import javax.inject.Inject

@SessionScope
class DashboardViewModel @Inject constructor(
    private val connectablesManager: ConnectablesManager,
    private val extensions: @JvmSuppressWildcards Set<Extension>
): ViewModel() {

    sealed interface Effect {
        data object Loading : Effect
        data class Retry(val name: String) : Effect
    }

    sealed interface Action {
        data class SetConnectableStates(
            val connectableStates: List<ConnectableState>
        ) : Action

        data class SetExtensions(
            val extensions: Collection<Extension>
        ) : Action

        data class UpdateConnectableStatus(
            val name: String,
            val status: ConnectableStatus
        ) : Action
    }

    data class State(
        val connectableStates: List<ConnectableState>,
        val extensions: Collection<Extension>
    ) {
        companion object {
            val EMPTY = State(
                connectableStates = emptyList(),
                extensions = emptyList()
            )
        }
    }

    private val effects = MutableSharedFlow<Effect>()
    private val actions = MutableSharedFlow<Action>()

    @OptIn(ExperimentalCoroutinesApi::class)
    val state: Flow<State> = flowOf(State.EMPTY)
        .flatMapMerge {
            merge(
                effects.transform { effect -> emitAll(execute(effect)) },
                actions
            )
        }
        .scan(State.EMPTY) { state, action -> reduce(action, state) }
        .catch { emit(State.EMPTY) } /* TODO Handle errors */
        .stateIn(
            scope = scope,
            started = SharingStarted.Eagerly,
            initialValue = State.EMPTY
        )

    override fun init() {
        scope.launch {
            effects.emit(Effect.Loading)
            connectablesManager.statuses().collect { (connectable, status) ->
                actions.emit(Action.UpdateConnectableStatus(connectable.name, status))
            }
        }
    }

    private fun execute(effect: Effect): Flow<Action> {
        return when (effect) {
            is Effect.Loading -> effect.execute()
            is Effect.Retry -> effect.execute()
        }
    }

    private fun reduce(action: Action, state: State): State {
        return when (action) {
            is Action.SetConnectableStates -> action.reduce(state)
            is Action.SetExtensions -> action.reduce(state)
            is Action.UpdateConnectableStatus -> action.reduce(state)
        }
    }

    private fun Action.SetConnectableStates.reduce(state: State): State {
        return state.copy(connectableStates = this.connectableStates)
    }

    private fun Action.SetExtensions.reduce(state: State): State {
        return state.copy(extensions = this.extensions)
    }

    private fun Action.UpdateConnectableStatus.reduce(state: State): State {
        return state.copy(
            connectableStates = state.connectableStates.map {
                if (it.name == this.name) {
                    it.copy(status = this.status)
                } else {
                    it
                }
            }
        )
    }

    @Suppress("unused")
    private fun Effect.Loading.execute(): Flow<Action> {
        return merge(
            flow {
                emit(Action.SetConnectableStates(connectablesManager.connectables.map { connectable ->
                    ConnectableState(
                        connectable.name,
                        connectable.icon,
                        ConnectableStatus.NOT_CONNECTED
                    )
                }))
            },
            flow {
                try {
                    emit(Action.SetExtensions(extensions))
                } catch (e: Exception) {
                    // TODO OPZ Afficher ça dans le dashboard
                    e.printStackTrace()
                }
            }
        )
    }

    private fun Effect.Retry.execute(): Flow<Action> {
        return flow {
            scope.launch {
                connectablesManager.connect(name)
            }
        }
    }

    fun retry(name: String) {
        scope.launch {
            effects.emit(Effect.Retry(name))
        }
    }

    data class ConnectableState(
        val name: String,
        val icon: String,
        val status: ConnectableStatus
    )
}
