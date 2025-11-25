#!/bin/bash
# Fix Jenkins SSL issues with Zscaler certificate

echo "🔧 Configuring Jenkins to trust Zscaler certificate..."
echo ""

# 1. Export the Zscaler certificate from system keychain
echo "📥 Step 1: Exporting Zscaler certificate..."
security find-certificate -a -p -c "Zscaler" /Library/Keychains/System.keychain > ~/Documents/zscaler-root-ca.pem 2>/dev/null

if [ ! -s ~/Documents/zscaler-root-ca.pem ]; then
    echo "❌ Could not find Zscaler certificate in system keychain"
    echo "Please ensure Zscaler is properly installed"
    exit 1
fi

echo "✅ Zscaler certificate exported to ~/Documents/zscaler-root-ca.pem"
echo ""

# 2. Find Java home
echo "📍 Step 2: Finding Java installation..."
JAVA_HOME=$(/usr/libexec/java_home -v 17)
echo "Java Home: $JAVA_HOME"
echo ""

# 3. Import certificate into Java keystore
echo "🔐 Step 3: Importing certificate into Java keystore..."
echo "This requires sudo password..."
echo ""

KEYSTORE="$JAVA_HOME/lib/security/cacerts"
ALIAS="zscaler-root-ca"

# Remove old certificate if it exists
sudo keytool -delete -alias "$ALIAS" -keystore "$KEYSTORE" -storepass changeit 2>/dev/null || true

# Import the certificate
sudo keytool -import -trustcacerts -alias "$ALIAS" \
    -file ~/Documents/zscaler-root-ca.pem \
    -keystore "$KEYSTORE" \
    -storepass changeit \
    -noprompt

if [ $? -eq 0 ]; then
    echo ""
    echo "✅ Certificate imported successfully!"
    echo ""
    echo "🔄 Now restart Jenkins:"
    echo "   1. Stop Jenkins: pkill -f jenkins.war"
    echo "   2. Start Jenkins: cd /Users/jenny/Documents/Labs/Internal_Projects/jenkins-local/jenkins-war && ./start-jenkins.sh"
    echo ""
else
    echo "❌ Failed to import certificate"
    exit 1
fi

