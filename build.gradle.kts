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
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.3")
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

// referencia segura a buildDirectory
val buildDirFile = layout.buildDirectory.get().asFile

// --- BLOQUE 1: CONFIGURACIÓN DE TESTS ---
tasks.withType<Test> {
	useJUnitPlatform()
	jvmArgs("--add-opens", "java.base/java.time=ALL-UNNAMED")

	testLogging {
		events("started", "passed", "skipped", "failed")
		exceptionFormat = org.gradle.api.tasks.testing.logging.TestExceptionFormat.FULL
		showStandardStreams = true
	}
}

tasks.withType<Test>().configureEach {
    val testTaskName = name
    extensions.configure(org.gradle.testing.jacoco.plugins.JacocoTaskExtension::class.java) {
        setDestinationFile(layout.buildDirectory.file("jacoco/$testTaskName.exec").get().asFile)
    }
}


// --- BLOQUE 2: CONFIGURACIÓN DE REPORTE JACOCO ---
tasks.named<JacocoReport>("jacocoTestReport") {
	// Asegura que los tests se hayan ejecutado ANTES de generar el reporte
	dependsOn(tasks.named<Test>("test"))

	reports {
		xml.required.set(true)
		html.required.set(true)
	}

	// Rutas correctas para Kotlin
	classDirectories.setFrom(files(layout.buildDirectory.dir("classes/kotlin/main").get().asFile))
	sourceDirectories.setFrom(files("src/main/kotlin"))
	// Usar solo el .exec del task 'test' para que no mezcle con unit/e2e
	executionData.setFrom(layout.buildDirectory.file("jacoco/test.exec"))
}

// --- BLOQUE 3: APLICAR FILTROS AL REPORTE ---
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

// --- BLOQUE 4: VALIDACIÓN DE COBERTURA (BASE) ---
tasks.named<JacocoCoverageVerification>("jacocoTestCoverageVerification") {
	// Le decimos que se ejecute DESPUÉS del reporte
	dependsOn(tasks.named("jacocoTestReport"))

	violationRules {
		rule {
			element = "BUNDLE"
			limit {
				counter = "LINE"
				value = "COVEREDRATIO"
				minimum = "0.70".toBigDecimal() // ¡Tu 70%!
			}
		}
	}

	// Debemos aplicar los MISMOS filtros que en jacocoTestReport
	classDirectories.setFrom(files("$buildDir/classes/kotlin/main"))
	sourceDirectories.setFrom(files("src/main/kotlin"))
	executionData.setFrom(fileTree(buildDir) {
		include("**/jacoco/*.exec")
	})
}

// --- BLOQUE 5: APLICAR FILTROS A LA VALIDACIÓN ---
afterEvaluate {
	tasks.named<JacocoCoverageVerification>("jacocoTestCoverageVerification") {
		classDirectories.setFrom(files(classDirectories.files.map {
			fileTree(it) {
				exclude(
					"**/config/**",
					"**/dto/**",
					"**/ar/edu/unq/futapp/FutappApplicationKt.class",
					"**/*\$*.class"
				)
			}
		}))
	}
}

tasks.register<Test>("unitTest") {
	group = "verification"
	description = "Ejecuta los tests unitarios (excluye Integration/E2E)."
	useJUnitPlatform()
	jvmArgs("--add-opens", "java.base/java.time=ALL-UNNAMED")

	include("**/ar/**/unit/**")
    exclude("**/ar/**/integration/**")

	finalizedBy("jacocoUnitTestReport")
}

tasks.register<JacocoReport>("jacocoUnitTestReport") {
	group = "verification"
	description = "Genera reporte JaCoCo para unitTest"
	dependsOn(tasks.named<Test>("unitTest"))
	reports {
		xml.required.set(true)
		html.required.set(true)
	}
	classDirectories.setFrom(files(layout.buildDirectory.dir("classes/kotlin/main").get().asFile))
	sourceDirectories.setFrom(files("src/main/kotlin"))
	executionData.setFrom(layout.buildDirectory.file("jacoco/unitTest.exec"))
}

tasks.register<Test>("e2eTest") {
	group = "verification"
	description = "Ejecuta los tests de integración / e2e (IntegrationTest/E2ETest)."
	useJUnitPlatform()
	jvmArgs("--add-opens", "java.base/java.time=ALL-UNNAMED")

	include("**/ar/**/integration/**")
    exclude("**/ar/**/unit/**")

	finalizedBy("jacocoE2eTestReport")
}

tasks.register<JacocoReport>("jacocoE2eTestReport") {
	group = "verification"
	description = "Genera reporte JaCoCo para e2eTest"
	dependsOn(tasks.named<Test>("e2eTest"))
	reports {
		xml.required.set(true)
		html.required.set(true)
	}
	classDirectories.setFrom(files(layout.buildDirectory.dir("classes/kotlin/main").get().asFile))
	sourceDirectories.setFrom(files("src/main/kotlin"))
	executionData.setFrom(layout.buildDirectory.file("jacoco/e2eTest.exec"))
}

tasks.register("listUnitTests") {
	group = "verification"
	description = "Lista las clases de test que coinciden con el patrón de unitTest"
	doLast {
		val classesDir = layout.buildDirectory.dir("classes/kotlin/test").get().asFile
		if (!classesDir.exists()) {
			println("Directorios de clases de test no encontrados: ${classesDir}. Ejecutá 'gradle test' para compilar tests primero.")
			return@doLast
		}
		val matched = fileTree(classesDir).matching {
			include("**/ar/**/unit/**")
            exclude("**/ar/**/integration/**")
		}.files.sorted()
		println("unitTest matched classes: ${matched.size}")
		matched.forEach { println(it.relativeTo(classesDir).path) }
	}
}

tasks.register("listE2eTests") {
	group = "verification"
	description = "Lista las clases de test que coinciden con el patrón de e2eTest"
	doLast {
		val classesDir = layout.buildDirectory.dir("classes/kotlin/test").get().asFile
		if (!classesDir.exists()) {
			println("Directorios de clases de test no encontrados: ${classesDir}. Ejecutá 'gradle test' para compilar tests primero.")
			return@doLast
		}
		val matched = fileTree(classesDir).matching {
			include("**/ar/**/integration/**")
			exclude("**/ar/**/unit/**")
		}.files.sorted()
		println("e2eTest matched classes: ${matched.size}")
		matched.forEach { println(it.relativeTo(classesDir).path) }
	}
}
