package money.tegro.market.web.component

import dev.fritz2.core.HtmlTag
import dev.fritz2.core.RenderContext
import dev.fritz2.core.href
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import money.tegro.market.web.route.AppRouter
import org.kodein.di.DI
import org.kodein.di.conf.global
import org.kodein.di.instance
import org.w3c.dom.HTMLAnchorElement

fun RenderContext.Link(
    to: Set<String>,
    classes: String? = null,
    content: HtmlTag<HTMLAnchorElement>.() -> Unit
): HtmlTag<HTMLAnchorElement> = Link(flowOf(to), classes, content)

fun RenderContext.Link(
    to: Flow<Set<String>>,
    classes: String? = null,
    content: HtmlTag<HTMLAnchorElement>.() -> Unit
): HtmlTag<HTMLAnchorElement> =
    a {
        val appRouter: AppRouter by DI.global.instance()

        to.map { "/#" + it.joinToString("/") }.let(::href)
        clicks.combine(to) { _, target -> target } handledBy appRouter.navigate

        classes?.let { className(it) }

        this.apply(content)
    }
