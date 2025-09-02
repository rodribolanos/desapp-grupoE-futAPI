package ar.edu.unq.futapp

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class FutappApplication

fun main(args: Array<String>) {
	runApplication<FutappApplication>(*args)
}
