package money.tegro.market.tool

import com.github.ajalt.clikt.core.subcommands

fun main(args: Array<String>) =
    Tool().subcommands(
        ItemCommand().subcommands(
            QueryItemCommand(),
            MintStandaloneItemCommand(),
            MintCollectionItemCommand(),
            TransferItemCommand(),
            SellItemCommand(),
        ),
        CollectionCommand().subcommands(
            QueryCollectionCommand(),
            ListCollectionItemsCommand(),
            MintCollectionCommand(),
            CloneCollectionCommand(),
        )
    ).main(args)
