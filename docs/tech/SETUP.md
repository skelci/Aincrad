# Aincrad Development Environment Setup

## 1. Prerequisites

### 1.1 JDK

- **JDK 21** (or later) — required by modern Minecraft and Gradle.
- Recommended: Eclipse Temurin 21 LTS.
- Set `JAVA_HOME` and ensure `java -version` reports 21+.

### 1.2 IDE

- **IntelliJ IDEA** (Community or Ultimate) — best Gradle and Minecraft modding support.
- Recommended plugins: Minecraft Development (optional), Gradle.

### 1.3 Git

- Git 2.x with your identity configured.

## 2. Clone and Import

```bash
git clone <repo-url> Aincrad
cd Aincrad
```

In IntelliJ: File → Open → select the project root (the folder with `settings.gradle`). Let Gradle sync finish.

## 3. Project Structure

- `aincrad-mod/` — Fabric mod subproject.
- `aincrad-plugin/` — Paper plugin subproject.
- Root `settings.gradle` includes both.

## 4. Fabric Mod Setup

### 4.1 Fabric Loom

- Fabric Loom is configured in `aincrad-mod/build.gradle`.
- It automatically downloads the correct Minecraft version, mappings (Yarn or Mojang), and Fabric API.

### 4.2 Dependencies

- Fabric API, GeckoLib, Cloth Config, Mod Menu — declared in `gradle/libs.versions.toml` and pulled by Loom.

### 4.3 Run configurations

Loom provides:

- `runClient` — launches a dev client with the mod.
- `runServer` — launches a dev Fabric server with the mod.

In IntelliJ, these appear under Gradle → Tasks → fabric → `runClient` / `runServer`.

## 5. Paper Server Setup

### 5.1 Download Paper

- Get the Paper (or Purpur) build for the target MC version from https://papermc.io.
- Place the jar in `server/` (git-ignored).

### 5.2 Install the plugin

- Build the plugin: `./gradlew :aincrad-plugin:build`.
- Copy `aincrad-plugin/build/libs/aincrad-plugin-*.jar` into `server/plugins/`.
- Create `server/plugins/Aincrad/config.yml` with database and server settings.

### 5.3 First run

```bash
cd server
java -Xms2G -Xmx4G -jar paper-*.jar
```

- Accept the EULA (`eula.txt` → `eula=true`).
- The plugin generates its config and database schema on first start.

## 6. Running Client + Server Together

### 6.1 Local dev loop

1. Start the Paper server (section 5).
2. Run `runClient` from IntelliJ.
3. In the client, add a server entry pointing at `localhost:25565`.
4. Join — the mod detects the plugin via plugin messaging and enables guild/economy/matchmaking features.

### 6.2 Fabric server alternative

For testing custom content without the plugin:

- Run `runServer` (Fabric dev server).
- The mod works standalone; plugin-dependent features are disabled gracefully.

## 7. Debugging Workflow

### 7.1 Client debugging

- Run `runClient` in debug mode (IntelliJ debug button).
- Breakpoints in mod code work as normal.
- Use F3 + client logs for in-game diagnostics.

### 7.2 Server debugging

- For the Paper server: start with `-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=5005` and attach IntelliJ's remote JVM debug.
- For the Fabric dev server: `runServer` in debug mode.

### 7.3 Logging

- Mod: standard SLF4J logging via Fabric.
- Plugin: Bukkit logger.
- Both write to their respective console/log files.

### 7.4 Hot reload

- Client: use the Fabric dev reload (or restart `runClient` for resource changes).
- Server: plugin changes require a server restart (or a plugin manager like PlugMan for quick reloads in dev).

## 8. Build and Distribution

```bash
./gradlew build
```

Outputs:

- `aincrad-mod/build/libs/aincrad-mod-<version>.jar` — put in `mods/` on client and Fabric server.
- `aincrad-plugin/build/libs/aincrad-plugin-<version>.jar` — put in `plugins/` on Paper server.

## 9. Troubleshooting

- **Gradle sync fails**: check JDK version (must be 21+), check network (Loom downloads MC/mappings).
- **Mod not loading on client**: verify Fabric Loader and Fabric API versions match.
- **Plugin not loading on server**: check Paper version compatibility, check `plugins/Aincrad/config.yml`.
- **Plugin messages not received**: verify both sides use the same `aincrad:` channel namespace and protocol version.
