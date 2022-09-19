import {Address} from "ton";

export default function normalizeAndShorten(address: string): string {
    var full = Address.parse(address).toFriendly({
        bounceable: true,
        urlSafe: true,
        testOnly: true
    })
    return full.slice(0, 4) + "..." + full.slice(-5)
}
