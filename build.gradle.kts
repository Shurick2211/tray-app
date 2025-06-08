plugins {
    kotlin("jvm") version "1.9.25"
    kotlin("plugin.spring") version "1.9.25"
    id("org.springframework.boot") version "3.5.0"
    id("io.spring.dependency-management") version "1.1.7"
    application
}

group = "com.nimko"
version = "1.0.0"

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(17))
    }
}

repositories {
    mavenCentral()
}

val osName = System.getProperty("os.name").lowercase()
val platform = when {
    osName.contains("win") -> "win"
    osName.contains("mac") -> "mac"
    else -> "linux"
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    developmentOnly("org.springframework.boot:spring-boot-devtools")
    runtimeOnly("com.h2database:h2")

    implementation("no.tornado:tornadofx:1.7.20")

    implementation("commons-collections:commons-collections:3.2.2")
    implementation("io.vavr:vavr:0.10.4")
    implementation("org.apache.commons:commons-lang3:3.14.0")

    // JavaFX platform-specific dependencies
    implementation("org.openjfx:javafx-fxml:20:$platform")
    implementation("org.openjfx:javafx-base:20:$platform")
    implementation("org.openjfx:javafx-controls:20:$platform")
    implementation("org.openjfx:javafx-graphics:20:$platform")

    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

application {
    mainClass.set("com.nimko.trayapp.TrayAppApplication")
}

tasks.withType<Test> {
    useJUnitPlatform()
}

tasks.withType<JavaExec>().configureEach {
    jvmArgs = listOf(
        "--add-modules", "javafx.controls,javafx.fxml"
    )
}
