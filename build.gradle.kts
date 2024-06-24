plugins {
    java
    checkstyle
    id("org.springframework.boot") version "3.3.0"
    id("io.spring.dependency-management") version "1.1.5"
    id("com.diffplug.spotless") version "6.18.0"
    id("org.flywaydb.flyway") version "7.15.0"
}

group = "org.abx"
version = "0.0.1-SNAPSHOT"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(17)
    }
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("com.diffplug.spotless:spotless-plugin-gradle:6.18.0")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-data-mongodb")

    annotationProcessor("org.immutables:value:2.9.3")
    compileOnly("org.immutables:value:2.9.3")
    runtimeOnly("org.postgresql:postgresql")

    // test
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

tasks.withType<Test> {
    useJUnitPlatform()
}

tasks.withType<Test> {
    useJUnitPlatform()
}

tasks.named<Checkstyle>("checkstyleMain").configure {
    source = fileTree("src/main/java")
}
tasks.named<Checkstyle>("checkstyleTest").configure {
    source = fileTree("src/test/java")
}

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
    options.release = 17
}

val checkstylePublicTask = tasks.register("checkstyle") {
    group = "verification"
    description = "Runs all Checkstyle checks."
}

tasks.withType<Checkstyle>().forEach { checkstyleTask ->
    checkstylePublicTask { dependsOn(checkstyleTask) }
}

spotless {
    isEnforceCheck = false
    java {
        palantirJavaFormat("2.28.0")
        targetExclude("**/build/generated/**")
    }
}

flyway {
    url = "jdbc:postgresql://localhost:5432/virtual_pet_db"
    user = "postgres"
    password = "postgres"
    schemas = arrayOf("virtual_pet_schema")
    locations = arrayOf("classpath:db.migration")
}