# Snyk Code Integration Guide

Complete guide for using **Snyk Code (SAST)** to scan applications for security vulnerabilities.

## 🎯 What is Snyk Code?

**Snyk Code** is a Static Application Security Testing (SAST) tool that analyzes your source code to find security vulnerabilities before they reach production.

### What It Finds

✅ **Injection Flaws**
- SQL Injection
- Command Injection
- LDAP Injection
- XPath Injection

✅ **Cross-Site Scripting (XSS)**
- Reflected XSS
- Stored XSS
- DOM-based XSS

✅ **Broken Access Control**
- Path Traversal
- Insecure Direct Object References
- Missing Authorization

✅ **Security Misconfigurations**
- Hardcoded Secrets
- Weak Cryptography
- Insecure Deserialization
- XML External Entities (XXE)

✅ **And Many More!**

## 📋 Prerequisites

### Required Tools

- **Snyk Account**: Sign up free at [snyk.io](https://snyk.io)
- **Snyk CLI**: `npm install -g snyk`
- **Node.js**: v18+ or v20 LTS
- **Java 17**: For Jenkins (if using Jenkins)

### Get Your Snyk API Token

1. Login to https://app.snyk.io
2. Go to Account Settings: https://app.snyk.io/account
3. Scroll to "API Token"
4. Click "Click to show" and copy your token

## 🚀 Setup Methods

Choose your preferred method:

### Method 1: Command Line (No Jenkins Required)

Perfect for local development and quick scans.

#### Step 1: Install Snyk CLI

```bash
npm install -g snyk
```

#### Step 2: Configure Region (If Applicable)

If you're using Snyk EU, AU, or US-02 tenants, configure your region before authenticating:

```bash
# For EU tenant
snyk config environment SNYK-EU-01

# For Australia tenant
snyk config environment SNYK-AU-01

# For US-02 tenant
snyk config environment SNYK-US-02

# For US-01 (default), no configuration needed
```

**Available Regions:**
- **SNYK-US-01** (US) - Default: `https://app.snyk.io`
- **SNYK-US-02** (US): `https://app.us.snyk.io`
- **SNYK-EU-01** (Germany): `https://app.eu.snyk.io`
- **SNYK-AU-01** (Australia): `https://app.au.snyk.io`

More info: [Regional hosting documentation](https://docs.snyk.io/snyk-data-and-governance/regional-hosting-and-data-residency)

#### Step 3: Authenticate

```bash
# Option A: Interactive (opens browser)
snyk auth

# Option B: Direct token
snyk auth YOUR_API_TOKEN

# Verify authentication
snyk --version
snyk whoami
```

#### Step 4: Test a Project

```bash
# Clone demo application (intentionally vulnerable)
git clone https://github.com/snyk-labs/nodejs-goof.git
cd nodejs-goof
npm install

# Run Snyk Code scan
snyk code test
```

#### Step 5: View Results

The scan will show vulnerabilities directly in your terminal:

```
Testing nodejs-goof ...

✗ [High] SQL Injection
  Path: routes/index.js, line 87
  Info: User input flows directly into SQL query
  
  87 |   db.query('SELECT * FROM users WHERE id = ' + req.params.id)
                                                       ^^^^^^^^^^^^^^
  
  Fix: Use parameterized queries
  
✗ [High] Command Injection
  Path: routes/exec.js, line 23  
  Info: User input used in exec() without validation
  
  23 |   exec('ping ' + req.query.ip, callback)
                        ^^^^^^^^^^^^^^
  
  Fix: Validate and sanitize user input

... more issues ...

Organization: your-org
Test completed

Tested nodejs-goof for known issues
Found 15 issues: 6 high, 5 medium, 4 low
```

#### Step 6: Generate Reports

```bash
# JSON format (for parsing/automation)
snyk code test --json-file-output=results.json

# SARIF format (for security dashboards)
snyk code test --sarif-file-output=results.sarif

# Both formats
snyk code test \
  --json-file-output=results.json \
  --sarif-file-output=results.sarif
```

#### Step 7: Create HTML Report

```bash
# Install snyk-to-html
npm install -g snyk-to-html

# Generate HTML report
snyk-to-html -i results.json -o report.html

# Open report
open report.html  # macOS
# or double-click the file
```

---

### Method 2: Jenkins Integration

Perfect for CI/CD pipelines and team collaboration.

#### Step 1: Start Jenkins

```bash
cd jenkins-war
./start-jenkins.sh
```

Access Jenkins at **http://localhost:8080**

#### Step 2: Configure Snyk Token in Jenkins

1. Go to **Manage Jenkins** → **Manage Credentials**
2. Click **(global)** domain
3. Click **Add Credentials**
4. Configure:
   - **Kind**: Secret text
   - **Secret**: `<paste your Snyk API token>`
   - **ID**: `snyk-api-token` (exactly this)
   - **Description**: Snyk API Token
5. Click **OK**

#### Step 3: Verify Snyk CLI is Installed

Create a test pipeline:

1. Click **New Item**
2. Name: `test-snyk`
3. Type: **Pipeline**
4. Pipeline script:

```groovy
pipeline {
    agent any
    stages {
        stage('Test Snyk') {
            steps {
                sh 'snyk --version'
                sh 'node --version'
                sh 'npm --version'
            }
        }
    }
}
```

5. Save and **Build Now**
6. If Snyk not found, install it: `npm install -g snyk` on your Jenkins machine

#### Step 4: Create Your First Snyk Code Pipeline

1. **New Item** → Name: `Snyk-Code-Scan`
2. Type: **Pipeline**
3. Copy content from `sample-pipelines/Jenkinsfile.snyk-code`
4. **Save** and **Build Now**

## 📊 Sample Pipelines

### Pipeline 1: Basic Snyk Code Scan

**File**: `pipelines/snyk-code-basic.groovy`

Simple scan that shows results in console.

```groovy
pipeline {
    agent any
    environment {
        SNYK_TOKEN = credentials('snyk-api-token')
    }
    stages {
        stage('Checkout') {
            steps {
                git 'https://github.com/snyk-labs/nodejs-goof.git'
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

### Pipeline 2: With Reports & Dashboard

**File**: `pipelines/snyk-code-with-reports.groovy`

Generates JSON, SARIF, and HTML reports. Integrates with Warnings NG dashboard.

**Features**:
- ✅ Multiple report formats
- ✅ Interactive dashboard
- ✅ Trend charts
- ✅ Archived artifacts

### Pipeline 3: With Quality Gates

**File**: `pipelines/3-snyk-code-cli-fail-on-high.groovy`

Fails the build if high/critical vulnerabilities are found.

**Use case**: Block deployments with serious security issues.

## 🎓 Understanding Results

### Severity Levels

- **Critical**: Immediate action required - actively exploitable
- **High**: Fix soon - serious security risk
- **Medium**: Should fix - moderate risk
- **Low**: Consider fixing - minor risk or best practice

### Issue Information

Each vulnerability includes:

```
✗ [High] SQL Injection
  Path: routes/users.js:45-47
  Info: User-controlled data flows into SQL query
  
  Code:
  45 |   const query = 'SELECT * FROM users WHERE id = ' + userId;
  46 |   db.execute(query, (err, results) => {
  47 |     res.json(results);
  
  Data Flow:
  req.params.id (line 44) → query (line 45) → db.execute (line 46)
  
  Fix:
  Use parameterized queries:
  const query = 'SELECT * FROM users WHERE id = ?';
  db.execute(query, [userId], callback);
```

## 🔧 Advanced Configuration

### Filtering Results

```bash
# Only high/critical issues
snyk code test --severity-threshold=high

# Specific severity levels
snyk code test --severity-threshold=medium

# All issues including low
snyk code test --severity-threshold=low
```

### Ignoring Issues

Create `.snyk` policy file in your project root:

```yaml
# Snyk policy file
version: v1.25.0
ignore:
  'SNYK-JS-EXAMPLE-123456':
    - '*':
        reason: False positive - reviewed by security team
        expires: 2024-12-31T00:00:00.000Z
        created: 2024-01-01T00:00:00.000Z
```

### Project Configuration

```bash
# Set project name
snyk code test --project-name="my-app-${BUILD_NUMBER}"

# Set organization
snyk code test --org="my-org-name"

# Combine options
snyk code test \
  --project-name="my-app" \
  --org="my-org" \
  --severity-threshold=high \
  --json-file-output=results.json
```

## 🎯 Common Use Cases

### Use Case 1: Pre-Commit Hook

Scan before every commit:

```bash
# .git/hooks/pre-commit
#!/bin/bash

echo "Running Snyk Code scan..."
snyk code test --severity-threshold=high

if [ $? -ne 0 ]; then
    echo "❌ Security issues found! Commit aborted."
    echo "Fix issues or use 'git commit --no-verify' to bypass"
    exit 1
fi

echo "✅ No security issues found"
```

Make it executable:
```bash
chmod +x .git/hooks/pre-commit
```

### Use Case 2: Pull Request Checks

In your Jenkins pipeline:

```groovy
stage('Security Check') {
    steps {
        script {
            def result = sh(
                script: 'snyk code test --severity-threshold=high',
                returnStatus: true
            )
            if (result != 0) {
                error("Security vulnerabilities found! Review and fix before merging.")
            }
        }
    }
}
```

### Use Case 3: Scheduled Scans

Set up nightly scans in Jenkins:

1. Configure pipeline
2. Enable "Build periodically"
3. Schedule: `H 2 * * *` (2 AM daily)
4. Email team with results

### Use Case 4: Multi-Project Scan

Scan multiple projects:

```bash
#!/bin/bash
# scan-all.sh

PROJECTS=("frontend" "backend" "api")

for project in "${PROJECTS[@]}"; do
    echo "Scanning $project..."
    cd "$project"
    npm install
    snyk code test --json-file-output="../results-$project.json" || true
    cd ..
done

echo "✅ All projects scanned"
```

## 📈 Best Practices

### 1. **Scan Early, Scan Often**
- Scan during development (locally)
- Scan on every commit/PR
- Scan before deployment
- Scan on schedule (weekly/nightly)

### 2. **Set Appropriate Thresholds**
- Development: Show all issues (`--severity-threshold=low`)
- CI/CD: Fail on high/critical (`--severity-threshold=high`)
- Production deployments: Block on any high+ issues

### 3. **Review and Triage**
- Not all findings need immediate fixes
- Use `.snyk` policy for accepted risks
- Document decisions
- Set expiration dates for ignores

### 4. **Integrate into Workflow**
```
Write Code → Scan Locally → Commit → PR Scan → Review → Merge → Deploy Scan
```

### 5. **Track Trends**
- Use Jenkins dashboard for trends
- Monitor new vs existing issues
- Set goals (e.g., "No high issues in production")

## 🐛 Troubleshooting

### Authentication Failed

```bash
# Get fresh token from https://app.snyk.io/account
snyk auth NEW_TOKEN

# Verify
snyk whoami
```

### No Files to Scan

```bash
# Check supported files exist
ls -la *.js *.ts *.py *.java

# Snyk Code supports:
# JavaScript, TypeScript, Python, Java, C#, PHP, Go, Ruby, and more!
```

### Out of Memory

```bash
# Increase Node.js memory
export NODE_OPTIONS="--max-old-space-size=4096"
snyk code test
```

### Jenkins Credential Issues

1. Verify credential ID is exactly `snyk-api-token`
2. Check token is valid in Snyk web app
3. Test authentication in a simple pipeline first

### Scan Takes Too Long

```bash
# Exclude files/directories
echo "node_modules/" > .snykignore
echo "dist/" >> .snykignore
echo "build/" >> .snykignore
```

## 📚 Additional Resources

- [Snyk Code Documentation](https://docs.snyk.io/snyk-code)
- [Snyk CLI Commands](https://docs.snyk.io/snyk-cli/commands/code)
- [Language Support](https://docs.snyk.io/snyk-code/snyk-code-language-and-framework-support)
- [Snyk IDE Plugins](https://docs.snyk.io/integrate-with-snyk/ide-tools)

## 💡 Pro Tips

### Tip 1: IDE Integration

Install Snyk plugins for:
- VS Code
- IntelliJ IDEA
- Visual Studio
- Eclipse

Get real-time feedback as you code!

### Tip 2: Compare Before/After

```bash
# Before fix
snyk code test --json-file-output=before.json

# Make fixes

# After fix
snyk code test --json-file-output=after.json

# Compare
diff before.json after.json
```

### Tip 3: Team Reports

Share results with team:

```bash
# Generate HTML report
snyk code test --json-file-output=results.json
snyk-to-html -i results.json -o report.html

# Send via email or upload to shared drive
```

### Tip 4: Combine with Other Tools

```bash
# Run Snyk Code + linters + tests
npm run lint && \
snyk code test && \
npm test
```

---

**Questions?** Check the main [README.md](README.md) or visit [Snyk Documentation](https://docs.snyk.io/)
