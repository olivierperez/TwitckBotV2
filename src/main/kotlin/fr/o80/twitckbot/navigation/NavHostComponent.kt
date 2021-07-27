package fr.o80.twitckbot.navigation

import androidx.compose.runtime.Composable
import com.arkivanov.decompose.ComponentContext
import com.arkivanov.decompose.extensions.compose.jetbrains.Children
import com.arkivanov.decompose.extensions.compose.jetbrains.animation.child.crossfade
import com.arkivanov.decompose.push
import com.arkivanov.decompose.router
import com.arkivanov.decompose.statekeeper.Parcelable
import fr.o80.twitckbot.di.AuthenticatedComponent
import fr.o80.twitckbot.di.DaggerRootComponent
import fr.o80.twitckbot.di.RootComponent
import fr.o80.twitckbot.screen.dashboard.DashboardComponent
import fr.o80.twitckbot.screen.onboarding.OnboardingComponent

/**
 * Navigator
 */
class NavHostComponent(
    componentContext: ComponentContext
) : Component, ComponentContext by componentContext {

    private val rootComponent: RootComponent = DaggerRootComponent.create()

    private var authenticatedComponent: AuthenticatedComponent? = null

    private val router = router<ScreenConfig, Component>(
        initialConfiguration = ScreenConfig.Onboarding,
        childFactory = ::createScreenComponent
    )

    private fun createScreenComponent(
        screenConfig: ScreenConfig,
        componentContext: ComponentContext
    ): Component {
        return when (screenConfig) {
            is ScreenConfig.Onboarding -> OnboardingComponent(
                rootComponent,
                onAuthentication = {
                    authenticatedComponent =
                        rootComponent.authenticatedComponentBuilder
                            .withAuth(it)
                            .build()
                    router.push(ScreenConfig.Dashboard)
                }
            )
            is ScreenConfig.Dashboard -> {
                DashboardComponent(
                    authenticatedComponent ?: error("Authenticated component is not yet initialized!")
                )
            }
        }
    }

    @Composable
    override fun render() {
        Children(
            routerState = router.state,
            animation = crossfade()
        ) {
            it.instance.render()
        }
    }

    sealed class ScreenConfig : Parcelable {
        object Onboarding : ScreenConfig()
        object Dashboard : ScreenConfig()
    }
}

