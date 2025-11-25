// Snyk Code Pipeline with Reports
// This pipeline runs Snyk Code scans and generates multiple report formats
//
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
                sh '''
                    node --version
                    npm --version
                    npm install
                '''
            }
        }
        
        stage('Snyk Code Scan') {
            steps {
                echo '🔍 Running Snyk Code scan with reports...'
                sh '''
                    # For EU tenant users, configure your region first:
                    # snyk config environment SNYK-EU-01
                    
                    # Authenticate with Snyk
                    snyk auth ${SNYK_TOKEN}
                    
                    # Run Snyk Code test with JSON and SARIF output
                    snyk code test \
                        --sarif-file-output=snyk-code.sarif \
                        --json-file-output=snyk-code.json \
                        || true
                    
                    echo "✅ Snyk Code scan complete"
                    ls -lh snyk-code.* 2>/dev/null || echo "Report files not generated"
                '''
            }
        }
        
        stage('Generate HTML Report') {
            steps {
                echo '📄 Generating HTML report...'
                sh '''
                    # Install snyk-to-html if not already installed
                    npm list -g snyk-to-html || npm install -g snyk-to-html
                    
                    # Convert JSON to HTML
                    if [ -f snyk-code.json ]; then
                        snyk-to-html -i snyk-code.json \
                            -o snyk-code-report.html \
                            --summary || true
                        echo "✅ HTML report generated"
                    else
                        echo "⚠️  JSON file not found"
                    fi
                '''
            }
        }
        
        stage('Publish to Dashboard') {
            steps {
                echo '📊 Publishing results to Jenkins dashboard...'
                script {
                    // Publish SARIF to Warnings NG
                    if (fileExists('snyk-code.sarif')) {
                        recordIssues(
                            tool: sarif(
                                pattern: 'snyk-code.sarif',
                                name: 'Snyk Code',
                                id: 'snyk-code'
                            ),
                            name: '🔍 Snyk Code Security Report',
                            qualityGates: [[threshold: 1, type: 'TOTAL', unstable: false]]
                        )
                    }
                    
                    // Publish HTML report
                    if (fileExists('snyk-code-report.html')) {
                        publishHTML([
                            allowMissing: false,
                            alwaysLinkToLastBuild: true,
                            keepAll: true,
                            reportDir: '.',
                            reportFiles: 'snyk-code-report.html',
                            reportName: '📄 Snyk Code HTML Report'
                        ])
                    }
                }
            }
        }
    }
    
    post {
        always {
            echo '=== Snyk Code Scan Complete ==='
            echo '📊 View Dashboard: Click "🔍 Snyk Code Security Report" in the sidebar'
            echo '📄 View HTML Report: Click "📄 Snyk Code HTML Report" in the sidebar'
            
            // Archive reports
            archiveArtifacts artifacts: 'snyk-code.*', allowEmptyArchive: true, fingerprint: true
        }
    }
}

