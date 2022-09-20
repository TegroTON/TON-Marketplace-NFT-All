import {Address, fromNano} from "ton";

export function toFriendly(address: string): string {
    return Address.parse(address).toFriendly({
        bounceable: true,
        urlSafe: true,
        testOnly: true
    })
}

export function normalizeAndShorten(address: string): string {
    var full = toFriendly(address)
    return full.slice(0, 4) + "..." + full.slice(-5)
}

export function explorerLink(address: string): string {
    return 'https://testnet.tonscan.org/address/' + toFriendly(address)
}

export function formatPrice(nano: string): string {
    return fromNano(nano) + " TON"
}
