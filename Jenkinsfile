pipeline {
    agent any
    stages {
        stage('GitHub') {
            steps {
                echo '1. Clonage du projet depuis GitHub'
                git branch: 'amena',
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
    }
      stage('SonarQube Analysis') {
                steps {
                    script {
                        echo 'Analyse de la qualité de code avec SonarQube (configuration fournie)'
                        // Utilise la configuration demandée. Par sécurité, si une variable d'environnement SONAR_TOKEN est fournie
                        // dans Jenkins, elle sera utilisée à la place du token statique ci-dessous.
                        sh """
                            mvn clean verify sonar:sonar \
                              -Dsonar.projectKey=Student-Management \
                              -Dsonar.projectName='Student-Management' \
                              -Dsonar.host.url=http://localhost:9000 \
                              -Dsonar.token=${env.SONAR_TOKEN ?: 'adaa36c3f039654b3679b526b51b09c0aafff88bdf03fee91bdbb8fcf150d0eb'}
                        """
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