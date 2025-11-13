package ar.edu.unq.futapp.architecture

import com.tngtech.archunit.core.domain.JavaClasses
import com.tngtech.archunit.core.importer.ClassFileImporter
import com.tngtech.archunit.lang.syntax.ArchRuleDefinition
import org.junit.jupiter.api.Test

class ServiceRules {
    private val classes: JavaClasses = ClassFileImporter().importPackages("ar.edu.unq.futapp")

    @Test
    fun servicesShouldNotDependOnControllers() {
        val rule = ArchRuleDefinition.noClasses()
            .that()
            .resideInAPackage("..service..")
            .should()
            .dependOnClassesThat()
            .resideInAPackage("..controller..")

        rule.check(classes)
    }

}