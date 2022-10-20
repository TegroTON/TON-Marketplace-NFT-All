package money.tegro.market.server.controller

import io.ktor.server.application.*
import io.ktor.server.resources.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.coroutines.flow.toList
import money.tegro.market.resource.CollectionResource
import money.tegro.market.server.repository.CollectionRepository
import org.kodein.di.instance
import org.kodein.di.ktor.controller.AbstractDIController
import org.ton.block.MsgAddressInt

class CollectionController(application: Application) : AbstractDIController(application) {
    private val collectionRepository: CollectionRepository by instance()

    override fun Route.getRoutes() {
        get<CollectionResource> { request ->
            call.respond(
                when (request.sort) {
                    CollectionResource.Sort.ALL -> collectionRepository.all().toList()
                    CollectionResource.Sort.TOP -> collectionRepository.all().toList() // TODO: Actual collection top
                }
            )
        };

        get<CollectionResource.ByAddress> { request ->
            call.respond(requireNotNull(collectionRepository.get(MsgAddressInt(request.address))))
        }
    }
}
