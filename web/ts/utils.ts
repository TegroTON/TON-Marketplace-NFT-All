import {Address} from "ton/dist/address/Address";

function isValidAddress(address: string | null | undefined) {
    if (address != null) {
        try {
            Address.parse(address)
            return true
        } catch (e) {
        }
    }
    return false
}

export {isValidAddress}
