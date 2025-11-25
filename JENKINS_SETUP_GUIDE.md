# Jenkins Setup Guide - Next Steps

## ✅ What You've Completed
1. ✅ Jenkins is running at http://localhost:8080
2. ✅ Jenkins setup wizard completed
3. ✅ Snyk CLI installed and authenticated
4. ✅ Snyk API token configured: `97cf915e-7877-4461-8c41-94d85117be5a`

## 🎯 Next: Create Your Security Scanning Pipelines

### Option 1: Snyk CLI Pipeline (Recommended)
This pipeline uses Snyk CLI directly and provides the most control.

**Steps:**
1. Go to Jenkins dashboard: http://localhost:8080
2. Click **"New Item"**
3. Enter name: `Snyk-CLI-Security-Scan`
4. Select **"Pipeline"** → Click **OK**
5. Scroll to **"Pipeline"** section
6. Definition: **"Pipeline script"**
7. Copy and paste the contents of `pipelines/snyk-cli-pipeline.groovy`
8. Click **"Save"**

### Option 2: Snyk Plugin Pipeline
This pipeline uses the Snyk Jenkins Plugin (requires plugin installation).

**First, install the Snyk Plugin:**
1. Go to **Manage Jenkins** → **Manage Plugins**
2. Go to **"Available"** tab
3. Search for **"Snyk Security Scanner"**
4. Check the box and click **"Install without restart"**
5. Wait for installation to complete

**Then create the pipeline:**
1. Click **"New Item"**
2. Enter name: `Snyk-Plugin-Security-Scan`
3. Select **"Pipeline"** → Click **OK**
4. Scroll to **"Pipeline"** section
5. Definition: **"Pipeline script"**
6. Copy and paste the contents of `pipelines/snyk-plugin-pipeline.groovy`
7. Click **"Save"**

### Install Warnings NG Plugin (for Dashboard)
1. Go to **Manage Jenkins** → **Manage Plugins**
2. Go to **"Available"** tab
3. Search for **"Warnings Next Generation"**
4. Check the box and click **"Install without restart"**
5. Wait for installation to complete

## 🔐 Configure Snyk Credentials in Jenkins

1. Go to **Manage Jenkins** → **Manage Credentials**
2. Click on **(global)** domain
3. Click **"Add Credentials"**
4. Configure:
   - **Kind:** Secret text
   - **Secret:** `97cf915e-7877-4461-8c41-94d85117be5a`
   - **ID:** `snyk-api-token`
   - **Description:** Snyk API Token
5. Click **"OK"**

## 🚀 Run Your First Security Scan

1. Go to your pipeline job
2. Click **"Build Now"**
3. Watch the build progress in **"Build History"**
4. Click on the build number to see details
5. View the **Warnings NG Dashboard** for vulnerability visualization

## 📊 What Each Pipeline Does

### Snyk CLI Pipeline:
- **Snyk Code** (SAST): Scans source code for security vulnerabilities
- **Snyk Open Source** (SCA): Scans dependencies for known vulnerabilities
- Generates SARIF reports for Warnings NG dashboard

### Snyk Plugin Pipeline:
- All-in-one scan using the Snyk Jenkins Plugin
- Scans code, dependencies, containers, and IaC
- Integrates directly with Snyk UI

## 🔧 Troubleshooting

### "Snyk command not found"
The pipelines run in Jenkins, which may not have Snyk in PATH. The CLI pipeline handles this by using the npm-installed version.

### SSL Certificate Errors
The pipelines include `NODE_TLS_REJECT_UNAUTHORIZED=0` to handle Zscaler SSL interception.

### Credentials Not Found
Make sure you created the `snyk-api-token` credential with ID exactly as shown above.

## 📖 Resources
- [Snyk CLI Documentation](https://docs.snyk.io/snyk-cli)
- [Snyk Jenkins Plugin](https://docs.snyk.io/integrations/ci-cd-integrations/jenkins-integration-overview)
- [Warnings NG Plugin](https://plugins.jenkins.io/warnings-ng/)

## 🎉 You're All Set!
Your Jenkins environment is ready for security scanning. Start building secure applications! 🔒

