import BN from "bn.js";
import {toNano} from "ton";
import {TransactionRequest} from "../types/TransactionRequest";
import {connection} from "../Connection";

export function SellModal(props: {
    royaltyNumerator: number,
    royaltyDenominator: number,
    marketplaceFeeNumerator: number,
    marketplaceFeeDenominator: number,
    serviceFee: string,
}) {
    return {
        priceTon: 10.0,
        serviceFee: new BN(props.serviceFee),

        get price() {
            return toNano(this.priceTon)
        },
        get royalties() {
            return this.price.mul(new BN(props.royaltyNumerator)).div(new BN(props.royaltyDenominator))
        },
        get marketplaceFee() {
            return this.price.mul(new BN(props.marketplaceFeeNumerator)).div(new BN(props.marketplaceFeeDenominator))
        },
        get fullPrice() {
            return this.price.add(this.royalties.add(this.marketplaceFee))
        },

        async requestItemSale(item: string, price: BN) {
            let req = await (await fetch("/api/v1/sell?" + new URLSearchParams({
                item: item,
                seller: connection.walletAddress,
                price: price.toString(),
            }))).json() as TransactionRequest
            return await connection.requestTransaction(req)
        },
    }
}
