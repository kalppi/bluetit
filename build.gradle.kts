plugins {
    kotlin("jvm") version "2.2.21" apply false
}

allprojects {
    repositories {
        mavenCentral()
    }
}

subprojects {
    tasks.withType<Test> {
        outputs.upToDateWhen { false }
    }
}

tasks.register("bootRunAll") {
    group = "application"
    description = "Run all services concurrently"

    dependsOn(
        ":services:bff-service:bootRun",
        ":services:clip-service:bootRun",
        ":services:rendering-orchestrator-service:bootRun"
    )
}
