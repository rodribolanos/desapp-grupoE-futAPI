package ar.edu.unq.futapp.beans

import ar.edu.unq.futapp.exception.EntityNotFound
import org.junit.jupiter.api.*
import org.openqa.selenium.WebDriver
import org.openqa.selenium.chrome.ChromeDriver
import org.springframework.beans.factory.annotation.Autowired
import java.io.File

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class TeamUrlExtractorTest {
    @Autowired
    private lateinit var webDriverFactory: WebDriverFactory
    private lateinit var driver: WebDriver
    private val extractor = TeamUrlExtractor()

    @BeforeAll
    fun setup() {
        driver = webDriverFactory.createDriver()
    }

    @AfterAll
    fun tearDown() {
        driver.quit()
    }

    @Test
    fun `debe extraer la url del primer equipo cuando hay resultados`() {
        val file = File("src/test/resources/static/page-with-results.html")
        driver.get(file.toURI().toString())
        val url = extractor.getFirstTeamUrl(driver, "Boca Juniors")
        Assertions.assertTrue(url.startsWith("https://es.whoscored.com"))
    }

    @Test
    fun `debe lanzar EntityNotFound cuando no hay resultados`() {
        val file = File("src/test/resources/static/page-without-results.html")
        driver.get(file.toURI().toString())
        Assertions.assertThrows(EntityNotFound::class.java) {
            extractor.getFirstTeamUrl(driver, "EquipoInexistente")
        }
    }
}
