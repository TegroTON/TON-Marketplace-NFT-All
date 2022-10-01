import kotlinx.coroutines.runBlocking
import money.tegro.market.contract.market.MarketplaceContract
import org.ton.api.liteserver.LiteServerDesc
import org.ton.api.pub.PublicKeyEd25519
import org.ton.block.MsgAddressInt
import org.ton.crypto.base64
import org.ton.lite.client.LiteClient

val owner = MsgAddressInt("EQCtrqGMkj5GvQjFWjVOasejjkOWST7n5GuRGH9V3JJ07uap")

val marketplace = MarketplaceContract(
    owner = owner,
)

println("Marketplace will be deployed to ${marketplace.address().toString(userFriendly = true, bounceable = false)}")
println("Send coins there, then run this script again")

runBlocking {
    val liteClient = LiteClient(
        LiteServerDesc(
            id = PublicKeyEd25519(base64("Htx+2sriPIAZ46oF1j5KGRZFhh+XmZNA+Y0mDPbHTDc=")),
            ip = 96247402,
            port = 34366,
        )
    )

    liteClient.liteApi.sendMessage(marketplace.externalInitMessage())
}
