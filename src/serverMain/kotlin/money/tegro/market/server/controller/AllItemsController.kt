package money.tegro.market.server.controller

import io.ktor.server.application.*
import io.ktor.server.resources.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.toList
import money.tegro.market.model.OrdinaryItemModel
import money.tegro.market.model.SaleItemModel
import money.tegro.market.resource.AllItemsResource
import money.tegro.market.resource.AllItemsResource.Filter.NOT_FOR_SALE
import money.tegro.market.resource.AllItemsResource.Filter.ON_SALE
import money.tegro.market.resource.AllItemsResource.Relation.COLLECTION
import money.tegro.market.resource.AllItemsResource.Relation.OWNERSHIP
import money.tegro.market.resource.AllItemsResource.Sort.*
import money.tegro.market.server.dropTake
import money.tegro.market.server.repository.ItemRepository
import org.kodein.di.instance
import org.kodein.di.ktor.controller.AbstractDIController
import org.ton.block.MsgAddressInt

class AllItemsController(application: Application) : AbstractDIController(application) {
    private val itemRepository: ItemRepository by instance()

    override fun Route.getRoutes() {
        get<AllItemsResource> { request ->
            when (request.relation) {
                COLLECTION -> itemRepository.byCollection(MsgAddressInt(requireNotNull(request.relatedTo)))

                OWNERSHIP -> itemRepository.byOwner(MsgAddressInt(requireNotNull(request.relatedTo)))

                else -> itemRepository.all()
            }
                .filter { item ->
                    when (request.filter) {
                        ON_SALE -> item is SaleItemModel
                        NOT_FOR_SALE -> item is OrdinaryItemModel
                        null -> true
                    }
                }
                .toList() // Damn
                .let { items ->
                    when (request.sort) {
                        INDEX_UP -> items.sortedBy { it.index }
                        INDEX_DOWN -> items.sortedByDescending { it.index }
                        PRICE_UP -> items.sortedBy { (it as? SaleItemModel)?.fullPrice }
                        PRICE_DOWN -> items.sortedByDescending { (it as? SaleItemModel)?.fullPrice }
                        null -> items
                    }
                }
                .asFlow()
                .dropTake(request.drop, request.take)
                .toList()
                .let { call.respond(it) }
        }
    }
}
