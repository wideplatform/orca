plugins {
    id("java")
}

group = "com.iostate"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation(project(":orca-api"))
    implementation(group = "org.freemarker", name = "freemarker", version = "2.3.32")
    implementation(group = "com.fasterxml.jackson.core", name = "jackson-databind", version = "2.16.1")
    implementation(group = "com.fasterxml.jackson.dataformat", name = "jackson-dataformat-yaml", version = "2.16.1")
    testImplementation(platform("org.junit:junit-bom:5.9.1"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testImplementation("com.h2database:h2:2.2.220")
    testImplementation("com.mysql:mysql-connector-j:8.2.0")
    testImplementation("org.postgresql:postgresql:42.7.3")
}

tasks.test {
    useJUnitPlatform()
}