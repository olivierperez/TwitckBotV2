package fr.o80.twitckbot.navigation

import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.router.stack.ChildStack
import com.arkivanov.decompose.router.stack.StackNavigation
import com.arkivanov.decompose.router.stack.childStack
import com.arkivanov.decompose.router.stack.pushNew
import com.arkivanov.decompose.value.Value
import fr.o80.twitckbot.data.model.Config
import fr.o80.twitckbot.di.AuthenticatedComponent
import fr.o80.twitckbot.di.DaggerRootDiComponent
import fr.o80.twitckbot.di.RootDiComponent
import fr.o80.twitckbot.navigation.RootComponent.Child.Dashboard
import fr.o80.twitckbot.navigation.RootComponent.Child.Onboarding
import fr.o80.twitckbot.screen.dashboard.DashboardComponent
import fr.o80.twitckbot.screen.onboarding.OnboardingComponent
import kotlinx.serialization.Serializable

interface RootComponent {
    val stack: Value<ChildStack<*, Child>>

    sealed class Child {
        data class Onboarding(val component: OnboardingComponent) : Child()
        data class Dashboard(val component: DashboardComponent) : Child()
    }
}

class DefaultRootComponent(
    componentContext: ComponentContext
) : RootComponent, ComponentContext by componentContext {

    private val rootDiComponent: RootDiComponent = DaggerRootDiComponent.create()

    private var authenticatedComponent: AuthenticatedComponent? = null

    private val navigation = StackNavigation<Screen>()

    override val stack: Value<ChildStack<*, RootComponent.Child>> =
        childStack(
            source = navigation,
            serializer = Screen.serializer(),
            initialConfiguration = Screen.Onboarding,
            handleBackButton = true,
            childFactory = ::child
        )

    @Suppress("UNUSED_PARAMETER")
    private fun child(
        screen: Screen,
        componentContext: ComponentContext
    ): RootComponent.Child = when (screen) {
        Screen.Onboarding -> Onboarding(onboardingComponent())
        Screen.Dashboard -> Dashboard(dashboardComponent())
    }

    private fun onboardingComponent() = OnboardingComponent(
        rootDiComponent = rootDiComponent,
        onAuthentication = ::onAuthentication
    )

    private fun dashboardComponent() = DashboardComponent(
        authenticatedComponent = authenticatedComponent ?: error("Authenticated component is not yet initialized!")
    )

    private fun onAuthentication(config: Config) {
        authenticatedComponent =
            rootDiComponent.authenticatedComponentBuilder
                .withConfig(config)
                .build()
        navigation.pushNew(Screen.Dashboard)
    }

    @Serializable
    private sealed interface Screen {
        @Serializable
        data object Onboarding : Screen
        @Serializable
        data object Dashboard : Screen
    }
}
