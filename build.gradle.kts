import ru.endlesscode.bukkitgradle.dependencies.papermc

plugins {
    alias(libs.plugins.paperweight) apply false
    alias(libs.plugins.bukkitgradle)
    `java-library`
}

allprojects {

    group = "me.fortibrine"
    version = "1.0"

    repositories {
        mavenCentral()

        papermc()
    }
}

subprojects {

    val libs = rootProject.libs

    apply {
        plugin("java-library")
        plugin(libs.plugins.bukkitgradle.get().pluginId)
    }

    tasks {
        withType<JavaCompile>().configureEach {
            options.encoding = "UTF-8"
            targetCompatibility = "17"
            sourceCompatibility = "17"
        }

        jar {
            duplicatesStrategy = DuplicatesStrategy.EXCLUDE
            from (
                configurations.runtimeClasspath.get().map { if (it.isDirectory) it else zipTree(it) }
            )
        }
    }

    dependencies {

        compileOnly(libs.lombok)
        annotationProcessor(libs.lombok)

        compileOnly(libs.jetbrains.annotations)
        annotationProcessor(libs.jetbrains.annotations)
    }
}

tasks {

    copyPlugins.configure {

        actions.clear()

        dependsOn(
            ":plugin:build",
            ":api:build"
        )

        doLast {
            File(
                "$rootDir/plugin/build/libs/plugin-1.0.jar"
            ).copyTo(
                target = File("$destinationDir/plugin-1.0.jar"),
                overwrite = true,
                bufferSize = 4096
            )

            File(
                "$rootDir/api/build/libs/api-1.0.jar"
            ).copyTo(
                target = File("$destinationDir/api-1.0.jar"),
                overwrite = true,
                bufferSize = 4096
            )
        }
    }
}

bukkit {

    apiVersion = "1.19.4"

    server {
        setCore("paper")
        eula = true
        onlineMode = true
        encoding = "UTF-8"
        javaArgs("-Xmx1G")
        bukkitArgs("nogui")
    }
}
