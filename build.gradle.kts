plugins {
	java
	id("org.springframework.boot") version "3.3.4"
	id("io.spring.dependency-management") version "1.1.6"
}

group = "ru.pereguzochka"
version = "0.0.1-SNAPSHOT"

java {
	toolchain {
		languageVersion = JavaLanguageVersion.of(17)
	}
}

configurations {
	compileOnly {
		extendsFrom(configurations.annotationProcessor.get())
	}
}

repositories {
	mavenCentral()
}

dependencies {
	/**
	 * SpringBoot
	 */
	implementation("org.springframework.boot:spring-boot-starter")
	testImplementation("org.springframework.boot:spring-boot-starter-test")

	/**
	 * Utils
	 */
	compileOnly("org.projectlombok:lombok")
	annotationProcessor("org.projectlombok:lombok")

	/**
	 * Tests
	 */
	testRuntimeOnly("org.junit.platform:junit-platform-launcher")

	/**
	 * Telegram
	 */
	implementation("org.telegram:telegrambots:6.9.7.1")




}

tasks.withType<Test> {
	useJUnitPlatform()
}
