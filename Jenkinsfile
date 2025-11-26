pipeline {
    agent any

    tools {
        // Remplacez les valeurs ci‑dessous par les labels configurés dans Jenkins
        jdk 'JAVA_HOME'
        maven 'M2_HOME'
    }

    stages {
        stage('Checkout') {
            steps {
                // checkout du repo et de la branche 'oussama'
                git branch: 'oussama', url: 'https://github.com/BenGamraOussama/Student_Management.git'
            }
        }

        stage('Compile') {
            steps {
                // option -B pour build non interactif, adapter si nécessaire
                sh 'mvn -B clean compile'
            }
        }

        stage('Build') {
            steps {
                // Construction du package (JAR/WAR)
                sh 'mvn -B clean package -DskipTests'
            }
            post {
                success {
                    // Optionnel : archiver l'artefact généré
                    archiveArtifacts artifacts: 'target/*.jar', fingerprint: true
                }
            }
        }

        stage('Test') {
            steps {
                // Exécution des tests
                sh 'mvn -B test'
            }
            post {
                always {
                    // Publication des rapports de test même en cas d'échec
                    junit 'target/surefire-reports/*.xml'
                }
            }
        }
    }

    post {
        always {
            // Nettoyage ou actions post-build
            echo 'Pipeline terminé - Statut: ${currentBuild.result}'
        }
        success {
            echo 'Build et tests réussis!'
        }
        failure {
            echo 'Build ou tests échoués!'
        }
    }
}