package net.kyori.adventure.text.feature.pagination

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.event.ClickEvent

/**
 * Minimal vendored reimplementation of `net.kyori.adventure.text.feature.pagination.Pagination`.
 *
 * The original `adventure-text-feature-pagination` artifact was only ever published as a Kyori
 * SNAPSHOT and is no longer hosted on any public repository, so it is vendored here to keep the
 * project buildable without an external dependency.
 */
class Pagination private constructor(
    val width: Int,
    val resultsPerPage: Int,
    val renderer: Renderer
) {

    interface Renderer {
        fun renderEmpty(): Component

        fun renderNextPageButton(page: Int, pageCommand: (Int) -> String): Component =
            Component.text(" ")
                .append(Component.text("[Next page]").clickEvent(ClickEvent.runCommand(pageCommand(page + 1))))

        fun renderPreviousPageButton(page: Int, pageCommand: (Int) -> String): Component =
            Component.text("[Previous page]").clickEvent(ClickEvent.runCommand(pageCommand(page - 1)))
                .append(Component.text(" "))
    }

    interface Builder {
        fun width(width: Int): Builder
        fun resultsPerPage(resultsPerPage: Int): Builder
        fun renderer(renderer: Renderer): Builder
        fun create(): Pagination
    }

    private class BuilderImpl : Builder {
        var width = 0
        var resultsPerPage = 0
        var renderer: Renderer = object : Renderer {
            override fun renderEmpty(): Component = Component.empty()
        }

        override fun width(width: Int): Builder = apply { this.width = width }
        override fun resultsPerPage(resultsPerPage: Int): Builder = apply { this.resultsPerPage = resultsPerPage }
        override fun renderer(renderer: Renderer): Builder = apply { this.renderer = renderer }
        override fun create(): Pagination = Pagination(width, resultsPerPage, renderer)
    }

    fun paginate(title: Component, source: (Component?, Int) -> List<Component>): Rendered =
        paginate(title, source, null)

    fun paginate(title: Component, source: (Component?, Int) -> List<Component>, pageCommand: ((Int) -> String)?): Rendered =
        Rendered(this, title, source, pageCommand)

    class Rendered(
        private val pagination: Pagination,
        private val title: Component,
        private val source: (Component?, Int) -> List<Component>,
        private val pageCommand: ((Int) -> String)?
    ) {
        fun render(rows: List<Component>, page: Int): List<Component> {
            val entries = rows.flatMapIndexed { index, component -> source(component, index) }
            val pages = if (entries.isEmpty()) 1 else (entries.size + pagination.resultsPerPage - 1) / pagination.resultsPerPage
            val safePage = page.coerceIn(1, pages)

            val result = ArrayList<Component>()
            result.add(title)

            if (entries.isEmpty()) {
                result.add(pagination.renderer.renderEmpty())
                return result
            }

            val from = (safePage - 1) * pagination.resultsPerPage
            val to = minOf(from + pagination.resultsPerPage, entries.size)
            for (i in from until to) {
                result.add(entries[i])
            }

            if (pageCommand != null) {
                if (safePage > 1) {
                    result.add(pagination.renderer.renderPreviousPageButton(safePage, pageCommand))
                }
                if (safePage < pages) {
                    result.add(pagination.renderer.renderNextPageButton(safePage, pageCommand))
                }
            }

            return result
        }
    }

    companion object {
        fun builder(): Builder = BuilderImpl()
    }
}
