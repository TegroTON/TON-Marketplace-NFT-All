package money.tegro.market.tool

import com.github.ajalt.clikt.core.subcommands

fun main(args: Array<String>) =
    Tool().subcommands(
        ItemCommand().subcommands(
            QueryItemCommand(),
            MintStandaloneItemCommand(),
            MintCollectionItemCommand(),
        ),
        CollectionCommand().subcommands(
            QueryCollectionCommand(),
            ListCollectionItemsCommand(),
            MintCollectionCommand(),
        )
    ).main(args)
