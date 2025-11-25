# Jenkins Local Environment for Snyk Code

A simplified Jenkins setup for scanning applications with **Snyk Code (SAST)** to find security vulnerabilities in your source code.

## 🚀 Features

- **Jenkins LTS** running standalone (no Docker required)
- **Snyk Code (SAST)** - Static Application Security Testing
- **Sample Pipelines** - Ready-to-use examples
- **Warnings NG Dashboard** - Visual security reports
- **Persistent Storage** - All data saved locally

## 🎯 What is Snyk Code?

Snyk Code is a **Static Application Security Testing (SAST)** tool that scans your source code to find:
- SQL Injection vulnerabilities
- Cross-Site Scripting (XSS)
- Command Injection
- Path Traversal
- Hardcoded secrets
- And many other code-level security issues

## 📋 Prerequisites

- **Java 17** - Install with `brew install openjdk@17`
- **Node.js & npm** - For JavaScript/Node.js projects
- **Snyk CLI** - Install with `npm install -g snyk`
- **Snyk Account** - Sign up free at [snyk.io](https://snyk.io)

## 🛠️ Quick Start

### Option 1: Using the Start Script (Recommended)

```bash
cd jenkins-war
./start-jenkins.sh
```

Wait 30-60 seconds, then access Jenkins at **http://localhost:8080**

### Option 2: Manual Start

```bash
cd jenkins-war
export JENKINS_HOME="$(pwd)/jenkins_home"
export JAVA_HOME="/opt/homebrew/opt/openjdk@17"
java -jar jenkins.war --httpPort=8080
```

## 📝 Manual Workflow (Without Scripts)

### Step 1: Install Prerequisites

```bash
# Install Java 17
brew install openjdk@17

# Install Snyk CLI
npm install -g snyk

# Verify installations
java --version
snyk --version
```

### Step 2: Get Snyk API Token

1. Sign up/login at https://app.snyk.io
2. Go to Account Settings: https://app.snyk.io/account
3. Copy your API Token

### Step 3: Authenticate Snyk CLI

```bash
snyk auth YOUR_API_TOKEN
```

### Step 4: Clone a Test Application

```bash
git clone https://github.com/snyk-labs/nodejs-goof.git
cd nodejs-goof
npm install
```

### Step 5: Run Snyk Code Scan (Manual)

```bash
# Basic scan
snyk code test

# Scan with JSON output
snyk code test --json-file-output=results.json

# Scan with SARIF output (for dashboard integration)
snyk code test --sarif-file-output=results.sarif

# Scan with severity threshold
snyk code test --severity-threshold=high
```

### Step 6: View Results

- Console output shows vulnerabilities immediately
- JSON file contains detailed structured results
- SARIF file can be imported into security dashboards

### Step 7: Start Jenkins

```bash
cd jenkins-war
./start-jenkins.sh
```

### Step 8: Configure Jenkins

1. Access http://localhost:8080
2. Create an admin account
3. Go to **Manage Jenkins** → **Manage Credentials**
4. Add a new credential:
   - Kind: Secret text
   - Secret: Your Snyk API token
   - ID: `snyk-api-token`

### Step 9: Create a Pipeline

1. Click **New Item**
2. Enter name: `Snyk-Code-Scan`
3. Select **Pipeline**, click OK
4. Copy content from `sample-pipelines/Jenkinsfile.snyk-code`
5. Click **Save** and **Build Now**

## 📁 Project Structure

```
jenkins-local/
├── jenkins-war/                         # Standalone Jenkins
│   ├── start-jenkins.sh                 # Start script
│   ├── stop-jenkins.sh                  # Stop script
│   ├── jenkins.war                      # Jenkins executable
│   ├── jenkins.log                      # Log file
│   └── jenkins_home/                    # Configuration & data
├── pipelines/                           # Pipeline examples
│   ├── snyk-code-basic.groovy           # Simple Snyk Code scan
│   ├── snyk-code-with-reports.groovy    # With HTML/SARIF reports
│   ├── 2-snyk-code-cli.groovy           # Full featured
│   └── 3-snyk-code-cli-fail-on-high.groovy  # With quality gates
├── sample-pipelines/                    # Jenkinsfile examples
│   ├── Jenkinsfile.hello-world          # Hello world
│   ├── Jenkinsfile.multi-stage          # Multi-stage example
│   └── Jenkinsfile.snyk-code            # Snyk Code scan
├── .gitignore                           # Excludes sensitive files
├── README.md                            # This file
├── QUICKSTART.md                        # Quick start guide
└── SNYK_SETUP.md                        # Detailed Snyk setup
```

## 🎯 Sample Pipelines

### 1. Hello World Pipeline
Learn Jenkins basics with a simple pipeline.

**File:** `sample-pipelines/Jenkinsfile.hello-world`

### 2. Multi-Stage Pipeline  
Production-ready pipeline with parameters and stages.

**File:** `sample-pipelines/Jenkinsfile.multi-stage`

### 3. Snyk Code Security Pipeline 🔒
Scan for code vulnerabilities with Snyk Code.

**File:** `sample-pipelines/Jenkinsfile.snyk-code`

**Features:**
- Snyk Code (SAST) scanning
- JSON report generation
- Automatic archiving
- Clean console output

### 4. Snyk Code with Dashboard 📊
Enhanced scanning with visual dashboards.

**File:** `pipelines/snyk-code-with-reports.groovy`

**Features:**
- SARIF format for Warnings NG
- HTML report generation
- Interactive dashboard
- Trend charts

## 📚 Common Commands

### Jenkins Commands

```bash
# Start Jenkins
cd jenkins-war && ./start-jenkins.sh

# Stop Jenkins
cd jenkins-war && ./stop-jenkins.sh

# View logs
cd jenkins-war && tail -f jenkins.log

# Backup Jenkins data
cd jenkins-war && tar czf backup.tar.gz jenkins_home/
```

### Snyk Code Commands

```bash
# Authenticate
snyk auth YOUR_TOKEN

# Basic scan
snyk code test

# Scan with JSON output
snyk code test --json-file-output=results.json

# Scan with SARIF output
snyk code test --sarif-file-output=results.sarif

# Fail on high severity issues
snyk code test --severity-threshold=high

# Show all issues (including low severity)
snyk code test --severity-threshold=low
```

## 🔧 Configuration

### Change Jenkins Port

Edit `jenkins-war/start-jenkins.sh` and change:
```bash
--httpPort=9090
```

### Add Jenkins Plugins

1. Go to **Manage Jenkins** > **Manage Plugins**
2. Select **Available** tab
3. Search and install plugins
4. Restart Jenkins

### Configure Snyk Token in Jenkins

1. **Manage Jenkins** → **Manage Credentials**
2. Click **(global)** domain
3. **Add Credentials**
4. Kind: Secret text
5. Secret: Your Snyk API token
6. ID: `snyk-api-token`
7. Click **OK**

## 🐛 Troubleshooting

### Java Not Found

```bash
brew install openjdk@17
export JAVA_HOME="/opt/homebrew/opt/openjdk@17"
```

### Snyk CLI Not Found

```bash
npm install -g snyk
snyk --version
```

### Port 8080 Already in Use

```bash
# Find what's using it
lsof -i :8080

# Kill Jenkins processes
pkill -f jenkins.war

# Or change the port in start-jenkins.sh
```

### Jenkins Won't Start

```bash
# Check logs
cd jenkins-war && tail -f jenkins.log

# Common fixes:
# - Ensure Java 17 is installed
# - Check port 8080 is available
# - Verify jenkins_home directory exists
```

### Snyk Authentication Failed

```bash
# Get a fresh token from https://app.snyk.io/account
snyk auth YOUR_NEW_TOKEN

# Verify authentication
snyk test --help
```

## 🔒 Security Notes

### Before Pushing to GitHub

This repo includes `.gitignore` to exclude:
- `jenkins_home/` (contains credentials and secrets)
- `credentials.xml` (encrypted tokens)
- `secrets/` directory
- Log files
- Build artifacts

**Always verify:**
```bash
git status
# Make sure no sensitive files are staged
```

### Best Practices

1. **Never commit tokens** - Always use Jenkins credentials
2. **Use .gitignore** - Exclude sensitive directories
3. **Change default passwords** - If you set up user accounts
4. **Keep Jenkins updated** - Check for security updates
5. **Use HTTPS in production** - For Jenkins access

## 📖 Learn More

- [Snyk Code Documentation](https://docs.snyk.io/snyk-code)
- [Jenkins Documentation](https://www.jenkins.io/doc/)
- [Jenkins Pipeline Syntax](https://www.jenkins.io/doc/book/pipeline/syntax/)
- [Snyk CLI Reference](https://docs.snyk.io/snyk-cli/commands/code)

## 💡 Tips

### Running Scans Locally (No Jenkins)

```bash
# Clone and scan any Node.js project
git clone <repo-url>
cd <repo-directory>
npm install
snyk auth <your-token>
snyk code test
```

### Generating HTML Reports

```bash
# Install snyk-to-html
npm install -g snyk-to-html

# Generate JSON first
snyk code test --json-file-output=results.json

# Convert to HTML
snyk-to-html -i results.json -o report.html
```

### Integrating with CI/CD

Use the pipelines in this repo as templates for:
- GitHub Actions
- GitLab CI
- CircleCI
- Any CI/CD platform

Just adapt the Groovy syntax to your platform's YAML/config format.

---

**Ready to start?** Check [QUICKSTART.md](QUICKSTART.md) for a 2-minute setup guide!
