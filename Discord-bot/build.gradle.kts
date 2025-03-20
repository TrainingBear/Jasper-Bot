plugins {
    id("java")
}

group = "org.example"
version = "1.0.0"

repositories {
    mavenCentral()
}

dependencies {
    implementation("net.dv8tion:JDA:5.3.0")
    implementation ("com.googlecode.json-simple:json-simple:1.1.1")
}
