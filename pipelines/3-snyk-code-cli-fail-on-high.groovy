// ⚠️  REGIONAL HOSTING: If you're using Snyk EU, AU, or US-02 tenants:
// 1. Configure your region: snyk config environment SNYK-EU-01 (or SNYK-AU-01, SNYK-US-02)
// 2. More info: https://docs.snyk.io/snyk-data-and-governance/regional-hosting-and-data-residency

pipeline {
    agent any
    
    environment {
        // Configure your Snyk API token as a Jenkins credential with ID 'snyk-api-token'
        SNYK_TOKEN = credentials('snyk-api-token')
    }
    
    stages {
        stage('Checkout') {
            steps {
                echo '📥 Checking out vulnerable demo application...'
                git branch: 'main',
                    url: 'https://github.com/snyk-labs/nodejs-goof.git'
            }
        }
        
        stage('Install Dependencies') {
            steps {
                echo '📦 Installing npm dependencies...'
                sh 'npm install'
            }
        }
        
        stage('Snyk Code Scan - Fail on High') {
            steps {
                script {
                    sh '''
                        # For EU tenant users, configure your region first:
                        # snyk config environment SNYK-EU-01
                        
                        # Debug: Check if token is available
                        echo "Token starts with: ${SNYK_TOKEN:0:10}..."
                        
                        # Write token to a temporary file for Snyk to use
                        echo "$SNYK_TOKEN" > /tmp/.snyk_token_failhigh
                        export SNYK_TOKEN=$(cat /tmp/.snyk_token_failhigh)
                        
                        # Run Snyk Code test with --report flag
                        # This will always generate reports first
                        snyk code test \
                            --report \
                            --project-name="Workshop-App-Demo-Goof-FailOnHigh-Build-${BUILD_NUMBER}" \
                            --sarif-file-output=snyk-code-fail-high.sarif \
                            --json-file-output=snyk-code-fail-high.json \
                            || true
                        
                        # Clean up temp file
                        rm -f /tmp/.snyk_token_failhigh
                        
                        echo "✅ Snyk Code scan completed - reports generated and sent to Snyk platform"
                    '''
                    
                    // Now check severity and fail if high/critical found
                    def scanResult = sh(
                        script: '''
                            snyk code test --severity-threshold=high --json
                        ''',
                        returnStatus: true
                    )
                    
                    // Store result for post-processing
                    env.SCAN_FAILED = scanResult != 0 ? 'true' : 'false'
                    
                    if (scanResult != 0) {
                        echo "⚠️  HIGH or CRITICAL severity vulnerabilities found!"
                        echo "Pipeline will be marked as FAILED after publishing reports"
                    } else {
                        echo "✅ No HIGH or CRITICAL vulnerabilities found"
                    }
                }
            }
        }
        
        
        stage('Generate HTML Report') {
            steps {
                sh '''
                    # Install snyk-to-html globally
                    npm config set strict-ssl false
                    npm install -g snyk-to-html || true
                    npm config set strict-ssl true
                    
                    # Convert JSON to HTML report
                    if [ -f snyk-code-fail-high.json ]; then
                        snyk-to-html -i snyk-code-fail-high.json -o snyk-code-fail-high.html || true
                        echo "✅ HTML report generated"
                    else
                        echo "⚠️  No JSON file found to convert"
                    fi
                '''
            }
        }
        
        stage('Publish to Dashboard') {
            steps {
                // Publish SARIF results to Warnings NG Dashboard
                recordIssues(
                    tool: sarif(pattern: 'snyk-code-fail-high.sarif'),
                    name: 'Snyk Code (Fail on High)',
                    qualityGates: [
                        [threshold: 1, type: 'TOTAL_HIGH', unstable: true],
                        [threshold: 1, type: 'TOTAL_ERROR', unstable: true]
                    ],
                    healthy: 0,
                    unhealthy: 1,
                    enabledForFailure: true
                )
                
                // Publish HTML report
                publishHTML([
                    allowMissing: false,
                    alwaysLinkToLastBuild: true,
                    keepAll: true,
                    reportDir: '.',
                    reportFiles: 'snyk-code-fail-high.html',
                    reportName: 'Snyk Code HTML Report (Fail on High)',
                    reportTitles: 'Snyk Code Security Report'
                ])
            }
        }
        
        stage('Evaluate Security Gate') {
            steps {
                script {
                    if (env.SCAN_FAILED == 'true') {
                        echo "❌ SECURITY GATE FAILED: HIGH or CRITICAL vulnerabilities detected!"
                        error("Build failed due to HIGH or CRITICAL severity vulnerabilities")
                    } else {
                        echo "✅ SECURITY GATE PASSED: No HIGH or CRITICAL vulnerabilities"
                    }
                }
            }
        }
    }
    
    post {
        always {
            echo '=== Snyk Code Scan Complete (Fail on High Severity) ==='
            echo '📊 View SARIF results in Warnings NG Dashboard'
            echo '📄 View HTML report in "Snyk Code HTML Report" link'
            echo '☁️  Results monitored in Snyk platform'
        }
        failure {
            echo '❌ Pipeline FAILED due to HIGH or CRITICAL severity vulnerabilities!'
            echo '🔒 Please review the security findings in the dashboard and reports'
        }
        success {
            echo '✅ Pipeline PASSED - No HIGH or CRITICAL vulnerabilities detected'
        }
    }
}


