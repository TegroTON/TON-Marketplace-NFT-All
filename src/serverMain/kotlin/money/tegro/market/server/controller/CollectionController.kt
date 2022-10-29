package money.tegro.market.server.controller

import io.ktor.server.application.*
import io.ktor.server.resources.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import money.tegro.market.resource.CollectionResource
import money.tegro.market.server.repository.CollectionRepository
import org.kodein.di.instance
import org.kodein.di.ktor.controller.AbstractDIController
import org.ton.block.MsgAddressInt

class CollectionController(application: Application) : AbstractDIController(application) {
    private val collectionRepository: CollectionRepository by instance()

    override fun Route.getRoutes() {
        get<CollectionResource> { request ->
            requireNotNull(collectionRepository.get(MsgAddressInt(request.address)))
                .let { call.respond(it) }
        }
    }
}