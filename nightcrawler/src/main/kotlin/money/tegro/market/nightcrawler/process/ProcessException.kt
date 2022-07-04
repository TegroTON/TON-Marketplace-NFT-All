package money.tegro.market.nightcrawler.process

import money.tegro.market.core.key.AddressKey

class ProcessException(val id: AddressKey, override val message: String?, override val cause: Throwable?) :
    Exception(message) {
}
