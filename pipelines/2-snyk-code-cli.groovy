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
        
        stage('Snyk Code Test') {
            steps {
                script {
                    sh '''
                        # For EU tenant users, configure your region first:
                        # snyk config environment SNYK-EU-01
                        
                        # Write token to temp file for Snyk to use
                        echo "$SNYK_TOKEN" > /tmp/.snyk_token
                        export SNYK_TOKEN=$(cat /tmp/.snyk_token)
                        
                        # Run simple Snyk Code test
                        snyk code test || true
                        
                        # Clean up temp file
                        rm -f /tmp/.snyk_token
                        
                        echo "✅ Snyk Code test completed"
                    '''
                }
            }
        }
        
        stage('Generate SARIF & JSON Reports') {
            steps {
                script {
                    sh '''
                        # Write token to temp file for Snyk to use
                        echo "$SNYK_TOKEN" > /tmp/.snyk_token
                        export SNYK_TOKEN=$(cat /tmp/.snyk_token)
                        
                        # Run Snyk Code test with SARIF and JSON outputs
                        snyk code test \
                            --sarif-file-output=snyk-code-cli.sarif \
                            --json-file-output=snyk-code-cli.json \
                            || true
                        
                        # Clean up temp file
                        rm -f /tmp/.snyk_token
                        
                        echo "✅ SARIF and JSON reports generated"
                        ls -la snyk-code-cli.* 2>/dev/null || true
                    '''
                }
            }
        }
        
        stage('Generate HTML Report') {
            steps {
                script {
                    sh '''
                        # Install snyk-to-html if not already installed
                        npm config set strict-ssl false
                        npm list -g snyk-to-html || npm install -g snyk-to-html
                        npm config set strict-ssl true
                        
                        # Convert JSON to beautiful HTML report
                        if [ -f snyk-code-cli.json ]; then
                            snyk-to-html -i snyk-code-cli.json \
                                -o snyk-code-report.html \
                                --summary || true
                            
                            echo "✅ HTML report generated: snyk-code-report.html"
                            ls -lh snyk-code-report.html 2>/dev/null || echo "HTML generation may have failed"
                        else
                            echo "⚠️  JSON file not found, cannot generate HTML"
                        fi
                    '''
                }
            }
        }
        
        stage('Report to Snyk Platform') {
            steps {
                script {
                    sh '''
                        # Write token to temp file for Snyk to use
                        echo "$SNYK_TOKEN" > /tmp/.snyk_token
                        export SNYK_TOKEN=$(cat /tmp/.snyk_token)
                        
                        # Send results to Snyk platform
                        snyk code test \
                            --report \
                            --project-name="Workshop-App-Demo-Goof-CLI-Build-${BUILD_NUMBER}" \
                            || true
                        
                        # Clean up temp file
                        rm -f /tmp/.snyk_token
                        
                        echo "✅ Results sent to Snyk platform"
                    '''
                }
            }
        }
        
        
        stage('Publish to Warnings NG Dashboard') {
            steps {
                script {
                    if (fileExists('snyk-code-cli.sarif')) {
                        recordIssues(
                            tool: sarif(
                                pattern: 'snyk-code-cli.sarif',
                                name: 'Snyk Code Security Scanner',
                                id: 'snyk-code'
                            ),
                            name: '🛡️ Snyk Code Security Report',
                            sourceCodeEncoding: 'UTF-8',
                            
                            // Quality Gates
                            qualityGates: [
                                [threshold: 1, type: 'TOTAL_HIGH', unstable: true],
                                [threshold: 5, type: 'TOTAL_NORMAL', unstable: false],
                                [threshold: 1, type: 'NEW_HIGH', unstable: true],
                                [threshold: 100, type: 'TOTAL', unstable: false]
                            ],
                            
                            // Health Report
                            healthy: 5,
                            unhealthy: 10,
                            minimumSeverity: 'LOW',
                            
                            // Display Options
                            enabledForFailure: true,
                            aggregatingResults: true,
                            trendChartType: 'AGGREGATION_TOOLS',
                            
                            // Filters
                            ignoreQualityGate: false,
                            ignoreFailedBuilds: false,
                            
                            // Reference Build
                            referenceJobName: '',
                            
                            // Blame and Forensics
                            skipPublishingChecks: false
                        )
                        echo "✅ Results published to Warnings NG Dashboard with enhanced visualization"
                    } else {
                        echo "⚠️  No SARIF file found to publish"
                    }
                }
            }
        }
        
        stage('Publish HTML Report') {
            steps {
                script {
                    if (fileExists('snyk-code-report.html')) {
                        // Archive the HTML report as a build artifact
                        archiveArtifacts artifacts: 'snyk-code-report.html', 
                                       fingerprint: true,
                                       allowEmptyArchive: false
                        
                        // Publish HTML report for easy viewing
                        publishHTML([
                            allowMissing: false,
                            alwaysLinkToLastBuild: true,
                            keepAll: true,
                            reportDir: '.',
                            reportFiles: 'snyk-code-report.html',
                            reportName: '📄 Snyk Code HTML Report',
                            reportTitles: 'Snyk Code Security Report'
                        ])
                        
                        echo "✅ HTML report published - view it in the left sidebar!"
                    } else {
                        echo "⚠️  No HTML report found to publish"
                    }
                }
            }
        }
    }
    
    post {
        always {
            echo '=== Snyk Code Scan Complete ==='
            echo '📊 View Warnings NG Dashboard: Click "🛡️ Snyk Code Security Report" (left sidebar)'
            echo '📄 View HTML Report: Click "📄 Snyk Code HTML Report" (left sidebar)'
            echo '☁️  View in Snyk Platform: https://app.snyk.io'
        }
        success {
            echo '✅ Pipeline completed successfully'
        }
        failure {
            echo '⚠️  Pipeline failed - check console output above'
        }
    }
}


