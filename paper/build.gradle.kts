plugins {
    alias(libs.plugins.spigot.dependency.loader)
}

repositories {
    maven("https://repo.papermc.io/repository/maven-public/")

    maven("https://repo.rosewooddev.io/repository/public/") // RoseStacker repository

    maven("https://repo.extendedclip.com/content/repositories/placeholderapi/")

    maven("https://repo.betonquest.org/betonquest/") // BetonQuest Repository

    maven("https://nexus.hc.to/content/repositories/pub_releases") // Vault Repository

    maven("https://mvn.lumine.io/repository/maven-public/") // MythicMobs Repository

    maven("https://repo.oraxen.com/releases") // Oraxen Repository

    maven("https://jitpack.io") // Used for ItemsAdder

    maven("https://repo.auxilor.io/repository/maven-public/") // Eco Repository

    maven("https://maven.enginehub.org/repo/") // WorldGuard Repository

    maven("https://repo.nexomc.com/releases") // Nexo Repository

    maven {
        name = "faststatsReleases"
        url = uri("https://repo.faststats.dev/releases")
    }
}

dependencies {
    val kotlinGroupId = "org.jetbrains.kotlin"
    compileOnly(kotlin("stdlib"))

    // Plugin APIs provided by the server / installed plugins at runtime (compile-only)
    compileOnly(libs.paper.api)

    compileOnly(libs.miniplaceholders.api)
    compileOnly(libs.miniplaceholders.kotlin.ext)

    compileOnly(libs.rosestacker)
    compileOnly(libs.headdatabase.api)
    compileOnly(libs.placeholderapi)
    compileOnly(libs.vault.api)
    compileOnly(libs.mythic.dist)
    compileOnly(libs.oraxen)
    compileOnly(libs.itemsadder.api)
    compileOnly(libs.eco)
    compileOnly(libs.ecomobs)
    compileOnly(libs.betonquest) {
        exclude("dev.faststats.metrics", "bukkit")
        exclude("de.themoep", "minedown-adventure")
    }
    compileOnly(libs.worldguard.bukkit)
    compileOnly(libs.nexo)

    // Runtime libraries loaded via the Spigot/Paper library loader (Maven Central only).
    // These are downloaded at runtime, so they must NOT be shaded or relocated.
    spigot("org.jetbrains.kotlin", "kotlin-stdlib", libs.versions.kotlin.get())
    spigot("org.jetbrains.kotlin", "kotlin-reflect", libs.versions.kotlin.get())

    spigot(libs.exposed.core)
    spigot(libs.exposed.dao)
    spigot(libs.exposed.jdbc)

    spigot(libs.sqlite.jdbc)
    spigot(libs.mariadb.java.client)
    spigot(libs.hikaricp)
    spigot(libs.postgresql)

    spigot(libs.cloud.core)
    spigot(libs.cloud.minecraft.extras)
    spigot(libs.cloud.paper)
    spigot(libs.cloud.kotlin.coroutines)

    spigot(libs.boosted.yaml)
    spigot(libs.caffeine)
    spigot(libs.versioncompare)
    spigot(libs.ryseinventory.plugin)

    spigot(libs.mongodb.driver.kotlin.coroutine)
    spigot(libs.bson.kotlinx)


    spigot(libs.mccoroutine.folia.api)
    spigot(libs.mccoroutine.folia.core)

    // Shaded into the final jar via shadowJar
    implementation(libs.faststats.bukkit)
}
