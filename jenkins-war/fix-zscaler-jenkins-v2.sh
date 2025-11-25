#!/bin/bash
# Fix Jenkins SSL issues with Zscaler certificate (v2 - Fixed permissions)

echo "🔧 Configuring Jenkins to trust Zscaler certificate..."
echo ""

# 1. Export the Zscaler certificate from system keychain to /tmp (accessible by sudo)
echo "📥 Step 1: Exporting Zscaler certificate..."
security find-certificate -a -p -c "Zscaler" /Library/Keychains/System.keychain > /tmp/zscaler-root-ca.pem 2>/dev/null

if [ ! -s /tmp/zscaler-root-ca.pem ]; then
    echo "❌ Could not find Zscaler certificate in system keychain"
    echo "Please ensure the Jamf Self Service 'Zscaler Certificate Fix' has been run"
    exit 1
fi

echo "✅ Zscaler certificate exported to /tmp/zscaler-root-ca.pem"
echo ""

# 2. Find Java home
echo "📍 Step 2: Finding Java installation..."
JAVA_HOME=$(/usr/libexec/java_home -v 17)
echo "Java Home: $JAVA_HOME"
echo ""

# 3. Import certificate into Java keystore
echo "🔐 Step 3: Importing certificate into Java keystore..."
echo "This requires your sudo password..."
echo ""

KEYSTORE="$JAVA_HOME/lib/security/cacerts"
ALIAS="zscaler-root-ca"

# Remove old certificate if it exists
sudo keytool -delete -alias "$ALIAS" -keystore "$KEYSTORE" -storepass changeit 2>/dev/null || true

# Import the certificate
sudo keytool -import -trustcacerts -alias "$ALIAS" \
    -file /tmp/zscaler-root-ca.pem \
    -keystore "$KEYSTORE" \
    -storepass changeit \
    -noprompt

if [ $? -eq 0 ]; then
    echo ""
    echo "✅ Certificate imported successfully into Java keystore!"
    echo ""
    echo "🔄 Now restart Jenkins:"
    echo ""
    echo "   Stop Jenkins:  pkill -f jenkins.war"
    echo "   Start Jenkins: cd /Users/jenny/Documents/Labs/Internal_Projects/jenkins-local/jenkins-war && ./start-jenkins.sh"
    echo ""
    echo "Then you should be able to install plugins in Jenkins!"
    echo ""
    
    # Cleanup
    rm -f /tmp/zscaler-root-ca.pem
else
    echo "❌ Failed to import certificate"
    rm -f /tmp/zscaler-root-ca.pem
    exit 1
fi


