pipeline {

    /**********************************************************************
     * Définition de l'agent et des outils utilisés
     **********************************************************************/
    agent any

    tools {
        // Utilise une installation Maven déclarée dans Jenkins
        maven 'Maven-3.9'
    }

    /**********************************************************************
     * Variables globales du pipeline
     **********************************************************************/
    environment {
        // Token SonarQube stocké dans Jenkins Credentials (bonne pratique)
        SONAR_TOKEN = credentials('sonar-token-id')

        // URL du dépôt Git
        GIT_URL = "https://github.com/BenGamraOussama/Student_Management.git"

        // Branche à cloner
        GIT_BRANCH = "ghofrane"
    }

    stages {

        /******************************************************************
         * Étape 1 : Récupération du code source depuis GitHub
         ******************************************************************/
        stage('Clonage GitHub') {
            steps {
                echo '1. Clonage du projet depuis GitHub...'

                // Checkout Git propre
                checkout([
                    $class: 'GitSCM',
                    branches: [[name: "*/${env.GIT_BRANCH}"]],
                    userRemoteConfigs: [[url: env.GIT_URL]]
                ])

                // Afficher les informations du dernier commit
                script {
                    echo "Dernier commit :"
                    sh "git log -1 --oneline"
                }
            }
        }

        /******************************************************************
         * Étape 2 : Compilation du projet
         ******************************************************************/
        stage('Compilation') {
            steps {
                echo "2. Compilation de l'application Spring Boot..."
                sh "mvn clean compile -DskipTests=true"
            }
        }

        /******************************************************************
         * Étape 3 : Exécution des tests unitaires
         ******************************************************************/
        stage('Tests') {
            steps {
                echo "3. Exécution des tests unitaires..."
                sh "mvn test"
            }
        }

        /******************************************************************
         * Étape 4 : Analyse SonarQube
         ******************************************************************/
        stage('Analyse SonarQube') {
            steps {
                echo "4. Analyse de la qualité du code avec SonarQube..."

                // Exécution de l'analyse Sonar
                sh """
                    mvn verify sonar:sonar \
                        -Dsonar.projectKey=Student-Management \
                        -Dsonar.projectName='Student-Management' \
                        -Dsonar.host.url=http://localhost:9000 \
                        -Dsonar.token=$SONAR_TOKEN
                """
            }
        }

        /******************************************************************
         * Étape 5 : Packaging (bonne pratique CI)
         ******************************************************************/
        stage('Packaging') {
            steps {
                echo "5. Création du package exécutable..."
                sh "mvn package -DskipTests"
            }
        }
    }

    /**********************************************************************
     * Actions exécutées après le pipeline
     **********************************************************************/
    post {
        success {
            echo '✔ SUCCÈS : Build, tests et analyse SonarQube terminés !'
        }
        failure {
            echo '❌ ÉCHEC : Une erreur est survenue lors du pipeline.'
        }
        always {
            echo '🧹 Nettoyage des fichiers temporaires...'
        }
    }
}
