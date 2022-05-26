package money.tegro.market.nft

import io.ipfs.multihash.Multihash

// dirty hack to make ipfs work
class DummyMultihash(val content: String) :
    Multihash(Multihash.Type.id, byteArrayOf()) {
    override fun toString() = content
}
