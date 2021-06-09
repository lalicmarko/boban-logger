pipeline {
    agent any

    environment {
        LC_CTYPE = 'en_US.UTF-8'
        ANDROID_HOME = "/home/jenkins/Android/Sdk"
    }

    stages {
        stage('Build') {

            steps {
                sh 'echo "Hello World"'
                sh '''
                    echo "Multiline shell steps works too"
                    ls -lah
                '''
            }
        }
        stage('World') {

            steps {
                sh 'echo "World STAGE"'
            }
        }
        stage('Test') {
            steps {
                sh './gradlew check'
            }
        }
        stage('Upload') {
            steps {
                retry(10) {
                    sh './gradlew publish'
                }
            }
        }
        stage('Finish') {
            steps {
                sh 'echo "FINISH STAGE"'
            }
        }
    }
    post {
        always {
            archiveArtifacts artifacts: '**/*.jar', fingerprint: true
            junit allowEmptyResults: true, testResults: '**/test-results/*.xml'
        }
        success {
            echo 'Success'
        }
        failure {
            echo 'This will run only if failed'
        }
        unstable {
            echo 'This will run only if the run was marked as unstable'
        }
        changed {
            echo 'This will run only if the state of the Pipeline has changed'
            echo 'For example, if the Pipeline was previously failing but is now successful'
        }
    }
}