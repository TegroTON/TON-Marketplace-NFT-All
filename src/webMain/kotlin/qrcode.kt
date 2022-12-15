import kotlinx.coroutines.await
import kotlin.js.Promise

suspend fun QrCodeToString(text: String, options: dynamic): String =
    (js("QRCode.toString(text, options)") as Promise<String>).await()
