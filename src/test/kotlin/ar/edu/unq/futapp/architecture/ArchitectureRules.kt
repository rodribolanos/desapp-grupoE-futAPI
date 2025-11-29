package ar.edu.unq.futapp.architecture

import com.tngtech.archunit.core.domain.JavaClasses
import com.tngtech.archunit.core.importer.ClassFileImporter
import com.tngtech.archunit.library.Architectures
import org.junit.jupiter.api.Test

class ArchitectureRules {
    private val classes: JavaClasses = ClassFileImporter().importPackages("ar.edu.unq.futapp")

    @Test
    fun correctServiceDependencies() {
        Architectures.layeredArchitecture()
            .consideringOnlyDependenciesInLayers()
            .layer("Controllers").definedBy("..controller..")
            .layer("DTOs").definedBy("..dto..")
            .layer("Services").definedBy("..service..")
            .layer("Repositories").definedBy("..repository..")
            .layer("Models").definedBy("..model..")
            .whereLayer("Controllers").mayNotBeAccessedByAnyLayer()
            .whereLayer("DTOs").mayOnlyBeAccessedByLayers("Controllers")
            .whereLayer("Services").mayOnlyBeAccessedByLayers("Controllers")
            .whereLayer("Repositories").mayOnlyBeAccessedByLayers("Services")
            .whereLayer("Models").mayOnlyBeAccessedByLayers("Repositories", "Services", "Controllers", "DTOs")
            .check(classes)
    }
}