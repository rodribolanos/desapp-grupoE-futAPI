plugins {
	kotlin("jvm") version "1.9.25"
	kotlin("plugin.spring") version "1.9.25"
	id("org.springframework.boot") version "3.2.5"
	id("io.spring.dependency-management") version "1.1.7"
	kotlin("plugin.jpa") version "1.9.25"
	id("jacoco")
}

group = "ar.edu.unq"
version = "0.0.1-SNAPSHOT"
description = "Project for DESAPP"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

configurations {
    compileOnly {
        extendsFrom(configurations.annotationProcessor.get())
    }
}

springBoot {
    mainClass.set("ar.edu.unq.futapp.FutappApplicationKt")
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-data-redis")
    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-aop")
    implementation("org.hibernate.validator:hibernate-validator:8.0.1.Final")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.3.0")
    compileOnly("org.projectlombok:lombok")
    runtimeOnly("com.h2database:h2")
    runtimeOnly("org.postgresql:postgresql")
    annotationProcessor("org.projectlombok:lombok")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")
    testImplementation("org.springframework.security:spring-security-test")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
    implementation("io.jsonwebtoken:jjwt-api:0.11.5")
    runtimeOnly("io.jsonwebtoken:jjwt-impl:0.11.5")
    runtimeOnly("io.jsonwebtoken:jjwt-jackson:0.11.5")
    implementation("org.seleniumhq.selenium:selenium-java:4.35.0")
    testImplementation("org.jsoup:jsoup:1.21.2")
    testImplementation("io.mockk:mockk:1.14.5")
}

kotlin {
    compilerOptions {
        freeCompilerArgs.addAll("-Xjsr305=strict")
    }
}

allOpen {
    annotation("jakarta.persistence.Entity")
    annotation("jakarta.persistence.MappedSuperclass")
    annotation("jakarta.persistence.Embeddable")
}

// --- BLOQUE 1: CONFIGURACIÓN DE TESTS ---
tasks.withType<Test> {
	useJUnitPlatform()
	jvmArgs("--add-opens", "java.base/java.time=ALL-UNNAMED")

    // Correcto: asegura que el reporte se genere al finalizar los tests
	finalizedBy(tasks.named("jacocoTestReport"))
}


// --- BLOQUE 2: CONFIGURACIÓN DE JACOCO (Corregido) ---
tasks.named<JacocoReport>("jacocoTestReport") {
	// Asegura que los tests se hayan ejecutado ANTES de generar el reporte
	dependsOn(tasks.named<Test>("test"))

	reports {
		xml.required.set(true)
		html.required.set(true)
	}

    // Rutas correctas para Kotlin
	classDirectories.setFrom(files("$buildDir/classes/kotlin/main"))
	sourceDirectories.setFrom(files("src/main/kotlin"))
	executionData.setFrom(fileTree(buildDir) {
		include("**/jacoco/test.exec")
	})
}

// --- BLOQUE 3: APLICAR FILTROS (Movido) ---
afterEvaluate {
	tasks.named<JacocoReport>("jacocoTestReport") {
		classDirectories.setFrom(files(classDirectories.files.map {
			fileTree(it) {
				exclude(
					// Excluir clases que no quieren ser testeadas (DTOs, App, Config)
					"**/config/**",
					"**/dto/**",
					"**/ar/edu/unq/futapp/FutappApplicationKt.class",
					"**/*\$*.class"
				)
			}
		}))
	}
}
