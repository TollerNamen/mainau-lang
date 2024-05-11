plugins {
    id("java")
    application
}

group = "mainau"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    implementation("org.fusesource.jansi:jansi:2.4.1")
}

tasks.test {
    useJUnitPlatform()
}

tasks.run.configure {
    standardInput = System.`in`
}

application {
    mainClass = "mainau.repl.runtime.Main"
}