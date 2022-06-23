package money.tegro.market.tool

import io.micronaut.http.client.annotation.Client
import money.tegro.market.core.operations.ItemOperations

@Client("\${money.tegro.market.tool.server:`http://localhost:8080`}/items")
interface ItemClient : ItemOperations
