# Aincrad Deployment Guide

Complete deployment guide for the Aincrad SAO Minecraft mod (MC 26.3).

## Prerequisites

- **Java 25** (JDK 25.0.4.1+)
- **Git**
- **curl**
- **4GB+ RAM** (8GB recommended)
- **Minecraft 26.3** (for client)

## Project Structure

```
deploy/
├── server/
│   ├── setup.sh              # Server installation script
│   ├── start.sh              # Start server with Aikar's flags
│   ├── stop.sh               # Graceful server stop
│   ├── Dockerfile            # Docker image definition
│   ├── docker-compose.yml    # Docker Compose config
│   ├── server.properties     # MC server configuration
│   ├── eula.txt              # Pre-accepted EULA
│   ├── mods/                 # Fabric mod jars
│   └── plugins/
│       └── Aincrad/
│           └── config.yml    # Paper plugin config
├── client/
│   ├── setup.sh              # Client Fabric installer
│   └── start.sh              # Launch Minecraft with Fabric
├── build-and-deploy.sh       # Build mod + deploy to server
├── update-server.sh          # Pull, rebuild, restart
├── backup-server.sh          # Backup world + player data
└── README.md                 # This file
```

## Server Deployment

### Quick Start (Native)

```bash
cd deploy/server
chmod +x setup.sh start.sh stop.sh
./setup.sh          # Installs Java 25, Paper, Fabric, copies mod
./start.sh          # Start the server
```

### Docker Deployment

```bash
cd deploy/server
docker-compose up -d
```

### Manual Setup

1. **Install Java 25:**
   ```bash
   sudo apt-get install openjdk-25-jdk-headless
   ```

2. **Download Paper 26.3:**
   ```bash
   curl -o paper.jar "https://api.papermc.io/v2/projects/paper/versions/26.3/builds/latest/downloads/paper-26.3-latest.jar"
   ```

3. **Install Fabric Server:**
   ```bash
   java -jar fabric-installer.jar server --mcversion 26.3 --loader 0.19.5 --downloadMinecraft
   ```

4. **Copy mod jar:**
   ```bash
   mkdir -p mods
   cp ../../build/libs/aincrad-0.1.0.jar mods/
   ```

5. **Accept EULA:**
   ```bash
   echo "eula=true" > eula.txt
   ```

6. **Start server:**
   ```bash
   ./start.sh
   ```

## Client Deployment

### Quick Start

```bash
cd deploy/client
chmod +x setup.sh start.sh
./setup.sh          # Installs Fabric loader + copies mod
./start.sh          # Launch Minecraft
```

### Manual Setup

1. **Install Fabric Loader:**
   ```bash
   java -jar fabric-installer.jar client --mcversion 26.3 --loader 0.19.5
   ```

2. **Copy mod jar:**
   ```bash
   mkdir -p ~/.minecraft/mods
   cp ../../build/libs/aincrad-0.1.0.jar ~/.minecraft/mods/
   ```

3. **Launch Minecraft** with the `fabric-loader-0.19.5-26.3` profile.

## Build and Deploy

### Build mod and deploy to server

```bash
cd deploy
./build-and-deploy.sh
```

### Update server (pull + rebuild + restart)

```bash
cd deploy
./update-server.sh
```

### Backup server

```bash
cd deploy
./backup-server.sh
```

Backups are stored in `deploy/backups/` with timestamps. Last 7 backups are kept.

## Configuration

### Server Properties (`server.properties`)

| Setting | Value | Description |
|---------|-------|-------------|
| `server-port` | 25565 | Server port |
| `max-players` | 100 | Maximum players |
| `gamemode` | survival | Default game mode |
| `difficulty` | normal | Difficulty level |
| `pvp` | true | PvP enabled |
| `online-mode` | true | Mojang authentication |
| `view-distance` | 10 | Chunk view distance |
| `simulation-distance` | 8 | Tick simulation distance |

### Plugin Config (`plugins/Aincrad/config.yml`)

Key settings:
- **Database**: SQLite (default), MySQL, or PostgreSQL
- **Guild**: Max 50 members, 1000 guilds
- **Party**: Max 5 members
- **Economy**: Starting balance 1000 Cor, death penalty 5%
- **Dungeon**: Max 4 players, 30min timeout
- **Progression**: Max level 100, 5 stat points per level

## Troubleshooting

### Server won't start

- **Java version**: Ensure Java 25 is installed: `java -version`
- **Port in use**: Check if port 25565 is available: `ss -tlnp | grep 25565`
- **EULA not accepted**: Verify `eula.txt` contains `eula=true`
- **Insufficient RAM**: Increase `-Xmx` in `start.sh`

### Mod not loading

- **Wrong MC version**: Mod requires MC 26.3
- **Fabric version**: Requires Fabric Loader 0.19.5
- **Mod jar location**: Must be in `server/mods/` directory
- **Check logs**: Review `logs/latest.log` for errors

### Client crashes

- **Fabric API**: Ensure Fabric API is installed
- **Java version**: Client needs Java 25
- **Mod conflicts**: Remove other mods to test

### Docker issues

- **Permission denied**: Run `sudo usermod -aG docker $USER` and re-login
- **Port conflict**: Change port mapping in `docker-compose.yml`
- **Volume issues**: Ensure `world/` directory exists and is writable

## Maintenance

### Regular backups

Add to crontab for daily backups at 3 AM:
```cron
0 3 * * * /path/to/deploy/backup-server.sh
```

### Log rotation

Logs are stored in `server/logs/`. Configure logrotate or use Docker's built-in rotation.

### Performance tuning

- Adjust `-Xmx` in `start.sh` based on available RAM
- Tune `view-distance` and `simulation-distance` in `server.properties`
- Monitor GC logs with `-Xlog:gc*:logs/gc.log`

## Support

- GitHub: https://github.com/skelci/Aincrad
- Issues: https://github.com/skelci/Aincrad/issues
