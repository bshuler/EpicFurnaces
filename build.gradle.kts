import org.gradle.jvm.toolchain.JavaLanguageVersion

plugins {
    java
    jacoco
    id("com.gradleup.shadow") version "9.6.1"
}

group = "com.songoda"
version = providers.gradleProperty("pluginVersion").get()

// Java 25: the latest resolvable paper-api (26.2.build.111-stable, matching
// Minecraft's current calendar-versioned release) publishes Gradle module
// metadata requiring JVM 25 - discovered live via a failed compileClasspath
// resolution against a Java 21 toolchain, not assumed in advance. The
// foojay-resolver-convention plugin (declared in settings.gradle.kts)
// auto-provisions this JDK into Gradle's own toolchain cache; it does not
// touch the system/Homebrew JDK 21 install. See PLAN.md/CLAUDE.md.
java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(25))
    }
}

repositories {
    mavenCentral()
    maven("https://repo.papermc.io/repository/maven-public/")
    maven("https://jitpack.io")
    maven("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
}

dependencies {
    compileOnly("io.papermc.paper:paper-api:${providers.gradleProperty("paperApiVersion").get()}")
    // VaultAPI transitively pulls a pre-Mojang-mapped CraftBukkit snapshot
    // (org.bukkit:bukkit:1.13.1-R0.1-SNAPSHOT) whose "bukkit" capability
    // conflicts with the one paper-api provides. Only the Vault economy
    // interfaces are used here, so the transitive Bukkit dependency is
    // excluded rather than letting the two resolve against each other.
    compileOnly("com.github.MilkBowl:VaultAPI:1.7") {
        exclude(group = "org.bukkit", module = "bukkit")
    }

    // Test infrastructure. JUnit (Jupiter) for everything; MockBukkit for
    // simulating a Bukkit server so plugin/listener/manager code touching
    // org.bukkit.* can be exercised without a real server. MockBukkit
    // renamed its Maven group from `com.github.seeseemelk` to
    // `org.mockbukkit.mockbukkit` (still published live on Maven Central,
    // confirmed via repo1.maven.org); the newest per-version artifact under
    // the new group is `mockbukkit-v26.1.2`. Tests deliberately pin
    // `paper-api:26.1.2.build.74-stable` here rather than inheriting
    // whichever `-PpaperApiVersion` the main sourceSet is compiled against:
    // MockBukkit ships its own bundled registry-data snapshot (materials,
    // items, etc.) captured from that exact Paper build, and letting Gradle
    // resolve a newer paper-api (e.g. the default 26.2.build.111-stable)
    // onto the test classpath produced a real
    // `InternalDataLoadException: ... MockBukkit / MC version mismatch?` at
    // test-runtime - not a hypothetical concern. So: tests run against
    // exactly ONE fixed target, 26.1.2, independent of whatever the main
    // build is targeting. It transitively requires JUnit 6 (JUnit 5 ended
    // at 5.14.4; 6.1.x is current as of this writing), so the JUnit BOM is
    // pinned there too.
    testImplementation(platform("org.junit:junit-bom:6.1.3"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
    testImplementation("io.papermc.paper:paper-api:26.1.2.build.74-stable")
    testImplementation("org.mockbukkit.mockbukkit:mockbukkit-v26.1.2:4.115.0")
    // Deliberately NOT extending testImplementation from compileOnly: doing
    // so would let Gradle's "highest version wins" conflict resolution pull
    // the main sourceSet's paper-api (26.2, or whatever -PpaperApiVersion
    // is) back onto the test classpath ahead of the pinned 26.1.2 above,
    // silently reintroducing the MockBukkit data-mismatch from the comment
    // above. VaultAPI is duplicated here instead for the same reason.
    testImplementation("com.github.MilkBowl:VaultAPI:1.7") {
        exclude(group = "org.bukkit", module = "bukkit")
    }
}

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
    options.release.set(25)
}

tasks.shadowJar {
    archiveClassifier.set("")
    archiveBaseName.set("EpicFurnaces")
}

tasks.build {
    dependsOn(tasks.shadowJar)
}

tasks.processResources {
    val props = mapOf("version" to project.version)
    filesMatching("plugin.yml") {
        expand(props)
    }
}

tasks.test {
    useJUnitPlatform()
    finalizedBy(tasks.jacocoTestReport)
}

// Classes that are not included in the enforced 100% coverage bar. Each
// exclusion is documented with its reason in PLAN.md ("Test coverage" /
// exclusions table) - never silent. Coverage for these classes is still
// measured and visible in the HTML/XML report, just not gated by `check`.
val jacocoExcludes = listOf(
    "com/songoda/epicfurnaces/EpicFurnacesPlugin*",
    "com/songoda/epicfurnaces/Locale*",
    "com/songoda/epicfurnaces/References*",
    "com/songoda/epicfurnaces/furnace/EFurnace*",
    "com/songoda/epicfurnaces/utils/SettingsManager*",
    "com/songoda/epicfurnaces/listeners/**",
    "com/songoda/epicfurnaces/command/**",
    "com/songoda/epicfurnaces/hooks/**"
)

tasks.jacocoTestReport {
    dependsOn(tasks.test)
    reports {
        xml.required.set(true)
        html.required.set(true)
    }
    classDirectories.setFrom(
        files(classDirectories.files.map {
            fileTree(it) { exclude(jacocoExcludes) }
        })
    )
}

tasks.jacocoTestCoverageVerification {
    dependsOn(tasks.jacocoTestReport)
    classDirectories.setFrom(
        files(classDirectories.files.map {
            fileTree(it) { exclude(jacocoExcludes) }
        })
    )
    violationRules {
        rule {
            limit {
                counter = "LINE"
                minimum = "1.00".toBigDecimal()
            }
        }
    }
}

tasks.check {
    dependsOn(tasks.jacocoTestCoverageVerification)
}
