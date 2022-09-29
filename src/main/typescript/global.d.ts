import {TonWalletProvider} from "./types/TonWalletProvider";

declare global {
   var ton: TonWalletProvider | null | undefined
}
