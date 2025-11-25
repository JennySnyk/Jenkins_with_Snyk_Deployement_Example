#!/bin/bash

# Jenkins WAR Startup Script

echo "🚀 Starting Jenkins..."
echo ""
echo "Jenkins will start on: http://localhost:8080"
echo ""
echo "⏳ This will take about 30-60 seconds to start..."
echo "   Watch for the unlock password below!"
echo ""
echo "━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━"

# Set Jenkins home directory
export JENKINS_HOME="/Users/jenny/Documents/Labs/Internal_Projects/jenkins-local/jenkins-war/jenkins_home"

# Create home directory if it doesn't exist
mkdir -p "$JENKINS_HOME"

# Use Java 17 (full path to ensure correct version)
export JAVA_HOME="/opt/homebrew/opt/openjdk@17"
export PATH="$JAVA_HOME/bin:$PATH"

echo "Using Java version:"
"$JAVA_HOME/bin/java" --version
echo ""

# Start Jenkins with full Java path (bind to localhost only to avoid macOS security issues)
"$JAVA_HOME/bin/java" -jar jenkins.war --httpListenAddress=127.0.0.1 --httpPort=8080 2>&1 | tee -a jenkins.log

echo ""
echo "Jenkins stopped."
