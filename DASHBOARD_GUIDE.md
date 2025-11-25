# Jenkins Warnings NG Dashboard Guide

Complete guide to using the Warnings NG plugin for beautiful Snyk security scan visualizations.

## 🎨 What is Warnings NG?

The [Warnings Next Generation (NG) Plugin](https://www.jenkins.io/doc/pipeline/steps/warnings-ng/) is a powerful Jenkins plugin that collects compiler warnings, static analysis results, and security scan findings, then displays them in an interactive dashboard with:

- 📊 **Trend Charts** - Visual graphs showing vulnerability trends over time
- 🎯 **Severity Distribution** - Pie charts and bar graphs by severity level
- 📈 **New vs Fixed Issues** - Track which issues are new, fixed, or outstanding
- 🔍 **Drill-Down Details** - Click through to see exact code locations
- 📋 **Beautiful Tables** - Sortable, filterable issue lists
- 🎨 **Rich UI** - Modern, interactive interface

## ✨ Key Features

### 1. Visual Dashboards
- Interactive charts showing vulnerability trends
- Severity distribution (Critical, High, Medium, Low)
- Issue type categorization
- Build-to-build comparisons

### 2. Detailed Reports
- Line-by-line issue details
- Source code preview with highlighted issues
- Issue descriptions and remediation advice
- Links to external documentation

### 3. Quality Gates
- Set thresholds for acceptable vulnerability counts
- Automatically mark builds as unstable or failed
- Different thresholds for different severity levels
- Delta-based quality gates (new issues only)

### 4. Trend Analysis
- Historical data across multiple builds
- New, fixed, and outstanding issue tracking
- Build health metrics
- Customizable time ranges

## 🚀 Setup

### Step 1: Verify Plugin Installation

The Warnings NG plugin is already included in your Jenkins setup. Verify it's installed:

1. Go to **Manage Jenkins** → **Manage Plugins**
2. Click on **Installed** tab
3. Search for "Warnings Next Generation"
4. Should show as installed ✓

### Step 2: Run Enhanced Pipeline

Use the new pipeline with dashboard integration:

**File**: `sample-pipelines/Jenkinsfile.snyk-cli-dashboard`

This pipeline:
- Runs Snyk Code (SAST) scan
- Runs Snyk Open Source (SCA) scan
- Runs Snyk Container scan
- Exports results in SARIF format
- Publishes to Warnings NG dashboard

### Step 3: Create Dashboard Pipeline

1. Click **New Item**
2. Name: `Goof-Security-Dashboard`
3. Type: **Pipeline**
4. Pipeline script: Copy content from `Jenkinsfile.snyk-cli-dashboard`
5. **Save** and **Build Now**

## 📊 Viewing Results

### Build Dashboard

After a build completes, you'll see new sections in the build view:

```
Build #1
├── 🔍 Snyk Code Security Issues (X issues)
├── 📦 Snyk Open Source Vulnerabilities (X issues)
└── 🐳 Snyk Container Vulnerabilities (X issues)
```

Click on any section to see detailed reports!

### Trend Charts

On the project homepage, you'll see:

- **Security Trend Chart** - Line graph showing issues over time
- **Distribution Chart** - Breakdown by severity
- **New vs Fixed** - Tracking issue lifecycle

### Issue Details

Click through to see:

1. **Summary View**: Total issues by severity
2. **Issues Tab**: Sortable table of all findings
3. **Details**: Individual issue descriptions
4. **Severity**: Color-coded (🔴 Critical, 🟠 High, 🟡 Medium, 🟢 Low)
5. **File Location**: Exact file and line number
6. **Description**: What the issue is and how to fix it

## 🎯 Understanding the Dashboard

### Main Metrics

- **Total Issues**: All vulnerabilities found
- **New Issues**: Issues introduced in this build
- **Fixed Issues**: Issues resolved since last build
- **Outstanding Issues**: Pre-existing issues

### Severity Levels

| Severity | Icon | Description |
|----------|------|-------------|
| **Critical** | 🔴 | Immediate security risk, fix ASAP |
| **High** | 🟠 | Significant security risk, prioritize |
| **Medium** | 🟡 | Moderate risk, should fix |
| **Low** | 🟢 | Minor issue, fix when possible |

### Quality Gate Status

- ✅ **Success**: Below all thresholds
- ⚠️ **Unstable**: Exceeded warning threshold
- ❌ **Failed**: Exceeded failure threshold

## 🔧 Customization

### Adjust Quality Gates

Edit the `recordIssues` block in the pipeline:

```groovy
recordIssues(
    enabledForFailure: true,
    tool: sarif(pattern: 'snyk-code.sarif'),
    qualityGates: [
        [threshold: 1, type: 'TOTAL', unstable: true],      // Warn on any issue
        [threshold: 5, type: 'NEW', unstable: true],        // Warn on 5+ new issues
        [threshold: 1, type: 'TOTAL_HIGH', unstable: false] // Fail on high severity
    ],
    healthy: 0,        // 100% healthy = 0 issues
    unhealthy: 5,      // Unhealthy at 5+ issues
    minimumSeverity: 'LOW',
    name: '🔍 Snyk Code Security Issues'
)
```

### Filter by Severity

Only show high and critical issues:

```groovy
recordIssues(
    tool: sarif(pattern: 'snyk-code.sarif'),
    minimumSeverity: 'HIGH',  // Only show HIGH and CRITICAL
    name: 'Critical Security Issues'
)
```

### Custom Icons and Names

Personalize your reports:

```groovy
recordIssues(
    tool: sarif(
        pattern: 'snyk-code.sarif',
        name: 'My Custom Security Scan',
        id: 'my-scan'
    ),
    name: '🚨 Critical Security Findings'
)
```

## 📈 Use Cases

### 1. Security Dashboard for Management

Create a high-level view showing:
- Total vulnerabilities over time
- Trend: improving or worsening?
- Percentage of critical issues resolved

### 2. Developer Workflow

Help developers:
- See exactly which issues they introduced
- Get remediation advice
- Track their progress fixing issues

### 3. Quality Gates

Enforce security standards:
- Fail builds with critical vulnerabilities
- Warn on increasing vulnerability count
- Block deployment of insecure code

### 4. Compliance Reporting

Generate reports showing:
- Security posture over time
- Issue resolution timeline
- Compliance with security policies

## 🎓 Best Practices

### 1. Set Realistic Thresholds

Start with warning-only, then gradually enforce stricter limits:

```groovy
// Week 1: Just track
qualityGates: [[threshold: 100, type: 'TOTAL', unstable: false]]

// Week 2: Warn on new issues
qualityGates: [[threshold: 1, type: 'NEW', unstable: true]]

// Week 3: Fail on critical
qualityGates: [[threshold: 1, type: 'TOTAL_HIGH', unstable: false]]
```

### 2. Focus on New Issues

Don't let technical debt block progress:

```groovy
qualityGates: [
    [threshold: 0, type: 'NEW', unstable: false]  // No new issues allowed
]
```

### 3. Track Trends

Review the trend charts weekly to ensure:
- Total issues are decreasing
- New issues are being fixed quickly
- No sudden spikes in vulnerabilities

### 4. Use Multiple Scans

Separate dashboards for different scan types:
- SAST (Snyk Code)
- SCA (Open Source)
- Container
- IaC

This makes it easier to track improvements in each area.

## 🔍 Advanced Features

### SARIF Format

Snyk exports results in [SARIF format](https://sarifweb.azurewebsites.net/) (Static Analysis Results Interchange Format), which:
- Is a standard format for static analysis
- Includes detailed issue information
- Works with many security tools
- Provides rich metadata

### Integration with GitHub

The Warnings NG plugin can:
- Post comments on pull requests
- Create GitHub checks
- Block merges based on quality gates

### Export Options

Export reports in multiple formats:
- HTML reports
- JSON for custom processing
- SARIF for tool interoperability
- JUnit XML for CI/CD integration

## 🐛 Troubleshooting

### No Issues Showing

**Check:**
1. SARIF files are being generated (`snyk-*.sarif`)
2. Files are in the workspace root
3. Pattern matches the files (`snyk-code.sarif`)

**Solution:**
```groovy
sh 'ls -la snyk-*.sarif'  // Verify files exist
```

### Plugin Not Found

**Rebuild Jenkins image:**
```bash
docker-compose down
docker-compose build --no-cache
docker-compose up -d
```

### Duplicate IDs Error

Each report needs a unique ID:

```groovy
// BAD - Same ID
recordIssues(tool: sarif(id: 'snyk'))
recordIssues(tool: sarif(id: 'snyk'))  // Error!

// GOOD - Unique IDs
recordIssues(tool: sarif(id: 'snyk-code'))
recordIssues(tool: sarif(id: 'snyk-oss'))
```

## 📚 Additional Resources

- [Warnings NG Plugin Documentation](https://www.jenkins.io/doc/pipeline/steps/warnings-ng/)
- [Warnings NG GitHub](https://github.com/jenkinsci/warnings-ng-plugin)
- [SARIF Format Specification](https://sarifweb.azurewebsites.net/)
- [Snyk Documentation](https://docs.snyk.io/)

## 💡 Example Dashboard Interpretation

After running the Goof app scan, you might see:

```
🔍 Snyk Code Security Issues: 12 issues
├── Critical: 3 (SQL Injection, Command Injection)
├── High: 4 (XSS, Path Traversal)
├── Medium: 3
└── Low: 2

📦 Snyk Open Source Vulnerabilities: 47 issues
├── Critical: 8 (Log4Shell, etc.)
├── High: 15
├── Medium: 18
└── Low: 6

🐳 Snyk Container Vulnerabilities: 23 issues
├── Critical: 2
├── High: 8
├── Medium: 10
└── Low: 3
```

Click on each to see:
- Exact vulnerability details
- Affected code/dependencies
- Remediation steps
- CVE references

---

**Enjoy your beautiful security dashboards! 📊**

