plugins {
    id("java")
}

group = "com.iostate"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
}

tasks.test {
    useJUnitPlatform()
}
