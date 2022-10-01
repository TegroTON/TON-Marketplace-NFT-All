import BN from "bn.js";
import {toNano} from "ton";

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
        }
    }
}
