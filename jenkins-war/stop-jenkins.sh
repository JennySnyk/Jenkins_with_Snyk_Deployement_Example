#!/bin/bash

echo "🛑 Stopping Jenkins..."

# Kill any Jenkins processes
pkill -9 -f "jenkins.war"

# Wait a moment for the port to be released
sleep 2

echo "✅ Jenkins stopped"


