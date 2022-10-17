package webview

import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.awt.SwingPanel
import me.friwi.jcefmaven.CefAppBuilder
import me.friwi.jcefmaven.MavenCefAppHandlerAdapter
import org.cef.CefApp
import org.cef.CefSettings
import org.cef.browser.CefBrowser
import org.cef.browser.CefFrame
import org.cef.handler.CefDisplayHandler
import java.io.File

class CefAppState(args: Array<String> = emptyArray(), installDir: File = File("jcef-bundle"), setting: CefSettings.() -> Unit = {}) {
    private val builder = CefAppBuilder()
    private val app: CefApp

    init {
        builder.addJcefArgs(*args)
        builder.setInstallDir(installDir)
        builder.cefSettings.windowless_rendering_enabled = false
        builder.cefSettings.persist_session_cookies = true
        builder.cefSettings.setting()
        builder.setAppHandler(object : MavenCefAppHandlerAdapter() {
            override fun stateHasChanged(state: CefApp.CefAppState) {
                if (state == CefApp.CefAppState.TERMINATED) System.exit(0)
            }
        })

        app = builder.build()
    }

    fun createClient() = app.createClient()
}

@Composable
fun rememberCefAppState(args : Array<String> = emptyArray(), installDir: File = File("jcef-bundle"), setting: CefSettings.() -> Unit = {}) = remember { CefAppState(args, installDir, setting) }

class CefClientState(appState: CefAppState) {

    val client = appState.createClient()

    init {

        val browser = client.createBrowser("https://sagiri.me", false, false)
    }
}

@Composable
fun rememberCefClientState(appState: CefAppState) = remember(appState) { CefClientState(appState) }

class CefBrowserState(clientState: CefClientState, url: String, isTransparent: Boolean = false, isOffscreen: Boolean = false) {

    val browser: CefBrowser
    var url by mutableStateOf("")
    var title by mutableStateOf("")
    var tooltip by mutableStateOf("")
    var status by mutableStateOf("")

    init {
        browser = clientState.client.createBrowser(url, isOffscreen, isTransparent)
        this@CefBrowserState.url = url
        clientState.client.addDisplayHandler(object : CefDisplayHandler {
            override fun onAddressChange(browser: CefBrowser?, frame: CefFrame?, url: String?) {
                this@CefBrowserState.url = url ?: ""
            }

            override fun onTitleChange(browser: CefBrowser?, title: String?) {
                this@CefBrowserState.title = title ?: ""
            }

            override fun onTooltip(browser: CefBrowser?, text: String?): Boolean {
                this@CefBrowserState.tooltip = text ?: ""

                return true
            }

            override fun onStatusMessage(browser: CefBrowser?, value: String?) {
                this@CefBrowserState.status = value ?: ""
            }

            override fun onConsoleMessage(
                browser: CefBrowser?,
                level: CefSettings.LogSeverity?,
                message: String?,
                source: String?,
                line: Int
            ): Boolean {
                TODO("Not yet implemented")
            }

            override fun onCursorChange(browser: CefBrowser?, cursorType: Int): Boolean {
                println(cursorType)
                return true
            }

        })
    }

    fun loadURL(url: String) {
        browser.loadURL(url)
    }

    fun reload() {
        browser.reload()
    }

    fun close(force: Boolean) {
        browser.close(force)
    }
}

@Composable
fun rememberCefBrowserState(clientState: CefClientState, url: String, isTransparent: Boolean = false, isOffscreen: Boolean = false) = remember(clientState, url, isTransparent, isOffscreen) { CefBrowserState(clientState, url, isTransparent, isOffscreen) }

@Composable
fun WebView(webViewState: CefBrowserState, modifier: Modifier = Modifier) {
    DisposableEffect(Unit) {
        println("-------------------------------create")

        onDispose {
            println("-------------------------------------disponse")
        }
    }
    SwingPanel(
        modifier = modifier,
        factory = {
            webViewState.browser.uiComponent
        }
    )
}