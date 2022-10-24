package money.tegro.market.server.controller

import io.ktor.server.application.*
import io.ktor.server.resources.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.coroutines.flow.toList
import money.tegro.market.resource.AllCollectionsResource
import money.tegro.market.resource.AllCollectionsResource.Sort.ALL
import money.tegro.market.resource.AllCollectionsResource.Sort.TOP
import money.tegro.market.server.dropTake
import money.tegro.market.server.repository.CollectionRepository
import org.kodein.di.instance
import org.kodein.di.ktor.controller.AbstractDIController

class AllCollectionsController(application: Application) : AbstractDIController(application) {
    private val collectionRepository: CollectionRepository by instance()

    override fun Route.getRoutes() {
        get<AllCollectionsResource> { request ->
            collectionRepository.all()
                .let {
                    when (request.sort) {
                        ALL -> it

                        TOP -> it // TODO: Actual collection top

                        else -> it
                    }
                }
                .dropTake(request.drop, request.take)
                .toList()
                .let { call.respond(it) }
        }
    }
}
