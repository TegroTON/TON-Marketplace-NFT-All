import kotlinx.coroutines.runBlocking
import money.tegro.market.contract.market.MarketplaceContract
import org.ton.api.liteserver.LiteServerDesc
import org.ton.api.pub.PublicKeyEd25519
import org.ton.block.MsgAddressInt
import org.ton.crypto.base64
import org.ton.lite.client.LiteClient

val owner = MsgAddressInt("EQAnTon5VVNKup8v0EUT0SvCKsRmEpotr_3eSpqYJTneIVht ")

val marketplace = MarketplaceContract(
    owner = owner,
)

println("Marketplace will be deployed to ${marketplace.address().toString(userFriendly = true, bounceable = false)}")
println("Send coins there, then run this script again")

runBlocking {
    val liteClient = LiteClient(
        LiteServerDesc(
            id = PublicKeyEd25519(base64("vOe1Xqt/1AQ2Z56Pr+1Rnw+f0NmAA7rNCZFIHeChB7o=")),
            ip = 1091931590,
            port = 47160,
        )
    )

    liteClient.liteApi.sendMessage(marketplace.externalInitMessage())
}
