# CLAUDE.md — EpicFurnaces

## What this is

EpicFurnaces is a Bukkit/Spigot/Paper **server plugin** (not a client mod, not a
Fabric/Forge/NeoForge mod). It lets players smelt items in a vanilla furnace to
earn levels; each level increases smelt speed, fuel efficiency, and reward
drops, with an in-game GUI for spending XP/economy/materials on upgrades.

This is a 2018-era Songoda plugin (`com.songoda.epicfurnaces`), snapshotted
into this repo as an **original copy**, not a GitHub fork object. See
`PLAN.md` for the full modernization plan, milestone status, and the honest
platform/version support matrix — read it before assuming any feature works
on a given target.

## Provenance and licensing (binding)

- This repo is a standalone snapshot of an old Songoda `EpicFurnaces` release,
  pre-dating Songoda's later rename to Craftaro-adjacent branding. It is
  **not** a `git fork` of anything on GitHub.
- A genuinely-maintained same-lineage plugin exists at
  `github.com/Songoda-Plugins/EpicFurnaces` (same package
  `com.songoda.epicfurnaces`, same file/class layout ancestry, now at v6.0.0).
  It is licensed **CC BY-NC-ND 4.0** (NonCommercial, **NoDerivatives**).
  Because this repo is public, that license **forbids copying or adapting any
  of its code** here — it may only be used as architectural/conceptual
  reference (e.g. "how did the modern version restructure the furnace
  manager"), never as a source of pasted or lightly-modified code. No code
  has been copied from it; all porting in this repo is original work against
  this repo's own pre-existing 2018 source.
- The modern lineage also depends on `SongodaCore`, a large shared
  multi-Minecraft-version NMS abstraction library, and this plugin's original
  form depended on a sibling Songoda utility library, **Arconix**, for config
  handling, text formatting, GUI glass helpers, and location serialization.
  Both are unobtainable from any live Maven repository and carry the same
  restrictive license family. **This port removes the Arconix dependency
  entirely**, replacing each call site with small hand-written equivalents
  against vanilla Bukkit/Paper API (see "Removed dependencies" below).
- This repo's own `LICENSE` (a custom permissive-but-no-redistribution
  license from the original 2018 author) predates and is independent of the
  above; it already forbids redistributing/selling the plugin. Nothing in
  this modernization changes that.

## Architecture

Single Bukkit `JavaPlugin` (`EpicFurnacesPlugin`), event-driven:

- `EpicFurnacesPlugin` — plugin entry point, owns all manager singletons,
  registers listeners and (previously) protection-plugin hooks.
- `furnace/` — `EFurnace` (a placed furnace's runtime state: level, uses,
  access list), `EFurnaceManager` (location→furnace map), `ELevel` /
  `ELevelManager` (level definitions: cost, performance, reward, fuel
  duration — loaded from config).
- `listeners/` — `BlockListeners` (place/break), `FurnaceListeners` (smelt
  event → level-up logic lives in `EFurnace.plus(...)`), `InteractListeners`
  (open the overview GUI), `InventoryListeners` (GUI clicks/moves/close),
  `ChatListeners` (nickname-setting chat capture).
- `command/` — `CommandManager` + subcommands (`reload`, `remote`,
  `settings`).
- `utils/` — `Methods` (glass/particle/name-formatting helpers),
  `SettingsManager` (in-game settings-editor GUI + the `Setting` enum mapping
  legacy config keys to current ones), `Debugger`.
- `player/` — per-player transient state (`PlayerData`/`PlayerDataManager`).
- `EpicFurnaces-API/` — a small standalone interface module
  (`com.songoda.epicfurnaces.api`) meant for other plugins to depend on
  without pulling in the implementation. Interfaces only, no third-party
  imports — required no changes.
- `hooks/` — **excluded from the build** (see below).

### Removed dependencies

| Removed | Why | Replacement |
|---|---|---|
| Arconix (`com.songoda.arconix:*`) | Dead private Maven repo (`repo.songoda.com`), CC BY-NC-ND-family license | Hand-written: `ChatColor.translateAlternateColorCodes('&', ...)` for text formatting, `DyeColor` + `Material.*_STAINED_GLASS_PANE` for GUI glass, a small local location-string serializer, `YamlConfiguration` directly instead of Arconix's `ConfigWrapper` |
| MassiveStats | Defunct metrics library, no modern equivalent needed | Removed outright, no replacement |
| `org.json.simple` + `update()` update-checker | Called `http://update.songoda.com/...`, long dead | Removed outright |
| `javax.script` (Nashorn) in `EFurnace.updateCook()` | Removed from the JDK entirely since Java 15 — hard compile blocker on Java 21 | Direct arithmetic: `Math.round(performance / 100.0 * 200)` |
| 2-arg `FurnaceRecipe(ItemStack, Material)` | Removed from modern Bukkit API | `NamespacedKey`-based `FurnaceRecipe` constructor |
| NMS-package version-sniffing in `checkVersion()` | Obsolete/breakable string-splitting on `org.bukkit.craftbukkit.vX_Y_RZ`; modern Paper is Mojang-mapped, no such package | Removed; `plugin.yml`'s `api-version` is the real compatibility gate |

### Protection-plugin hooks: 6 revived, 3 dropped

The original 2018 source integrated with 9 land-claim plugins, all pinned to
long-dead or version-incompatible Maven coordinates (e.g. `WorldGuard
6.1.1-SNAPSHOT`, `Towny 0.92.0.0`, `PlotSquared 18.05.01`). Each was
re-evaluated against its *current* Maven coordinate rather than assumed dead:

- **Revived, live in `hooks/`**: `HookWorldGuard`, `HookGriefPrevention`,
  `HookRedProtect`, `HookASkyBlock` (moved back unchanged — confirmed
  API-compatible with their current coordinates as-is) and `HookTowny`,
  `HookPlotSquared` (rewritten against their modern APIs — `TownyAPI`
  singleton; PlotSquared's `com.plotsquared.core`/`com.plotsquared.bukkit`
  split). Each is declared in `plugin.yml`'s `softdepend:` list and
  registered in `EpicFurnacesPlugin.onEnable()` only behind a
  `pluginManager.getPlugin("...") != null` guard, so the plugin enables
  cleanly whether zero, some, or all six are present. See `build.gradle.kts`
  for the exact Maven coordinates/repos and the transitive-dependency
  exclusions each one needed to resolve cleanly against `paper-api`.
- **Dropped, relocated to `legacy-hooks/` (not compiled, not deleted)**:
  `HookFactions`, `HookKingdoms`, `HookUSkyBlock` — no live/resolvable Maven
  coordinate exists for any of them (Kingdoms has none at all; Factions/
  uSkyBlock's coordinates pull dead transitive dependencies with no viable
  substitute). This is a real, intentional feature reduction for these
  three specifically.

See `PLAN.md` ("Protection-plugin hooks: revive vs. drop") for the full
per-hook evidence table, and its "Test coverage" exclusions table for why
each hook's actual `canBuild()` business logic (as opposed to the
registration guard) isn't unit-tested.

## Platforms

This is Bukkit-API software. "Platform" here means Bukkit-API server
implementations, not mod loaders:

- **Paper** (primary target) — and by API compatibility, **Purpur**.
  **Folia is analyzed and NOT supported**: `EFurnace.upgradeFinal()` (two
  call sites) and `EFurnace.updateCook()` make Bukkit scheduler calls that
  assume safe cross-region access to a furnace's block/inventory state,
  which Folia's region-threaded model does not guarantee. `plugin.yml` does
  **not** set `folia-supported: true`. See `PLAN.md` ("Folia compatibility")
  for the full verdict; the one mitigation applied regardless (switching
  `EFurnaceManager`/`PlayerDataManager` to `ConcurrentHashMap`) reduces one
  hazard class but does not by itself make the plugin Folia-safe.
- **Spigot** — the plugin only uses stable Bukkit/Spigot API, no Paper-only
  calls, so it should run on plain Spigot too, just without any Paper-only
  optimizations (none are currently used).
- **Fabric / NeoForge / Forge are not applicable.** This is a genuine
  architectural fact, not a shortcut: Bukkit plugins run against a stable,
  version-independent server API maintained by the server implementation;
  Fabric/Forge/NeoForge mods compile directly against Minecraft's
  (Mojang-mapped or obfuscated) internal classes and are rebuilt per MC
  version. There is no way to "cross-compile" a Bukkit plugin onto a mod
  loader without rewriting it as a mod from scratch (replacing every
  `org.bukkit.*` call with direct NMS/Mixin access) — at which point it is a
  different piece of software, not a port. See `PLAN.md` milestone 3 for the
  full reasoning and what was actually evaluated before reaching this
  conclusion.

See `PLAN.md` for the version matrix and per-version build status.

## Build

```bash
./gradlew build          # compiles, shades, produces build/libs/EpicFurnaces-<version>.jar
./gradlew shadowJar       # same, explicit task name
```

- Gradle 9.x, Java 25 toolchain (auto-provisioned via the foojay resolver —
  do not install a system JDK for this). Java 25 is required because the
  latest `paper-api`'s Gradle module metadata declares it as a minimum; this
  was discovered by a failed build against a Java 21 toolchain, not assumed
  in advance. Older-version builds (see `PLAN.md` milestone 4) may use an
  older toolchain if their corresponding `paper-api` allows it.
- Single consolidated module (the old broken two-module Maven layout
  — `EpicFurnaces-API` + `EpicFurnaces-Plugin` sharing one `pom.xml` with no
  `<modules>` declared — is replaced by one Gradle project; the API package
  is still a distinct, unchanged source package within it).
- Depends on `io.papermc.paper:paper-api` at the latest resolvable version
  from `repo.papermc.io/repository/maven-public/`. Do not trust a cached
  memory of "the latest MC version" — query
  `https://fill.papermc.io/v3/projects/paper` or the maven-metadata.xml for
  `paper-api` at build time; Minecraft is calendar-versioned now (26.x).

### Tests

```bash
./gradlew test jacocoTestReport   # run tests, generate coverage report
./gradlew check                   # test + jacocoTestCoverageVerification (enforced 100% bar, see PLAN.md)
```

- JUnit 6 (via `org.junit:junit-bom:6.1.3` — JUnit 5 ended at 5.14.4) +
  MockBukkit (`org.mockbukkit.mockbukkit:mockbukkit-v26.1.2:4.115.0`) for
  anything touching `org.bukkit.*`; plain JUnit for pure logic.
- The test classpath deliberately pins `paper-api:26.1.2.build.74-stable`
  independently of whatever `-PpaperApiVersion` the main source set is
  compiled against — MockBukkit ships a bundled registry-data snapshot
  captured from that exact Paper build, and letting a newer `paper-api`
  (e.g. the default 26.2 the main jar targets) leak onto the test classpath
  produces a real `InternalDataLoadException` at test runtime, not a
  hypothetical concern. See `build.gradle.kts`'s dependency block comments.
- Coverage report/enforced bar, exclusions, and the one documented
  exception (`utils.Methods`, capped at 11 missed lines) are in `PLAN.md`
  ("Test coverage").

## Porting notes for whoever touches this next

- The `Setting` enum in `SettingsManager.java` has a
  `UPGRADE_PARTICLE_TYPE` default of `"WITCH"` (was `SPELL_WITCH` in the
  original 2018 source — Bukkit renamed that `Particle` enum constant around
  MC 1.20.5). `config.yml`/`lang.yml` ship empty in this repo (generated at
  first run by `SettingsManager`/`Locale`), so this Java default is the only
  place the value needed correcting.
- `BlockListeners.onBlockBreak` originally read a config key literally typed
  `"ain.Remember Furnace Item Levels"` (missing the `M` of `Main.`) — a
  pre-existing typo that silently disabled the "remember furnace level on
  break" feature. Fixed to `"Main.Remember Furnace Item Levels"`.
- `ChatListeners`/`InventoryListeners` use `AsyncPlayerChatEvent`, which is
  soft-deprecated on Paper in favor of the Adventure-based
  `io.papermc.paper.event.player.AsyncChatEvent`. Left as-is: the legacy
  event still compiles and fires on current Paper; migrate to the Paper
  event only if/when Spigot compatibility is deliberately dropped.
