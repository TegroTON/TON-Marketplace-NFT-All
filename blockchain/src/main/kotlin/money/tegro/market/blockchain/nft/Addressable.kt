package money.tegro.market.blockchain.nft

import org.ton.block.MsgAddress

interface Addressable {
    val address: MsgAddress
}
