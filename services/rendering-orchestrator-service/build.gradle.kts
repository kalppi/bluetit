plugins {
	kotlin("jvm") version "2.2.21"
	kotlin("plugin.spring") version "2.2.21"
	id("org.springframework.boot") version "4.0.3"
	id("io.spring.dependency-management") version "1.1.7"
	kotlin("plugin.jpa") version "2.2.21"
	kotlin("plugin.noarg") version "2.3.10"
}

group = "dev.jarno.bluetit"
version = "0.0.1-SNAPSHOT"
description = "Rendering Orchestrator - Routes clip requests to rendering services"

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
	implementation("org.springframework.boot:spring-boot-starter-web")
	implementation("org.springframework.boot:spring-boot-starter-amqp")
	implementation("org.springframework.boot:spring-boot-starter-data-jpa")
	implementation("org.jetbrains.kotlin:kotlin-reflect")
	implementation("tools.jackson.module:jackson-module-kotlin")
	runtimeOnly("org.postgresql:postgresql")
	developmentOnly("org.springframework.boot:spring-boot-devtools")
	testImplementation("org.springframework.boot:spring-boot-starter-test")
	testImplementation("org.springframework.amqp:spring-rabbit-test")
	testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")
	testRuntimeOnly("org.junit.platform:junit-platform-launcher")
	testImplementation(kotlin("test"))
	testImplementation(platform("org.testcontainers:testcontainers-bom:1.19.8"))
	testImplementation("org.testcontainers:junit-jupiter")
	testImplementation("org.testcontainers:rabbitmq")
	testImplementation("org.testcontainers:postgresql")
	testImplementation("org.mockito.kotlin:mockito-kotlin:6.2.3")
	testImplementation("org.awaitility:awaitility:4.2.0")
	implementation(project(":libs:outbox"))
	implementation(kotlin("stdlib-jdk8"))
}

tasks.test {
	jvmArgs("-XX:+EnableDynamicAgentLoading")
}

kotlin {
	compilerOptions {
		freeCompilerArgs.addAll("-Xjsr305=strict", "-Xannotation-default-target=param-property")
	}
}

tasks.withType<Test> {
	useJUnitPlatform()
}


