plugins {
	kotlin("jvm") version "2.2.21"
	kotlin("plugin.spring") version "2.2.21"
	id("org.springframework.boot") version "4.0.3"
	id("io.spring.dependency-management") version "1.1.7"
	id("com.github.node-gradle.node") version "7.1.0"
}

group = "dev.jarno.bluetit"
version = "0.0.1-SNAPSHOT"
description = "BFF service - Backend for Frontend"

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
	implementation("org.springframework.boot:spring-boot-starter-webmvc")
	implementation("org.jetbrains.kotlin:kotlin-reflect")
	implementation("tools.jackson.module:jackson-module-kotlin")
	runtimeOnly("org.postgresql:postgresql")
	testImplementation("org.springframework.boot:spring-boot-starter-webmvc-test")
	testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")
	testRuntimeOnly("org.junit.platform:junit-platform-launcher")
	developmentOnly("org.springframework.boot:spring-boot-devtools")
	testImplementation("org.springframework.boot:spring-boot-starter-test")
	testImplementation(kotlin("test"))
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

// Node/NPM configuration for Vue frontend build
node {
	version.set("20.11.0")
	npmVersion.set("10.2.3")
	download.set(true)
	nodeProjectDir.set(file("src/main/frontend"))
}

// Build Vue frontend
tasks.register<com.github.gradle.node.npm.task.NpmTask>("buildVue") {
	dependsOn("npm_install")
	args.set(listOf("run", "build"))
	inputs.dir("src/main/frontend/src")
	inputs.file("src/main/frontend/package.json")
	outputs.dir("src/main/frontend/dist")
}

// Copy built Vue app to resources
tasks.register<Copy>("copyVueApp") {
	dependsOn("buildVue")
	from("src/main/frontend/dist")
	into("${project.layout.buildDirectory.get()}/resources/main/static")
}

// Ensure resources are processed after copying Vue app
tasks.processResources {
	dependsOn("copyVueApp")
}

// Ensure JAR includes the copied resources
tasks.bootJar {
	dependsOn("copyVueApp")
}




