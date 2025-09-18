package ar.edu.unq.futapp

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.data.jpa.repository.config.EnableJpaRepositories

@SpringBootApplication
@EnableJpaRepositories(basePackages = ["ar.edu.unq.futapp.repository"])
class FutappApplication

fun main(args: Array<String>) {
    runApplication<FutappApplication>(*args)
}
