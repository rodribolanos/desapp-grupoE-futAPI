package ar.edu.unq.futapp.beans

import ar.edu.unq.futapp.exception.OverloadBrowserException
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.withTimeout
import org.springframework.context.annotation.Lazy
import org.springframework.stereotype.Component
import org.springframework.context.annotation.Profile
import java.util.concurrent.TimeUnit

@Component
@Profile("default")
class SynchronizedSeleniumWrapper(@param:Lazy val seleniumWebBrowser: SeleniumWebBrowser) {
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