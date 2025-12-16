pipeline {
    agent any
    environment {
        // Docker
        LOCAL_IMAGE = 'oussamabengamra/student-app:latest'
        DOCKER_NAMESPACE = 'oussamabengamra'
        DOCKER_IMAGE_NAME = 'student-app'
        DOCKER_CREDENTIALS_ID = 'docker-hub-token'
        DOCKER_REGISTRY = ''

        // Kubernetes
        KUBECONFIG = '/var/lib/jenkins/.kube/config'
        K8S_NAMESPACE = 'devops'
    }
    triggers {
        githubPush()
    }
    stages {
        stage('GitHub Checkout') {
            steps {
                echo '1. Clonage du projet depuis GitHub'
                git branch: 'oussama', url: 'https://github.com/BenGamraOussama/Student_Management.git'
                sh 'git log -1 --oneline'
            }
        }

        stage('Build & Package') {
            steps {
                echo '2. Build et package de l\'application Spring Boot...'
                sh 'mvn clean package -DskipTests'
                archiveArtifacts artifacts: 'target/*.jar', fingerprint: true
            }
        }

        stage('Test') {
            steps {
                echo '3. Exécution des tests...'
                sh 'mvn test'
            }
        }

        stage('Docker Login (optionnel)') {
            when {
                expression { return env.DOCKER_CREDENTIALS_ID?.trim() }
            }
            steps {
                script {
                    echo '4. Connexion au registre Docker...'
                    def reg = env.DOCKER_REGISTRY?.trim()
                    if (!reg || reg == 'docker.io') reg = ''

                    withCredentials([usernamePassword(credentialsId: env.DOCKER_CREDENTIALS_ID, usernameVariable: 'REG_USER', passwordVariable: 'REG_PASS')]) {
                        sh """
                            echo "${REG_PASS}" | docker login ${reg} -u "${REG_USER}" --password-stdin
                        """
                    }
                }
            }
        }

        stage('Docker Build') {
            steps {
                echo '5. Construction de l\'image Docker locale (tag: latest)...'
                sh "docker build -t ${DOCKER_NAMESPACE}/${DOCKER_IMAGE_NAME}:latest ."
            }
        }

        stage('Docker Push') {
            when {
                expression { return env.DOCKER_CREDENTIALS_ID?.trim() }
            }
            steps {
                echo '6. Push de l\'image Docker vers le registre...'
                sh "docker push ${DOCKER_NAMESPACE}/${DOCKER_IMAGE_NAME}:latest"
            }
        }

        stage('Deploy to Kubernetes') {
            steps {
                echo '7. Déploiement sur Kubernetes...'
                sh """
                    export KUBECONFIG=${KUBECONFIG}
                    kubectl apply -f spring-deployment.yaml -n ${K8S_NAMESPACE}
                    kubectl rollout status deployment/spring-app -n ${K8S_NAMESPACE} --timeout=120s
                    kubectl get pods -n ${K8S_NAMESPACE}
                """
            }
        }
    }

    post {
        success {
            echo 'Build, Docker et déploiement réussis!'
        }
        failure {
            echo 'Échec du pipeline!'
        }
        always {
            echo 'Nettoyage des ressources Docker...'
            sh 'docker image prune -f || true'

            script {
                if (env.DOCKER_CREDENTIALS_ID?.trim()) {
                    def reg = env.DOCKER_REGISTRY?.trim()
                    if (!reg || reg == 'docker.io') reg = ''
                    sh "docker logout ${reg} || true"
                }
            }
        }
    }
}