import androidx.compose.material.MaterialTheme
import androidx.compose.desktop.ui.tooling.preview.Preview
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import webview.WebView
import webview.rememberCefAppState
import webview.rememberCefBrowserState
import webview.rememberCefClientState

@Composable
@Preview
fun App() {
    var url by remember { mutableStateOf("Hello, World!") }
    val app = rememberCefAppState()
    val client = rememberCefClientState(app)
    val webViewState = rememberCefBrowserState(client, "https://bilibili.com", true, false)

    MaterialTheme {
        Column {
            Row {
                TextField(value = url, onValueChange = { url = it })
                Button(onClick = { webViewState.browser.loadURL(url) }) {
                    Text("Go")
                }

                Button(onClick = { webViewState.browser.reload() }) {
                    Text("Reload")
                }

                Button(onClick = { webViewState.browser.goBack() }) {
                    Text("Back")
                }

                Button(onClick = { webViewState.browser.goForward() }) {
                    Text("Forward")
                }
            }

            WebView(webViewState, Modifier.fillMaxSize())
        }
    }
}

fun main() = application {
    Window(onCloseRequest = ::exitApplication) {
        App()
    }
}
