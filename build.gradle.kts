plugins {
    java
    checkstyle
    id("org.springframework.boot") version "3.3.0"
    id("io.spring.dependency-management") version "1.1.5"
    id("com.diffplug.spotless") version "6.18.0"
    id("org.flywaydb.flyway") version "10.15.2"
}
val springAiVersion by extra("1.0.0-M1")



group = "org.abx"
version = "0.0.1-SNAPSHOT"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(17)
    }
}

repositories {
    mavenCentral()
    maven {
        url = uri("https://repo.spring.io/snapshot")
        name = "Spring Snapshots"
        content {
            includeGroupByRegex("org\\.springframework.*")
            includeGroupByRegex("io\\.spring.*")
        }
    }
    maven {
        url = uri("https://repo.spring.io/milestone")
        name = "Spring Milestones"
        content {
            includeGroupByRegex("org\\.springframework.*")
            includeGroupByRegex("io\\.spring.*")
        }
    }
}



dependencies {
    implementation("org.springframework.boot:spring-boot-starter")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("com.diffplug.spotless:spotless-plugin-gradle:6.18.0")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-data-mongodb")
    implementation("javax.annotation:javax.annotation-api:1.3.2")
    runtimeOnly("org.postgresql:postgresql")


    // spring ai
    implementation("org.springframework.ai:spring-ai-openai-spring-boot-starter")
    // kafka
    implementation("org.springframework.kafka:spring-kafka:3.2.2")

    // aws
    implementation(platform("software.amazon.awssdk:bom:2.25.57"))
    // s3
    implementation("software.amazon.awssdk:s3")
    // sqs
    implementation("software.amazon.awssdk:sqs")
    
    // S3Mock
    testImplementation("software.amazon.awssdk:url-connection-client:2.17+")
    testImplementation("com.adobe.testing:s3mock:3.3.0")
    testImplementation("com.adobe.testing:s3mock-testcontainers:3.3.0")
    testImplementation("org.testcontainers:junit-jupiter:1.19.0")

    // S3Mock
    testImplementation("software.amazon.awssdk:url-connection-client:2.17+")
    testImplementation("com.adobe.testing:s3mock:3.3.0")
    testImplementation("com.adobe.testing:s3mock-testcontainers:3.3.0")
    testImplementation("org.testcontainers:junit-jupiter:1.19.0")


    annotationProcessor("org.immutables:value:2.9.3")
    compileOnly("org.immutables:value:2.9.3")


    // test
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("com.h2database:h2")
    testImplementation("org.awaitility:awaitility:3.0.0")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")

    // S3Mock
    testImplementation("software.amazon.awssdk:url-connection-client:2.17+")
    testImplementation("com.adobe.testing:s3mock:3.3.0")
    testImplementation("com.adobe.testing:s3mock-testcontainers:3.3.0")
    testImplementation("org.testcontainers:junit-jupiter:1.19.0")
    testImplementation("org.testcontainers:postgresql:1.20.0")

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
dependencyManagement {
    imports {
        mavenBom("org.springframework.ai:spring-ai-bom:$springAiVersion")
    }
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