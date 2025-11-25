# Jenkins + Snyk Code Quick Start

Get up and running with Snyk Code security scanning in 5 minutes!

## 🎯 What You'll Do

1. Install prerequisites
2. Start Jenkins
3. Configure Snyk
4. Run your first security scan

## 📋 Prerequisites Check

```bash
# Check Java 17
java --version
# If not installed: brew install openjdk@17

# Check Node.js
node --version
# If not installed: brew install node

# Check Snyk CLI
snyk --version
# If not installed: npm install -g snyk
```

## 🚀 Method 1: Quick Start (With Scripts)

### Step 1: Start Jenkins

```bash
cd jenkins-war
./start-jenkins.sh
```

Wait 30-60 seconds, then open: **http://localhost:8080**

### Step 2: Initial Setup

First time only:
1. Create admin account
2. Accept default plugin installations
3. Click "Start using Jenkins"

### Step 3: Add Snyk Token

1. Get your token: https://app.snyk.io/account
2. In Jenkins: **Manage Jenkins** → **Manage Credentials**
3. Click **(global)** → **Add Credentials**
4. Configure:
   - Kind: **Secret text**
   - Secret: `<paste your Snyk API token>`
   - ID: `snyk-api-token`
5. Click **OK**

### Step 4: Create Your First Pipeline

1. Click **New Item**
2. Name: `Snyk-Code-Scan`
3. Type: **Pipeline**
4. Click **OK**
5. Scroll to **Pipeline** section
6. Copy content from `sample-pipelines/Jenkinsfile.snyk-code`
7. Click **Save**
8. Click **Build Now** 🎉

### Step 5: View Results

- Console Output: Click the build number, then "Console Output"
- Artifacts: Click build number, then "Artifacts"
- See vulnerabilities found in the demo app!

## 🔧 Method 2: Manual Workflow (No Jenkins)

Perfect for quick scans without Jenkins setup!

### Step 1: Setup Snyk

```bash
# Install Snyk CLI
npm install -g snyk

# Authenticate
snyk auth
# This opens a browser - click "Authenticate"
```

### Step 2: Get Demo Application

```bash
# Clone vulnerable demo app
git clone https://github.com/snyk-labs/nodejs-goof.git
cd nodejs-goof

# Install dependencies
npm install
```

### Step 3: Run Snyk Code Scan

```bash
# If using EU, AU, or US-02 tenants, configure your region first:
# snyk config environment SNYK-EU-01  # For EU
# snyk config environment SNYK-AU-01  # For Australia
# snyk config environment SNYK-US-02  # For US-02
# More info: https://docs.snyk.io/snyk-data-and-governance/regional-hosting-and-data-residency

# Basic scan (shows results in terminal)
snyk code test
```

**Example output:**
```
Testing /path/to/nodejs-goof ...

✗ [High] SQL Injection
  Path: app/routes/users.js, line 45
  Info: User input flows into SQL query without sanitization
  
✗ [High] Command Injection  
  Path: app/routes/products.js, line 78
  Info: User input used in exec() without validation

✗ [Medium] Cross-site Scripting (XSS)
  Path: app/views/index.ejs, line 23
  Info: Unsanitized user input rendered in HTML

Organization: your-org
Test completed

Tested nodejs-goof for known issues, found 12 issues, 5 high, 4 medium, 3 low
```

### Step 4: Generate Reports

```bash
# JSON report
snyk code test --json-file-output=results.json

# SARIF report (for security dashboards)
snyk code test --sarif-file-output=results.sarif

# View JSON results
cat results.json | jq '.'  # if you have jq installed
```

### Step 5: Generate HTML Report

```bash
# Install report generator
npm install -g snyk-to-html

# Convert JSON to HTML
snyk-to-html -i results.json -o report.html

# Open in browser
open report.html  # macOS
# Or just double-click report.html
```

### Step 6: Scan Your Own Projects

```bash
# Navigate to your project
cd /path/to/your/project

# Install dependencies if needed
npm install

# Run scan
snyk code test
```

## 📊 Understanding Results

### Severity Levels

- 🔴 **Critical/High**: Fix immediately - exploitable vulnerabilities
- 🟠 **Medium**: Should fix - potential security issues  
- 🟡 **Low**: Consider fixing - best practice violations

### What Snyk Code Finds

✅ **SQL Injection** - Unsafe database queries  
✅ **Cross-Site Scripting (XSS)** - Unescaped user input  
✅ **Command Injection** - Unsafe system command execution  
✅ **Path Traversal** - File system access vulnerabilities  
✅ **Hardcoded Secrets** - API keys, passwords in code  
✅ **Insecure Crypto** - Weak encryption methods  
✅ **And much more!**

## 🎓 Next Steps with Jenkins

### Option 1: Use Sample Pipelines

Copy these ready-made examples:

```bash
# Basic scan
sample-pipelines/Jenkinsfile.snyk-code

# With reports and dashboard
pipelines/snyk-code-with-reports.groovy

# With quality gates (fail on high severity)
pipelines/3-snyk-code-cli-fail-on-high.groovy
```

### Option 2: Create Custom Pipeline

```groovy
pipeline {
    agent any
    
    environment {
        SNYK_TOKEN = credentials('snyk-api-token')
    }
    
    stages {
        stage('Checkout') {
            steps {
                // Your git repository
                git 'https://github.com/your-org/your-repo.git'
            }
        }
        
        stage('Install') {
            steps {
                sh 'npm install'
            }
        }
        
        stage('Snyk Code Scan') {
            steps {
                sh '''
                    snyk auth ${SNYK_TOKEN}
                    snyk code test || true
                '''
            }
        }
    }
}
```

## 🔄 Daily Workflow

### Developer Workflow (Local)

```bash
# 1. Write code
# 2. Before committing, scan:
snyk code test

# 3. Fix issues found
# 4. Scan again
snyk code test

# 5. Commit only when clean
git commit -m "Fix: Resolved SQL injection"
```

### CI/CD Workflow (Jenkins)

```bash
# 1. Push code to GitHub
git push

# 2. Jenkins automatically:
#    - Checks out code
#    - Installs dependencies
#    - Runs Snyk Code scan
#    - Reports vulnerabilities

# 3. Review results in Jenkins dashboard
# 4. Fix issues if build fails
```

## 💡 Pro Tips

### Tip 1: Scan Before Every Commit

```bash
# Add to your pre-commit hook
echo "snyk code test --severity-threshold=high" > .git/hooks/pre-commit
chmod +x .git/hooks/pre-commit
```

### Tip 2: Filter by Severity

```bash
# Only show high/critical
snyk code test --severity-threshold=high

# Show all issues
snyk code test --severity-threshold=low
```

### Tip 3: Ignore Specific Issues

Create `.snyk` file:
```yaml
# Snyk (https://snyk.io) policy file
version: v1.25.0
ignore:
  'SNYK-JS-123456':
    - '*':
        reason: False positive - reviewed by security team
        expires: 2024-12-31T00:00:00.000Z
```

### Tip 4: Quick Project Scan

```bash
# One-liner to scan any npm project
cd /path/to/project && npm install && snyk code test
```

## 🆘 Troubleshooting

### "snyk: command not found"

```bash
npm install -g snyk
export PATH="$PATH:$(npm root -g)/../bin"
```

### "Authentication required"

```bash
snyk auth
# Or specify token directly:
snyk auth YOUR_TOKEN_HERE
```

### "No supported files found"

Snyk Code supports many languages. Ensure your project has source files:
- JavaScript/TypeScript: `.js`, `.ts`, `.jsx`, `.tsx`
- Python: `.py`
- Java: `.java`
- And more!

### Jenkins Pipeline Fails

1. Check credentials are configured (ID must be `snyk-api-token`)
2. Check Snyk CLI is installed: `snyk --version` on Jenkins agent
3. Check console output for specific error
4. Verify token is valid at https://app.snyk.io

## 📖 What's Next?

- **See [README.md](README.md)** for complete documentation
- **See [SNYK_SETUP.md](SNYK_SETUP.md)** for advanced Snyk configuration
- **See [DASHBOARD_GUIDE.md](DASHBOARD_GUIDE.md)** for dashboard setup

## 🎯 Common Use Cases

### Scan Before Deployment

```groovy
// In your Jenkins pipeline
stage('Security Gate') {
    steps {
        sh '''
            snyk auth ${SNYK_TOKEN}
            snyk code test --severity-threshold=high
        '''
    }
}
```

### Scheduled Scans

In Jenkins:
1. Configure pipeline
2. Enable "Build periodically"
3. Set schedule: `H 2 * * *` (daily at 2 AM)

### Scan Pull Requests

Integrate with GitHub:
1. Install GitHub plugin in Jenkins
2. Configure webhook
3. Scan on every PR
4. Block merge if vulnerabilities found

---

**Ready?** Start with Method 1 for the full Jenkins experience, or Method 2 for quick command-line scans!
