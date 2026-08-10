import org.gradle.api.artifacts.ExternalModuleDependency
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
    // WorldGuard/WorldEdit (hooks/HookWorldGuard.java).
    maven("https://maven.enginehub.org/repo/")
    // RedProtect-Spigot (hooks/HookRedProtect.java) transitively needs
    // Sponge's Configurate library.
    maven("https://repo.spongepowered.org/repository/maven-public/")
    // Towny (hooks/HookTowny.java) is not published to Maven Central or
    // JitPack; this is its own maintainer-run repository.
    maven("https://repo.glaremasters.me/repository/towny/")
    // ASkyBlock (hooks/HookASkyBlock.java) is published here, not to Maven
    // Central or JitPack.
    maven("https://repo.codemc.org/repository/maven-public/")
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

    // Protection-plugin hooks (com/songoda/epicfurnaces/hooks/) - each is
    // registered as a runtime softdepend (see plugin.yml and
    // EpicFurnacesPlugin#onEnable), guarded by a
    // Bukkit.getPluginManager().getPlugin(...) null check, so none of these
    // are required at runtime; compileOnly is correct. See PLAN.md/CLAUDE.md
    // for the per-hook Maven-coordinate research (what resolves cleanly,
    // what doesn't, and why) behind this list - 3 of the original 9 hooks
    // (Factions, Kingdoms, uSkyBlock) could not be revived and remain
    // unbuilt under legacy-hooks/ for that reason.
    // worldguard-bukkit/-core/worldedit-core each declare a `strictly`
    // Guava/Gson version constraint under a "Mojang provides Guava/Gson"
    // rationale, pinned to the Minecraft version WorldGuard 7.0.18 actually
    // targets. That strict pin is incompatible with the much newer Guava/
    // Gson paper-api's own Gradle module metadata requires for this
    // project's target (26.2). Both libraries are compileOnly and never
    // shaded into the jar (see shadowJar below - only VaultAPI's economy
    // interfaces are relocated at runtime; everything compileOnly here is
    // provided by the server/hooked plugin at runtime), so excluding these
    // transitive copies and compiling against paper-api's own Guava/Gson is
    // safe: none of the WorldGuard/RedProtect/PlotSquared API surface this
    // project actually calls exposes a Guava/Gson type in its signature.
    val excludeShadedRuntimeLibs: ExternalModuleDependency.() -> Unit = {
        exclude(group = "com.google.guava", module = "guava")
        exclude(group = "com.google.code.gson", module = "gson")
    }
    compileOnly("com.sk89q.worldguard:worldguard-bukkit:7.0.18", excludeShadedRuntimeLibs)
    compileOnly("com.github.TechFortress:griefprevention:18.0.0")
    compileOnly("io.github.fabiozumbi12.RedProtect:RedProtect-Spigot:8.1.2") {
        // Confirmed dead: UltimateChat's real continuation republished under
        // a different groupId/version (io.github.fabiozumbi12.UltimateChat),
        // and RedProtect's canBuild() check never touches this dependency.
        exclude(group = "br.net.fabiozumbi12.UltimateChat", module = "UltimateFancy")
        // Same rationale as VaultAPI's org.bukkit:bukkit exclusion above:
        // this pulls a real, ancient org.spigotmc:spigot-api that conflicts
        // with paper-api's spigot-api capability. RedProtectAPI/Region
        // (the only classes hooks/HookRedProtect.java uses) live in
        // RedProtect-Core, not in spigot-api itself.
        exclude(group = "org.spigotmc", module = "spigot-api")
        excludeShadedRuntimeLibs()
    }
    compileOnly("com.wasteofplastic:askyblock:3.0.9.4")
    compileOnly("com.palmergames.bukkit.towny:towny:0.103.1.1")
    compileOnly("com.intellectualsites.plotsquared:plotsquared-bukkit:7.5.13", excludeShadedRuntimeLibs)

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
    // Precise class match, NOT a "EFurnace*" wildcard - that would also
    // match (and silently exempt from the 100% bar) EFurnaceManager, which
    // is genuinely tested below.
    "com/songoda/epicfurnaces/furnace/EFurnace.class",
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
        // Every included class must be 100% line-covered, with one
        // documented exception: Methods.java contains four
        // `catch (Exception e) { Debugger.runReport(e); }` defensive
        // blocks wrapping Bukkit/Paper calls (config lookups, DyeColor/
        // Material lookups, Locale message formatting) that cannot throw
        // under any reachable, legitimately-testable call path - forcing
        // them would require reflection or static-mocking hacks, which the
        // project's testing standard explicitly rules out. See PLAN.md
        // ("Test coverage" / exclusions) for the exact 11 line numbers.
        rule {
            element = "CLASS"
            excludes = listOf("com.songoda.epicfurnaces.utils.Methods")
            limit {
                counter = "LINE"
                minimum = "1.00".toBigDecimal()
            }
        }
        rule {
            element = "CLASS"
            includes = listOf("com.songoda.epicfurnaces.utils.Methods")
            limit {
                counter = "LINE"
                value = "MISSEDCOUNT"
                maximum = "11".toBigDecimal()
            }
        }
    }
}

tasks.check {
    dependsOn(tasks.jacocoTestCoverageVerification)
}
