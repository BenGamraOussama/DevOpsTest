pipeline {
    agent any

    triggers {
        githubPush()
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

        stage('Build Docker Image') {
            steps {
                // build after login so base image pulls use authenticated access when available
                sh 'docker build -t $LOCAL_IMAGE .'
            }
        }
stage('Push to Registry') {
            when {
                branch 'main' // ou 'master'
            }
            steps {
                script {
                    echo '8. Push de l\'image vers le registry...'
                    withCredentials([usernamePassword(
                        credentialsId: 'docker-hub-token',
                        usernameVariable: 'oussamabengamra',
                        passwordVariable: 'dckr_pat_iUmaty9ktMQvSXbt8s5XGoQvDPA'
                    )]) {
                        sh """
                            docker login -u ${DOCKER_USER} -p ${DOCKER_PASS} ${DOCKER_REGISTRY}
                            docker push oussamabengamra/student-app:latest
                        """
                    }
                }
            }
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