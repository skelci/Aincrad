FROM eclipse-temurin:25-jdk-jammy

WORKDIR /server

# Install dependencies
RUN apt-get update && apt-get install -y --no-install-recommends \
    curl \
    jq \
    && rm -rf /var/lib/apt/lists/*

# Copy Paper jar if it exists (user should download it first)
COPY paper.jar /server/paper.jar

# Install Fabric server for 1.21.1
RUN curl -fsSL -o fabric-installer.jar \
    "https://maven.fabricmc.net/net/fabricmc/fabric-installer/1.0.1/fabric-installer-1.0.1.jar" \
    && java -jar fabric-installer.jar server \
        --mcversion 1.21.1 \
        --loader 0.19.5 \
        --downloadMinecraft \
        --noprofile \
    && rm fabric-installer.jar

# Copy mod jar (mounted at runtime or copied via build context)
COPY mods/ /server/mods/
COPY plugins/ /server/plugins/
COPY server.properties /server/server.properties
COPY paper.yml /server/paper.yml
COPY eula.txt /server/eula.txt
RUN mkdir -p /server/world/datapacks
COPY datapacks/ /server/world/datapacks/

EXPOSE 25565

ENTRYPOINT ["java"]
CMD ["-Xms4G", "-Xmx4G", "-XX:+UseG1GC", "-XX:+ParallelRefProcEnabled", \
     "-XX:MaxGCPauseMillis=200", "-XX:+UnlockExperimentalVMOptions", \
     "-XX:+DisableExplicitGC", "-XX:+AlwaysPreTouch", \
     "-XX:G1NewSizePercent=30", "-XX:G1MaxNewSizePercent=40", \
     "-XX:G1HeapRegionSize=8M", "-XX:G1ReservePercent=20", \
     "-XX:G1HeapWastePercent=5", "-XX:G1MixedGCCountTarget=4", \
     "-XX:InitiatingHeapOccupancyPercent=15", "-XX:G1MixedGCLiveThresholdPercent=90", \
     "-XX:G1RSetUpdatingPauseTimePercent=5", "-XX:SurvivorRatio=32", \
     "-XX:+PerfDisableSharedMem", "-XX:MaxTenuringThreshold=1", \
     "-jar", "paper.jar", "nogui"]