package com.loiane.architecture;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.classes;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.fields;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;
import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noMethods;
import static com.tngtech.archunit.library.dependencies.SlicesRuleDefinition.slices;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.Repository;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RestController;

import com.tngtech.archunit.core.importer.ImportOption;
import com.tngtech.archunit.junit.AnalyzeClasses;
import com.tngtech.archunit.junit.ArchTest;
import com.tngtech.archunit.lang.ArchRule;

@AnalyzeClasses(packages = "com.loiane", importOptions = ImportOption.DoNotIncludeTests.class)
class ArchitectureTest {

    // 1. Layer dependencies: lower layers must not depend on upper layers
    @ArchTest
    static final ArchRule servicesShouldNotDependOnControllers = noClasses()
            .that().haveSimpleNameEndingWith("Service")
            .should().dependOnClassesThat().haveSimpleNameEndingWith("Controller");

    @ArchTest
    static final ArchRule repositoriesShouldNotDependOnServicesOrControllers = noClasses()
            .that().haveSimpleNameEndingWith("Repository")
            .should().dependOnClassesThat().haveSimpleNameEndingWith("Service")
            .orShould().dependOnClassesThat().haveSimpleNameEndingWith("Controller");

    @ArchTest
    static final ArchRule repositoriesOnlyAccessedByServices = classes()
            .that().haveSimpleNameEndingWith("Repository")
            .should().onlyBeAccessed().byClassesThat().haveSimpleNameEndingWith("Service")
            .orShould().onlyBeAccessed().byClassesThat().haveSimpleNameEndingWith("Repository");

    // 2. No cross-feature leakage: shared code stays generic
    @ArchTest
    static final ArchRule sharedShouldNotDependOnFeatures = noClasses()
            .that().resideInAPackage("com.loiane.shared..")
            .should().dependOnClassesThat().resideInAPackage("com.loiane.course..");

    // 3. Annotation and naming conventions
    @ArchTest
    static final ArchRule controllersAreAnnotatedAndPlacedCorrectly = classes()
            .that().haveSimpleNameEndingWith("Controller")
            .and().resideOutsideOfPackage("com.loiane.shared..")
            .should().beAnnotatedWith(RestController.class)
            .andShould().resideInAPackage("com.loiane.course..");

    @ArchTest
    static final ArchRule servicesAreAnnotatedWithService = classes()
            .that().haveSimpleNameEndingWith("Service")
            .should().beAnnotatedWith(Service.class);

    @ArchTest
    static final ArchRule repositoriesAreSpringDataInterfaces = classes()
            .that().haveSimpleNameEndingWith("Repository")
            .should().beInterfaces()
            .andShould().beAssignableTo(Repository.class);

    // 4. DTO discipline: controllers expose DTOs, never JPA entities
    @ArchTest
    static final ArchRule controllersDoNotExposeEntities = noMethods()
            .that().areDeclaredInClassesThat().areAnnotatedWith(RestController.class)
            .and().arePublic()
            .should().haveRawReturnType(com.loiane.course.Course.class)
            .orShould().haveRawReturnType(com.loiane.course.Lesson.class)
            .orShould().haveRawParameterTypes(com.loiane.course.Course.class)
            .orShould().haveRawParameterTypes(com.loiane.course.Lesson.class);

    // 5. General hygiene
    @ArchTest
    static final ArchRule noFieldInjection = fields()
            .should().notBeAnnotatedWith(Autowired.class)
            .because("constructor injection is required");

    @ArchTest
    static final ArchRule noJavaUtilLogging = noClasses()
            .should().dependOnClassesThat().resideInAPackage("java.util.logging..");

    @ArchTest
    static final ArchRule noPackageCycles = slices()
            .matching("com.loiane.(*)..")
            .should().beFreeOfCycles();
}
