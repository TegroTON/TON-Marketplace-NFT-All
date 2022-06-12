package money.tegro.market.drive.api

import money.tegro.market.db.CollectionInfoRepository
import money.tegro.market.db.ItemInfoRepository
import money.tegro.market.db.findByAddress
import org.springframework.core.io.InputStreamResource
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.ton.block.MsgAddressIntStd
import java.io.ByteArrayInputStream
import java.net.URL

@RestController
@RequestMapping("/api/v1/content")
class ContentController(
    val collectionInfoRepository: CollectionInfoRepository,
    val itemInfoRepository: ItemInfoRepository,
) {
    @GetMapping("/{address}/{name}", produces = ["image/jpeg", "image/png"])
    fun getContent(@PathVariable address: String, @PathVariable name: String): ResponseEntity<InputStreamResource>? {
        val str = collectionInfoRepository.findByAddress(MsgAddressIntStd(address))?.metadata?.let {
            if (name == "image") {
                it.imageData?.let { ByteArrayInputStream(it) } ?: it.image?.let {
                    URL(it).openConnection().getInputStream()
                }
            } else if (name == "cover") {
                it.coverImageData?.let { ByteArrayInputStream(it) } ?: it.coverImage?.let {
                    URL(it).openConnection().getInputStream()
                }
            } else {
                null
            }
        } ?: itemInfoRepository.findByAddress(MsgAddressIntStd(address))?.metadata?.let {
            it.imageData?.let { ByteArrayInputStream(it) } ?: it.image?.let {
                URL(it).openConnection().getInputStream()
            }
        }

        return ResponseEntity(str?.let { InputStreamResource(it) }, HttpStatus.OK)
    }
}
