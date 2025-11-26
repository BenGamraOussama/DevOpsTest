pipeline {
    agent any

    tools {
        jdk 'JAVA_HOME'
        maven 'M2_HOME'
    }

    stages {
        stage('Checkout') {
            steps {
                echo 'Récupération du code source...'
                git branch: 'oussama',
                    url: 'https://github.com/BenGamraOussama/Student_Management.git',
                    credentialsId: 'github-token'
            }
        }

        stage('Compile') {
            steps {
                sh 'mvn compile'
            }
        }

        stage('Build') {
            steps {
                sh 'mvn package -DskipTests'
            }
        }

        stage('Test') {
            steps {
                sh 'mvn test'
            }
        }
    }

    post {
        failure {
            echo 'Échec du pipeline.'
        }
        success {
            echo 'Pipeline exécuté avec succès.'
        }
    }
}
