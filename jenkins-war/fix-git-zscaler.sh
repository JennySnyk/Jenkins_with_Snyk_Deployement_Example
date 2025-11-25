#!/bin/bash
# Fix Git SSL issues in Jenkins with Zscaler certificate

echo "🔧 Configuring Git for Jenkins to work with Zscaler..."
echo ""

# Get the Jenkins home directory
JENKINS_HOME="/Users/jenny/Documents/Labs/Internal_Projects/jenkins-local/jenkins-war/jenkins_home"

# Export Zscaler certificate to the correct location
echo "📥 Step 1: Exporting Zscaler certificate..."
security find-certificate -a -p -c "Zscaler" /Library/Keychains/System.keychain > /tmp/zscaler-root-ca.crt 2>/dev/null

if [ ! -s /tmp/zscaler-root-ca.crt ]; then
    echo "❌ Could not find Zscaler certificate in system keychain"
    exit 1
fi

# Copy to a location Jenkins can access
sudo cp /tmp/zscaler-root-ca.crt /usr/local/share/zscaler-root-ca.crt
sudo chmod 644 /usr/local/share/zscaler-root-ca.crt

echo "✅ Zscaler certificate copied to /usr/local/share/zscaler-root-ca.crt"
echo ""

# Configure Git globally for Jenkins user
echo "🔧 Step 2: Configuring Git to use Zscaler certificate..."

# Set Git to use the Zscaler certificate
git config --global http.sslCAInfo /usr/local/share/zscaler-root-ca.crt
git config --global http.sslVerify true

echo "✅ Git configured to use Zscaler certificate"
echo ""

# Alternative: For Jenkins workspace specifically
echo "🔧 Step 3: Creating Git config for Jenkins..."

# Ensure .gitconfig exists for the current user
if [ ! -f ~/.gitconfig ]; then
    touch ~/.gitconfig
fi

echo "✅ Git configuration complete!"
echo ""
echo "🔄 Now restart Jenkins and try the pipeline again:"
echo "   1. pkill -f jenkins.war"
echo "   2. cd /Users/jenny/Documents/Labs/Internal_Projects/jenkins-local/jenkins-war && ./start-jenkins.sh"
echo ""

