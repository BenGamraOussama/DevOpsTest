pipeline {
    agent any

    triggers {
        githubPush()
    }

    environment {
        // Docker Hub configuration
        DOCKER_REGISTRY = 'docker.io'
        DOCKER_NAMESPACE = 'oussamabengamra'
        DOCKER_IMAGE_NAME = 'student-app'
        LOCAL_IMAGE = "${DOCKER_NAMESPACE}/${DOCKER_IMAGE_NAME}:latest"

        // Flag to control Docker push
        DOCKER_PUSH_ENABLED = 'true'  // Change to 'false' if you don't want to push by default
    }

    stages {
        stage('GitHub') {
            steps {
                echo '1. Clonage du projet depuis GitHub'
                git branch: 'oussama',
                    url: 'https://github.com/BenGamraOussama/Student_Management.git'

                script {
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

        stage('Build Docker Image') {
            steps {
                script {
                    echo '5. Building Docker image...'
                    sh "docker build -t ${LOCAL_IMAGE} ."
                }
            }
        }

        stage('Push to Docker Hub') {
            when {
                allOf {
                    expression { env.DOCKER_PUSH_ENABLED == 'true' }
                    branch 'main'  // Optionnel : seulement pour la branche main
                }
            }
            steps {
                script {
                    echo '6. Push vers Docker Hub...'
                    withCredentials([usernamePassword(
                        credentialsId: 'docker-hub-token',  // ID des credentials dans Jenkins
                        usernameVariable: 'DOCKER_USER',
                        passwordVariable: 'DOCKER_PASS'
                    )]) {
                        sh """
                            echo "Login to Docker Hub..."
                            docker login -u ${DOCKER_USER} -p ${DOCKER_PASS}

                            echo "Pushing image..."
                            docker push ${LOCAL_IMAGE}

                            echo "Image pushed successfully!"
                        """
                    }
                }
            }
        }
    }

    post {
        success {
            echo 'SUCCÈS : Build et déploiement réussis!'
            echo "Application disponible sur: http://localhost:8080"
        }
        failure {
            echo 'ÉCHEC : Build failed!'
        }
        always {
            echo 'Nettoyage...'
            script {
                // Nettoyage des conteneurs arrêtés
                sh 'docker container prune -f || true'
            }
        }
    }
}