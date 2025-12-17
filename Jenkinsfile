pipeline {
    agent any
    environment {
        DOCKER_USERNAME = 'ghofraneidriss'
        DOCKER_REPOSITORY = 'images'
        DOCKER_TAG = 'latest'
        DOCKER_IMAGE = "${DOCKER_USERNAME}/${DOCKER_REPOSITORY}:${DOCKER_TAG}"

        // Kubernetes
        KUBECONFIG = '/var/lib/jenkins/.kube/config'

    }

    stages {
        stage('GitHub') {
            steps {
                echo '1. Clonage du projet depuis GitHub'
                git branch: 'ghofrane',
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
                    sh 'mvn -v'
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
                    echo '5. Analyse SonarQube en cours...'
                    withSonarQubeEnv('MySonarQube') {
                        sh """
                            mvn clean verify sonar:sonar \
                            -Dsonar.projectKey=student_mangement \
                            -Dsonar.projectName=student_mangement \
                            -Dsonar.sources=src/main/java \
                            -Dsonar.tests=src/test/java \
                            -Dsonar.java.binaries=target/classes \
                            -Dsonar.coverage.jacoco.xmlReportPaths=target/site/jacoco/jacoco.xml
                        """
                    }
                }
            }
        }

        stage('Build Docker Image') {
            steps {
                echo '6. Building Docker image'
                sh 'docker build -t $DOCKER_IMAGE .'
            }
        }

        stage('Login Docker Hub') {
            steps {
                echo '7. Login to Docker Hub'
                withCredentials([usernamePassword(
                    credentialsId: 'dockerhub-creds',
                    usernameVariable: 'DOCKER_USER',
                    passwordVariable: 'DOCKER_PASS'
                )]) {
                    sh 'echo $DOCKER_PASS | docker login -u $DOCKER_USER --password-stdin'
                }
            }
        }

        stage('Push Docker Image') {
            steps {
                echo '8. Pushing image to Docker Hub'
                sh 'docker push $DOCKER_IMAGE'
            }
        }

        stage('Deploy to Kubernetes') {
            steps {
                echo '9. Déploiement sur Kubernetes...'
                sh """
                    export KUBECONFIG=${KUBECONFIG}
                    kubectl apply -f spring-deployment.yaml
                    kubectl rollout status deployment/spring-app --timeout=120s
                    kubectl get pods
                """
            }
        }
    }

    post {
        success {
            echo 'Build, Docker et déploiement réussis!'
        }
        failure {
            echo 'ÉCHEC : Build failed!'
        }
        always {
            echo 'Nettoyage...'
        }
    }
}
