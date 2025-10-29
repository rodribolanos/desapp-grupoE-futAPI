package ar.edu.unq.futapp.beans

import ar.edu.unq.futapp.exception.OverloadBrowserException
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.withTimeout
import org.springframework.stereotype.Component
import java.util.concurrent.TimeUnit

@Component
class SynchronizedSeleniumWrapper(val seleniumWebBrowser: SeleniumWebBrowser) {
    private val timeoutInMillis: Long = TimeUnit.SECONDS.toMillis(10)
    private val mutex = Mutex()

    init {
        seleniumWebBrowser.setOnCloseListener { mutex.unlock() }
    }

    fun seleniumWebBrowser(): SeleniumWebBrowser {
        return runBlocking {
            try {
                withTimeout(timeoutInMillis) {
                    mutex.lock()
                    seleniumWebBrowser
                }
            } catch (_: Exception) {
                throw OverloadBrowserException()
            }
        }
    }
}