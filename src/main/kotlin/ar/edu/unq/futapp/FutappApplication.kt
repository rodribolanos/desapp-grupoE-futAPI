package ar.edu.unq.futapp

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.cache.annotation.EnableCaching
import org.springframework.data.jpa.repository.config.EnableJpaRepositories
import org.springframework.scheduling.annotation.EnableAsync
import org.springframework.scheduling.annotation.EnableScheduling

@SpringBootApplication
@EnableAsync
@EnableJpaRepositories(basePackages = ["ar.edu.unq.futapp.repository"])
@EnableCaching
@EnableScheduling
class FutappApplication

fun main(args: Array<String>) {
    runApplication<FutappApplication>(*args)
}

// Trying testing!