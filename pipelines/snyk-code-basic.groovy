// Basic Snyk Code (SAST) Pipeline
// This pipeline demonstrates the simplest way to run Snyk Code scans
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
                echo '🔍 Running Snyk Code scan (SAST)...'
                sh '''
                    # For EU tenant users, configure your region first:
                    # snyk config environment SNYK-EU-01
                    
                    # Authenticate with Snyk
                    snyk auth ${SNYK_TOKEN}
                    
                    # Run Snyk Code test
                    snyk code test || true
                '''
            }
        }
    }
    
    post {
        always {
            echo '✅ Snyk Code scan complete!'
            echo '📊 Check console output above for vulnerability details'
        }
    }
}

