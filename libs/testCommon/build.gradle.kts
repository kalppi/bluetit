plugins {
	kotlin("jvm") version "2.2.21"
	kotlin("plugin.spring") version "2.2.21"
	id("org.springframework.boot") version "4.0.3"
	id("io.spring.dependency-management") version "1.1.7"
	kotlin("plugin.jpa") version "2.2.21"
	id("org.jetbrains.kotlin.plugin.noarg") version "2.2.21"
	id("java-test-fixtures")
}

group = "dev.jarno.bluetit"
version = "0.0.1-SNAPSHOT"
description = "Common"

java {
	toolchain {
		languageVersion = JavaLanguageVersion.of(21)
	}
}

repositories {
	mavenCentral()
}

dependencies {
	implementation("org.springframework.boot:spring-boot-starter-actuator")
	implementation("org.springframework.boot:spring-boot-starter-data-jpa")
	implementation("org.springframework.boot:spring-boot-starter-validation")
	implementation("org.springframework.boot:spring-boot-starter-webmvc")
	implementation("org.jetbrains.kotlin:kotlin-reflect")
	implementation("tools.jackson.module:jackson-module-kotlin")
	runtimeOnly("org.postgresql:postgresql")
	testFixturesImplementation("org.springframework.boot:spring-boot-starter-actuator-test")
	testFixturesImplementation("org.springframework.boot:spring-boot-starter-data-jpa-test")
	testFixturesImplementation("org.springframework.boot:spring-boot-starter-validation-test")
	testFixturesImplementation("org.springframework.boot:spring-boot-starter-webmvc-test")
	testFixturesImplementation("org.jetbrains.kotlin:kotlin-test-junit5")
	testRuntimeOnly("org.junit.platform:junit-platform-launcher")
	developmentOnly("org.springframework.boot:spring-boot-devtools")
	testFixturesImplementation("org.springframework.boot:spring-boot-starter-test")
	testFixturesImplementation(kotlin("test"))
	testFixturesImplementation(platform("org.testcontainers:testcontainers-bom:1.19.8"))
	testFixturesImplementation("org.testcontainers:junit-jupiter")
	testFixturesImplementation("org.testcontainers:postgresql")
}

tasks.test {
	jvmArgs("-XX:+EnableDynamicAgentLoading")
}

allOpen {
	annotation("jakarta.persistence.Entity")
	annotation("jakarta.persistence.MappedSuperclass")
	annotation("jakarta.persistence.Embeddable")
}

noArg {
	annotation("jakarta.persistence.Entity")
}

tasks.withType<Test> {
	useJUnitPlatform()
}
