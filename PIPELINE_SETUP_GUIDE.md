# 🚀 Snyk Code Pipeline Setup Guide

## 📋 Three Pipelines Created for You

### Pipeline 1: Snyk Code with Plugin
**File:** `pipelines/1-snyk-code-plugin.groovy`
- Uses Snyk Jenkins Plugin
- Generates SARIF + HTML reports
- Sends results to Snyk platform
- Shows results in Warnings NG dashboard

### Pipeline 2: Snyk Code with CLI
**File:** `pipelines/2-snyk-code-cli.groovy`
- Uses Snyk CLI directly
- Generates SARIF + HTML reports
- Monitors project in Snyk platform
- Shows results in Warnings NG dashboard
- Never fails the build

### Pipeline 3: Snyk Code CLI - Fail on High Severity
**File:** `pipelines/3-snyk-code-cli-fail-on-high.groovy`
- Uses Snyk CLI directly
- Generates SARIF + HTML reports
- Monitors project in Snyk platform
- Shows results in Warnings NG dashboard
- **FAILS the build if HIGH or CRITICAL vulnerabilities found** 🔒

---

## 🎯 How to Create Each Pipeline in Jenkins

### For Each Pipeline:

1. Go to Jenkins Dashboard: http://localhost:8080
2. Click **"New Item"** (top left)
3. Enter a name (suggestions below)
4. Select **"Pipeline"**
5. Click **"OK"**

### Configure the Pipeline:

1. Scroll down to **"Pipeline"** section
2. For **"Definition"** select: **"Pipeline script"**
3. Copy the entire contents of the pipeline file
4. Paste into the **Script** text box
5. Click **"Save"**

---

## 📛 Suggested Pipeline Names

- **Pipeline 1:** `Snyk-Code-Plugin`
- **Pipeline 2:** `Snyk-Code-CLI`
- **Pipeline 3:** `Snyk-Code-CLI-FailOnHigh`

---

## 🎨 What You'll See in the Dashboard

### Warnings NG Dashboard Features:
- 📊 **Trend charts** showing vulnerabilities over time
- 🔍 **Severity breakdown** (Critical, High, Medium, Low)
- 📝 **Detailed findings** with file locations and remediation advice
- 📈 **Quality gates** status
- 🎯 **Drill-down capability** to specific issues

### HTML Reports Include:
- 📄 Beautiful formatted vulnerability list
- 🔒 Severity ratings
- 💡 Remediation suggestions
- 📍 Exact file and line locations
- 🔗 Links to Snyk documentation

### Snyk Platform Monitoring:
- ☁️ Results sent to Snyk.io for continuous monitoring
- 📧 Get alerts when new vulnerabilities are discovered
- 🔄 Track fixes over time
- 👥 Share with your team

---

## 🔍 Understanding Each Pipeline's Output

### SARIF Format (for Warnings NG):
- Industry-standard static analysis format
- Rich metadata about vulnerabilities
- Perfect for dashboard visualization
- Integrates with many security tools

### HTML Format (for Human Review):
- Easy-to-read web page
- Beautiful formatting
- Shareable with non-technical stakeholders
- Can be archived and compared

### Snyk Platform (for Continuous Monitoring):
- `snyk code monitor` sends snapshot to Snyk.io
- Tracks your project over time
- Alerts on new vulnerabilities in existing code
- Provides fix recommendations

---

## 🚀 Running Your First Scan

1. **Create all 3 pipelines** (recommended) or start with Pipeline 2
2. Click on a pipeline name
3. Click **"Build Now"** (left sidebar)
4. Watch the **Console Output** in real-time
5. After completion, view:
   - **Warnings NG Dashboard** link (left sidebar)
   - **Snyk Code HTML Report** link (left sidebar)
   - **Snyk Platform** at https://app.snyk.io

---

## 🔒 Pipeline 3 Behavior (Fail on High)

This pipeline is designed for **gating deployments**:

- ✅ **PASSES** if only Low/Medium vulnerabilities found
- ❌ **FAILS** if High/Critical vulnerabilities found
- 📊 **Always generates reports** regardless of result
- 🎯 Use this for production deployments

Example use cases:
- Block merges to main branch
- Prevent production deployments
- Enforce security standards
- Meet compliance requirements

---

## 📖 Expected Results for Workshop-App-Demo-Goof

The **Goof** application is intentionally vulnerable, so you should see:

- ❌ Multiple HIGH severity issues
- ❌ SQL Injection vulnerabilities
- ❌ Command Injection vulnerabilities
- ❌ Path Traversal issues
- ❌ Cross-Site Scripting (XSS)
- ❌ And more...

This is **expected and good** - it proves your scanning is working! 🎯

**Pipeline 3 will FAIL** - this is correct behavior for a vulnerable app!

---

## 🎓 Next Steps After First Scan

1. ✅ Review findings in Warnings NG dashboard
2. ✅ Explore HTML reports
3. ✅ Check Snyk platform at https://app.snyk.io
4. ✅ Compare results between all 3 pipelines
5. ✅ Set up scheduled scans (optional)

---

## 🔧 Troubleshooting

### "Snyk command not found"
- Jenkins may need Snyk CLI in PATH
- The pipelines handle this automatically

### "Certificate error"
- Check if Zscaler certificate is properly configured
- `NODE_TLS_REJECT_UNAUTHORIZED=0` is set in pipelines

### "Credentials not found"
- Ensure `snyk-api-token` credential is created
- ID must be exactly `snyk-api-token`

### "HTML report not generated"
- `snyk-to-html` needs to be installed
- Pipelines install it automatically

---

## 🎉 You're Ready!

Your Jenkins environment now has enterprise-grade security scanning! 🔒✨

**Start with Pipeline 2 (CLI) for the most reliable results.**

Happy secure coding! 💪


