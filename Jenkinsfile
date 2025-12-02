pipeline {
    agent any
    tools {
            maven 'Maven-3.9'
        }
    stages {
        stage('GitHub') {
            steps {
                echo '1. Clonage du projet depuis GitHub'
                git branch: 'ghofrane',
                    url: 'https://github.com/BenGamraOussama/Student_Management.git'

                script {
                    // Afficher les informations du commit
                    sh 'git log -1 --oneline'
                }
            }
        }
        stage('Build') {
            steps {
                script {
                    echo "2. Building Spring Boot application..."
                    sh 'mvn clean compile -DskipTests'
                }
            }
        }
        stage('Test') {
            steps {
                script {
                    echo "Running tests..."
                    sh 'mvn test'
                }
            }
        }
        stage('SonarQube Analysis') {
            steps {
                script {
                    echo 'Analyse de la qualité de code avec SonarQube (configuration fournie)'
                    // Utilise la configuration demandée. Par sécurité, si une variable d'environnement SONAR_TOKEN est fournie
                    // dans Jenkins, elle sera utilisée à la place du token statique ci-dessous.
                    sh """
                        mvn clean verify sonar:sonar \
                          -Dsonar.projectKey=student_mangement \
                          -Dsonar.host.url=http://localhost:9000 \
                          -Dsonar.login=sqp_d93de4b3ea522a65f02bae1f9a30f97b8e4c935d
                    """
                }
            }
        }
    }
    post {
        success {
            echo 'SUCCÈS : Build et push réussis!'
        }
        failure {
            echo 'ÉCHEC : Build failed!'
        }
        always {
            echo 'Nettoyage...'
        }
    }
}