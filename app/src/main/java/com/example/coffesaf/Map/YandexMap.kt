import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import java.net.URLEncoder

@Composable
fun YandexMapView(
    startAddress: String,  // Адрес кафе (статичный)
    endAddress: String,     // Адрес клиента (динамичный)
    showRoute: Boolean = true

) {
    val context = LocalContext.current
    val mapUrl = remember(startAddress, endAddress) {
        if (showRoute) {
            // Параметры:
            // `rtt=auto` – только автомобильный маршрут
            // `rtext=...` – точки маршрута
            "https://yandex.ru/maps/?rtext=${URLEncoder.encode(startAddress, "UTF-8")}~${URLEncoder.encode(endAddress, "UTF-8")}&rtt=auto&mode=routes"
        } else {
            // Просто показываем точку (если не нужен маршрут)
            "https://yandex.ru/maps/?text=${URLEncoder.encode(endAddress, "UTF-8")}"
        }
    }


    AndroidView(
        factory = { ctx ->
            WebView(ctx).apply {
                settings.javaScriptEnabled = true
                settings.domStorageEnabled = true
                webViewClient = WebViewClient()
                loadUrl(mapUrl)
            }
        },
        modifier = Modifier
            .fillMaxWidth()
            .height(500.dp)
            .padding(horizontal = 16.dp)
            .border(
                width = 2.dp,
                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f),
                shape = MaterialTheme.shapes.medium
            )
    )
}