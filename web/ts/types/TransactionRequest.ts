export interface TransactionRequest {
    dest: string;
    value: string;
    stateInit: string | null | undefined;
    text: string | null | undefined;
    payload: string | null | undefined;
}
