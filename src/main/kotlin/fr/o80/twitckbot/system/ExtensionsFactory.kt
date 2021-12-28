package fr.o80.twitckbot.system

import fr.o80.twitckbot.di.SessionScope
import fr.o80.twitckbot.extensions.repeat.RepeatExtension
import fr.o80.twitckbot.extensions.welcome.WelcomeExtension
import fr.o80.twitckbot.service.log.Logger
import fr.o80.twitckbot.service.log.LoggerFactory
import fr.o80.twitckbot.service.time.TimeCheckerFactory
import fr.o80.twitckbot.service.twitch.TwitchApi
import fr.o80.twitckbot.system.event.EventBus
import fr.o80.twitckbot.system.step.StepsExecutor
import javax.inject.Inject
import kotlin.reflect.KClass
import kotlin.reflect.KParameter
import kotlin.reflect.full.createType
import kotlin.reflect.full.primaryConstructor

@SessionScope
class ExtensionsFactory @Inject constructor(
    private val eventBus: EventBus,
    private val twitchApi: TwitchApi,
    private val loggerFactory: LoggerFactory,
    private val timeCheckerFactory: TimeCheckerFactory,
    private val stepsExecutor: StepsExecutor
) {

    suspend fun create(): List<Extension> =
        listOf(
            WelcomeExtension::class,
            RepeatExtension::class
        )
            .map(::create)
            .onEach { extension -> extension.init() }

    private fun create(klass: KClass<out Extension>): Extension {
        return klass.primaryConstructor?.let { constructor ->
            val args = constructor.parameters
                .map { parameter -> provideInjection(klass, parameter) }
            constructor.call(*args.toTypedArray())
        } ?: error("Primary constructor not found for $klass!")
    }

    private fun provideInjection(
        klass: KClass<out Extension>,
        parameter: KParameter
    ): Any = when (parameter.type) {
        EventBus::class.createType() -> eventBus
        Logger::class.createType() -> loggerFactory.getLogger(klass)
        TwitchApi::class.createType() -> twitchApi
        TimeCheckerFactory::class.createType() -> timeCheckerFactory
        StepsExecutor::class.createType() -> stepsExecutor
        else -> error("Cannot inject parameter of type ${parameter.type}")
    }
}
