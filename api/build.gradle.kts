
plugins {
    alias(libs.plugins.paperweight)
}

dependencies {
    implementation(libs.ormlite)
    implementation(libs.sqlite)

    paperweight.paperDevBundle(libs.versions.paper.get())
}

tasks {
    assemble {
        dependsOn(reobfJar)
    }

    bukkit {
        apiVersion = "1.19.4"

        meta {
            name.set("BossApi")
            version.set(rootProject.version.toString())
            main.set("me.fortibrine.boss.api.BossApi")
        }

    }
}