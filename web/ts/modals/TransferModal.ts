import BN from "bn.js";
import {TransactionRequest} from "../types/TransactionRequest";
import {isValidAddress} from "../utils";
import {connection} from "../Connection";

export function TransferModal(props: {
    transferFee: string,
    networkFee: string,
}) {
    return {
        transferFee:  /* @__PURE__ */new BN(props.transferFee),
        networkFee:  /* @__PURE__ */new BN(props.networkFee),
        newOwner: null as string | null,

        get total() {
            return this.transferFee.add(this.networkFee)
        },

        get canTransfer() {
            return isValidAddress(this.newOwner)
        },

        async requestItemTransfer(item: string, newOwner: string) {
            let req = await (await fetch("/api/v1/transfer?" + new URLSearchParams({
                item: item,
                newOwner: newOwner,
                responseDestination: connection.walletAddress,
            }))).json() as TransactionRequest
            return await connection.requestTransaction(req)
        },
    }
}
