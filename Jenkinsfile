pipeline {
    agent any
    environment {
        // Nom d'image par défaut si aucun registre n'est fourni
        LOCAL_IMAGE = 'student-management:local'
    }
    triggers {
            githubPush()
        }
    stages {
        stage('GitHub') {
            steps {
                echo '1. Clonage du projet depuis GitHub'
                git branch: 'oussama',
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
                    echo "3. Running tests..."
                    sh 'mvn test'
                }
            }
        }
        stage('Jar Packaging') {
                    steps {
                        script {
                            echo "4. Packaging du fichier JAR..."
                            sh 'mvn clean package -DskipTests'
                        }
                        archiveArtifacts artifacts: 'target/*.jar', fingerprint: true
                    }
                }
        stage('SonarQube Analysis') {
            steps {
                script {
                    echo '5. Analyse de la qualité de code avec SonarQube (configuration fournie)'
                    sh """
                        mvn clean verify sonar:sonar \
                          -Dsonar.projectKey=Student-Management \
                          -Dsonar.projectName='Student-Management' \
                          -Dsonar.host.url=http://localhost:9000 \
                          -Dsonar.token=${env.SONAR_TOKEN ?: 'sqp_c12214b751ee7a42bd312c0c8a018761ccc617a1'}
                    """
                }
            }
        }
        stage('Docker Build') {
            steps {
                script {
                    echo '6. Construction de l\'image Docker...'
                    // Si un utilisateur Docker Hub est fourni, on prépare un nom d\'image distant
                    def remoteImage = (env.DOCKERHUB_USER && env.DOCKERHUB_USER.trim()) ? "${env.DOCKERHUB_USER}/student-management" : null
                    if (remoteImage) {
                        sh """
                          docker build -t ${remoteImage}:$BUILD_NUMBER -t ${remoteImage}:latest .
                        """
                        env.BUILT_IMAGE = "${remoteImage}:$BUILD_NUMBER"
                    } else {
                        sh """
                          docker build -t ${LOCAL_IMAGE} .
                        """
                        env.BUILT_IMAGE = LOCAL_IMAGE
                    }
                }
            }
        }
        stage('Docker Push (optionnel)') {
            when {
                expression { return env.DOCKERHUB_USER && env.DOCKERHUB_TOKEN }
            }
            steps {
                script {
                    echo '7. Connexion et push de l\'image vers Docker Hub...'
                    sh """
                      echo '${DOCKERHUB_TOKEN}' | docker login -u '${DOCKERHUB_USER}' --password-stdin
                      docker push ${DOCKERHUB_USER}/student-management:$BUILD_NUMBER
                      docker push ${DOCKERHUB_USER}/student-management:latest
                      docker logout || true
                    """
                }
            }
        }
        stage('Deploy avec Docker Compose') {
            steps {
                script {
                    echo '8. Déploiement/rafraîchissement du service via docker compose...'
                    // Utiliser docker compose pour (re)déployer l\'application Spring Boot
                    // Le service s\'appelle "spring-app" dans docker-compose.yaml
                    sh 'docker compose up -d --build spring-app'
                    sh 'docker ps --format "table {{.ID}}\t{{.Image}}\t{{.Status}}\t{{.Ports}}\t{{.Names}}"'
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