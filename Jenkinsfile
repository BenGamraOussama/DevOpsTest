pipeline {
    agent any

    tools {
        jdk 'JAVA_HOME'    // Doit correspondre au nom configuré dans "Global Tool Configuration"
        maven 'M2_HOME'    // Idem pour Maven
    }

    stages {
        stage('Checkout') {
            steps {
                echo "Récupération du code source..."
                git branch: 'oussama',
                    url: 'https://github.com/BenGamraOussema/Student_Management.git'
            }
        }

        stage('Compile') {
            steps {
                sh 'mvn -B clean compile'
            }
        }

        // Nouveau stage Build (package sans exécuter les tests)
        stage('Build') {
            steps {
                echo "Construction du JAR (sans tests)..."
                sh 'mvn -B -Dmaven.test.skip=true package'
            }
            post {
                success {
                    archiveArtifacts artifacts: '**/target/*.jar', fingerprint: true, allowEmptyArchive: false
                }
            }
        }

        // Nouveau stage Test (exécution des tests unitaires + rapports)
        stage('Test') {
            steps {
                echo "Exécution des tests unitaires..."
                sh 'mvn -B test'
            }
            post {
                always {
                    // Publication des rapports Surefire (JUnit)
                    junit testResults: '**/target/surefire-reports/TEST-*.xml', allowEmptyResults: true
                }
            }
        }

        // Optionnel : vous pouvez ajouter un stage "Package" qui refait un build complet (avec tests)
        // si vous voulez être sûr d’avoir un artefact final propre
        stage('Package') {
            steps {
                echo "Packaging final (avec tests)..."
                sh 'mvn -B package'
            }
            post {
                success {
                    archiveArtifacts artifacts: '**/target/*.jar', fingerprint: true
                }
            }
        }
    }

    post {
        always {
            echo "Nettoyage du workspace (optionnel)"
            // cleanWs()   // décommentez si vous voulez nettoyer à chaque fois
        }
        success {
            echo "Pipeline terminé avec succès !"
        }
        failure {
            echo "Échec du pipeline."
        }
    }
}