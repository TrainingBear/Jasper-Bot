plugins {
    id("java")
    id("com.github.johnrengelman.shadow") version "8.1.1"
}

group = "com.trbear9"
version = "1.0.0"

repositories {
    mavenCentral()
}

dependencies {
    implementation("net.dv8tion:JDA:5.3.0")
    implementation("com.googlecode.json-simple:json-simple:1.1.1")
}

tasks.jar {
    manifest {
        attributes(
            "Main-Class" to "Jasper.Main"
        )
    }
}

tasks {
    shadowJar {
        manifest {
            attributes(
                "Main-Class" to "Jasper.Main"
            )
        }
    }
}