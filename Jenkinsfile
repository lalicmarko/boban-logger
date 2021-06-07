pipeline {
    agent any
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
                sh 'echo World STAGE"'
            }
        }
        stage('Finish') {

            steps {
                sh 'echo "FINISH STAGE"'
            }
        }
    }
}