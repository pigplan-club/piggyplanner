import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import info.solidsoft.gradle.pitest.PitestTask

plugins {
    id("org.springframework.boot") version "2.2.6.RELEASE"
    id("io.spring.dependency-management") version "1.0.9.RELEASE"
    id("com.google.cloud.tools.jib") version "2.2.0"
    id("info.solidsoft.pitest") version "1.4.8"
    kotlin("jvm") version "1.3.71"
    kotlin("plugin.spring") version "1.3.71"
}

group = "club.piggyplanner"
version = "0.0.1-SNAPSHOT"
java.sourceCompatibility = JavaVersion.VERSION_11

configurations {
    compileOnly {
        extendsFrom(configurations.annotationProcessor.get())
    }
}

repositories {
    mavenCentral()
}

dependencies {
    //Spring boot webflux
    implementation("org.springframework.boot:spring-boot-starter-webflux")

    //Mongo DB
    implementation("org.springframework.boot:spring-boot-starter-data-mongodb-reactive")

    //Kotiln
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("io.projectreactor.kotlin:reactor-kotlin-extensions")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-reactor")

    //Axon framework
    implementation("org.axonframework:axon-spring-boot-starter:4.3.1")
    implementation("org.axonframework.extensions.mongo:axon-mongo:4.2")

    //GraphQL
    implementation("com.expediagroup:graphql-kotlin-spring-server:2.0.0")
    implementation("com.expediagroup:graphql-kotlin-schema-generator:2.0.0")

    //Testing
    testImplementation("org.springframework.boot:spring-boot-starter-test") {
        exclude(group = "org.junit.vintage", module = "junit-vintage-engine")
    }
    testImplementation("io.projectreactor:reactor-test")
    testImplementation("com.shazam:shazamcrest:0.11")

    //Mongo db testing
    testImplementation("de.flapdoodle.embed:de.flapdoodle.embed.mongo")

    //Axon framework testing
    testImplementation("org.axonframework:axon-test:4.3.1")

    //Pitest extension for junit5
    testImplementation("org.pitest:pitest-junit5-plugin:0.12")
}

jib {
    to {
        image = "pigplanclub/piggyplanner-services"
        tags = setOf(System.getenv("BUILD_VERSION")?:"$version")
    }
}

tasks {
    withType<Test> {
        useJUnitPlatform()
    }

    withType<KotlinCompile> {
        kotlinOptions {
            freeCompilerArgs = listOf("-Xjsr305=strict")
            jvmTarget = "1.8"
        }
    }

    withType<PitestTask> {
        testPlugin.set("junit5")
        threads.set(1)
        outputFormats.set(setOf("HTML"))
        mutators.set(setOf("STRONGER", "DEFAULTS"))
        avoidCallsTo.set(setOf("kotlin.jvm.internal", "kotlinx.coroutines"))
//        targetClasses.set(setOf("club.piggyplanner.services.*"))
        targetClasses.set(setOf("club.piggyplanner.services.account.domain.model.*"))
    }

    test {
        if (System.getenv("EXCLUDE_IT") == "true") {
            exclude("**/presentation*")
        }
    }

//    named("build") {
//        dependsOn("test", "pitest")
//    }
}