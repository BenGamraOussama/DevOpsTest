pipeline {
    agent any

    triggers {
        githubPush()
    }

    // Add optional parameters to pass a token/username at runtime
    parameters {
        string(name: 'DOCKERHUB_USERNAME', defaultValue: '', description: 'Optional Docker Hub username (defaults to DOCKER_NAMESPACE)')
        password(name: 'DOCKERHUB_TOKEN', defaultValue: '', description: 'Optional Docker Hub token (preferred over stored credentials)')
    }

    environment {
        // Paramètres Docker par défaut
        DOCKER_NAMESPACE = 'oussamabengamra'
        DOCKER_IMAGE_NAME = 'student-app'
        // Image locale par défaut
        LOCAL_IMAGE = 'oussamabengamra/student-app:latest'

        // flag used to decide whether to push the image
        DOCKER_PUSH_ENABLED = 'false'
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

        // Move Login before the Docker build so pulls of base images can succeed
        stage('Login Docker Hub') {
            steps {
                echo '5. Authentification à Docker Hub'
                script {
                    try {
                        if (params.DOCKERHUB_TOKEN?.trim()) {
                            // Prefer token provided as pipeline parameter
                            sh(script: "echo '${params.DOCKERHUB_TOKEN}' | docker login -u '${params.DOCKERHUB_USERNAME?.trim() ? params.DOCKERHUB_USERNAME : env.DOCKER_NAMESPACE}' --password-stdin")
                        } else {
                            // Fall back to Jenkins-stored credential id 'docker-hub-token'
                            withCredentials([string(credentialsId: 'docker-hub-token', variable: 'DOCKER_TOKEN')]) {
                                sh '''
                                    echo "$DOCKER_TOKEN" | docker login -u "${env.DOCKER_NAMESPACE}" --password-stdin
                                '''
                            }
                        }
                        env.DOCKER_PUSH_ENABLED = 'true'
                        echo 'Docker login successful.'
                    } catch (err) {
                        echo "Skipping Docker login: ${err.getMessage()}"
                        env.DOCKER_PUSH_ENABLED = 'false'
                    }
                }
            }
        }

        stage('Build Docker Image') {
            steps {
                // build after login so base image pulls use authenticated access when available
                sh 'docker build -t $LOCAL_IMAGE .'
            }
        }

        stage('Push Image Docker Hub') {
            when {
                expression { env.DOCKER_PUSH_ENABLED == 'true' }
            }
            steps {
                echo '6. Push vers Docker Hub'
                sh 'docker push $LOCAL_IMAGE'
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