#!/bin/bash
# Download Paper 1.21.1 server jar
# Run this before docker-compose up

set -euo pipefail

PAPER_JAR="deploy/server/paper.jar"

echo "Downloading Paper 1.21.1 server jar..."

# Try PaperMC API v2 (legacy, might work)
echo "Trying PaperMC API..."
if curl -L -H "User-Agent: Mozilla/5.0" -o "$PAPER_JAR.tmp" \
    "https://api.papermc.io/v2/projects/paper/versions/1.21.1/builds/latest/downloads/paper-1.21.1-latest.jar" 2>/dev/null; then
    if file "$PAPER_JAR.tmp" | grep -q "Java archive"; then
        mv "$PAPER_JAR.tmp" "$PAPER_JAR"
        echo "Downloaded successfully from PaperMC API"
        exit 0
    fi
    rm -f "$PAPER_JAR.tmp"
fi

# Try PaperMC Jenkins
echo "Trying PaperMC Jenkins..."
if curl -L -H "User-Agent: Mozilla/5.0" -o "$PAPER_JAR.tmp" \
    "https://ci.papermc.io/job/Paper-1.21/lastSuccessfulBuild/artifact/paper-1.21.1-SNAPSHOT.jar" 2>/dev/null; then
    if file "$PAPER_JAR.tmp" | grep -q "Java archive"; then
        mv "$PAPER_JAR.tmp" "$PAPER_JAR"
        echo "Downloaded successfully from PaperMC Jenkins"
        exit 0
    fi
    rm -f "$PAPER_JAR.tmp"
fi

# Try direct download from website
echo "Trying website download..."
if curl -L -H "User-Agent: Mozilla/5.0" -H "Referer: https://papermc.io/" -o "$PAPER_JAR.tmp" \
    "https://papermc.io/api/v2/projects/paper/versions/1.21.1/builds/latest/downloads/paper-1.21.1-latest.jar" 2>/dev/null; then
    if file "$PAPER_JAR.tmp" | grep -q "Java archive"; then
        mv "$PAPER_JAR.tmp" "$PAPER_JAR"
        echo "Downloaded successfully from website"
        exit 0
    fi
    rm -f "$PAPER_JAR.tmp"
fi

echo ""
echo "All automated downloads failed (PaperMC API is sunset)."
echo "Please manually download Paper 1.21.1 from:"
echo "  https://papermc.io/downloads/paper"
echo ""
echo "Then place the jar at: $PAPER_JAR"
echo ""
echo "Or if you have a local Paper jar, copy it:"
echo "  cp /path/to/paper-1.21.1.jar $PAPER_JAR"
exit 1