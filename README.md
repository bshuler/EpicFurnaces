# EpicFurnaces

EpicFurnaces is a Bukkit/Spigot/Paper server plugin that lets players level
up vanilla furnaces: smelting earns levels, each level increases smelt
speed, fuel efficiency, and reward drops, and an in-game GUI lets players
spend XP/economy/materials on upgrades. Main command: `/epicfurnaces`
(alias `/ef`).

![N|Solid](https://i.imgur.com/jKtE7ZM.png)

## Provenance and licensing

This repo is a standalone snapshot of a 2018-era Songoda `EpicFurnaces`
release (`com.songoda.epicfurnaces`) — not a GitHub fork. Two things worth
knowing:

- A separately maintained same-lineage plugin exists at
  `Songoda-Plugins/EpicFurnaces` under **CC BY-NC-ND 4.0** (NoDerivatives).
  **No code from it has been copied or adapted here** — all porting in this
  repo is original work against this repo's own pre-existing 2018 source.
- This repo keeps its original custom license (permissive use, no
  redistribution/resale) — see `LICENSE`.

## Modernization work

- **The dead Arconix dependency is gone.** The original build depended on
  Songoda's Arconix utility library (config handling, text formatting, GUI
  glass helpers, location serialization), which is unobtainable from any
  live Maven repository. Every call site was replaced with small
  hand-written equivalents against vanilla Bukkit/Paper API.
- Rebuilt on modern Gradle (9.x wrapper, Java toolchains), `api-version`
  raised to current, and a JUnit unit-test suite added over the furnace/
  level managers, player data, protection-hook registration, the public
  API, and utilities.
- Full history, per-milestone status, and the honest feature matrix are in
  `PLAN.md`; architecture notes in `CLAUDE.md`.

## Supported Paper versions

One codebase, no version branches. Default build targets the newest stable
Paper API (currently **26.2**); the same source compiles cleanly against
older API lines:

```sh
export JAVA_HOME=/Library/Java/JavaVirtualMachines/temurin-21.jdk/Contents/Home
./gradlew build                                             # 26.2 (default)
./gradlew build -PpaperApiVersion=1.21.11-R0.1-SNAPSHOT
./gradlew build -PpaperApiVersion=1.20.1-R0.1-SNAPSHOT
./gradlew build -PpaperApiVersion=1.19.4-R0.1-SNAPSHOT
./gradlew build -PpaperApiVersion=1.18.2-R0.1-SNAPSHOT
```

All five targets are verified builds. The jar lands in `build/libs/`.

## Testing

1. **Unit tests** — `./gradlew check` (JUnit 5, see suite under
   `src/test/java`).
2. **Headless Paper boot smoke test** (opt-in — needs a real Paper server
   jar):

   ```sh
   ./gradlew paperBootTest -PpaperServerJar=/path/to/paper-26.2-111.jar
   ```

   Boots a real headless Paper server with the packaged jar in `plugins/`
   and asserts: the jar loads, `onEnable` doesn't throw, `/epicfurnaces`
   is registered in the live command map and doesn't throw when invoked,
   the plugin shows in `plugins`, and `onDisable` exits cleanly. Without a
   server jar the task reports `SKIPPED (this is a skip, not a pass)`.
   Transcript: `build/paper-boot/paper-boot-test.log`.
