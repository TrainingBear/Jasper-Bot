plugins {
    id("java")
}

group = "com.trbear9"
version = "1.0.0"

repositories {
    mavenCentral()
}

dependencies {
    implementation("net.dv8tion:JDA:5.3.0") // main core basic necessity

    implementation("org.reflections:reflections:0.10.2") // reflection for QoL auto add feature
    implementation("com.fasterxml.jackson.core:jackson-databind:2.22.0") // 

    implementation("org.xerial:sqlite-jdbc:3.53.2.0") // table database especially for weather feature
    implementation("org.xhtmlrenderer:flying-saucer-core:10.2.0") // HTML -> BufferedImage
    implementation("io.pebbletemplates:pebble:4.1.2") // HTML template handle
}

tasks.jar {
    manifest {
        attributes(
            "Main-Class" to "jasper.Main",
            "Class-Path" to configurations.runtimeClasspath.get()
                .files
                .joinToString(" ") { "lib/${it.name}" }
        )
    }
}

tasks.register<Copy>("release") {
    dependsOn(tasks.jar)

    into(layout.buildDirectory.dir("release"))

    from(tasks.jar)

    into("lib") {
        from(configurations.runtimeClasspath)
    }
}