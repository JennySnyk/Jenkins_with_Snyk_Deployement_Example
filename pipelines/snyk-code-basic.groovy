// Basic Snyk Code (SAST) Pipeline
// This pipeline demonstrates the simplest way to run Snyk Code scans

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

