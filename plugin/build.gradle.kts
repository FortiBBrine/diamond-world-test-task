plugins {
    alias(libs.plugins.paperweight)
}

dependencies {
    compileOnly(project(":api"))

    paperweight.paperDevBundle(libs.versions.paper.get())
}

tasks {

    assemble {
        dependsOn(reobfJar)
    }

    bukkit {
        apiVersion = "1.19.4"

        meta {
            name.set("BossPlugin")
            version.set(rootProject.version.toString())
            main.set("me.fortibrine.boss.BossPlugin")
        }

    }
}