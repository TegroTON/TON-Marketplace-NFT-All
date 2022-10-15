package money.tegro.market.web.component

import dev.fritz2.core.HtmlTag
import dev.fritz2.core.RenderContext
import dev.fritz2.core.href
import kotlinx.coroutines.flow.map
import money.tegro.market.web.route.AppRouter
import org.w3c.dom.HTMLAnchorElement

fun RenderContext.Link(
    to: Set<String>,
    classes: String? = null,
    content: HtmlTag<HTMLAnchorElement>.() -> Unit
) =
    a {
        href("/#" + to.joinToString("/"))
        clicks.map { to } handledBy AppRouter.navigate

        classes?.let { className(it) }

        this.apply(content)
    }
