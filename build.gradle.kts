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

