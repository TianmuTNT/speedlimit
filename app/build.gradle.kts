plugins {
    java
}

group = "me.robertlit"
version = "0.7.1"
description = "SpeedLimit"

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}

repositories {
    mavenCentral()
    maven("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
    maven("https://oss.sonatype.org/content/repositories/snapshots")
    maven("https://oss.sonatype.org/content/repositories/central")
}

dependencies {
    compileOnly("org.spigotmc:spigot-api:1.16.5-R0.1-SNAPSHOT")
}

tasks {
    processResources {
        filesMatching("plugin.yml") {
            expand(mapOf(
                "version" to project.version,
                "name" to project.description
            ))
        }
    }
    
    jar {
        archiveBaseName.set(project.description as String)
        archiveVersion.set(project.version as String)
        destinationDirectory.set(file("${project.rootDir}/server/plugins"))
    }
}
