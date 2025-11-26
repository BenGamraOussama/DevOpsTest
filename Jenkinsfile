pipeline {
    agent any

    tools {
        jdk 'JAVA_HOME'
        maven 'M2_HOME'
    }

    stages {
        stage('Checkout') {
            steps {
                git branch: 'oussama', url: 'https://github.com/BenGamraOussama/Student_Management.git'
            }
        }

        stage('Compile') {
            steps {
                sh 'mvn -B clean compile'
            }
        }

        stage('Build') {
            steps {
                sh 'mvn -B package -DskipTests'
            }
        }

        stage('Test') {
            steps {
                // Tests unitaires uniquement
                sh 'mvn -B test'
            }
        }
    }

    post {
        success {
            echo "✔ Pipeline terminée avec succès !"
        }
        failure {
            echo "❌ Pipeline échouée !"
        }
    }
}
