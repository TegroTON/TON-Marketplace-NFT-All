package money.tegro.market.contract

class ContractException(override val message: String?, override val cause: Throwable? = null) :
    Exception(message, cause)
