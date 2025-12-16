pipeline {
    agent any

    environment {
        LOCAL_IMAGE_SONAR = 'sonarqube:latest'
        LOCAL_IMAGE_SPRING = 'spring-app:latest'

        DEPLOY_IMAGE_SONAR = 'amena12/images:sonarqube'
        DEPLOY_IMAGE_SPRING = 'amena12/images:spring'

        DOCKER_CREDENTIALS_ID = 'docker-hub-token'
        DOCKER_REGISTRY = ''
        DOCKER_LOGGED_IN = ''
        DOCKER_REGISTRY_EFFECTIVE = ''
    }

    stages {

        stage('GitHub') {
            steps {
                echo '1. Clonage du projet depuis GitHub'
                git branch: 'amena',
                    url: 'https://github.com/BenGamraOussama/Student_Management.git'
                sh 'git log -1 --oneline'
            }
        }

        stage('Build') {
            steps {
                sh 'mvn clean compile -DskipTests'
            }
        }

        stage('Test') {
            steps {
                sh 'mvn test'
            }
        }

        stage('SonarQube Analysis') {
            steps {
                sh """
                  mvn clean verify sonar:sonar \
                  -Dsonar.projectKey=Student-Management \
                  -Dsonar.projectName=Student-Management \
                  -Dsonar.host.url=http://localhost:9000 \
                  -Dsonar.token=${env.SONAR_TOKEN}
                """
            }
        }

        stage('Docker Login') {
            steps {
                withCredentials([usernamePassword(
                    credentialsId: env.DOCKER_CREDENTIALS_ID,
                    usernameVariable: 'REG_USER',
                    passwordVariable: 'REG_PASS'
                )]) {
                    sh 'echo $REG_PASS | docker login -u $REG_USER --password-stdin'
                }
                script {
                    env.DOCKER_LOGGED_IN = 'true'
                }
            }
        }

        stage('Docker Build - SonarQube') {
            steps {
                sh 'docker build -t sonarqube:latest ./sonarqube'
                sh 'docker tag sonarqube:latest amena12/images:sonarqube'
                sh 'docker push amena12/images:sonarqube'
            }
        }

        stage('Docker Build - Spring App') {
            steps {
                sh 'docker build -t spring-app:latest ./spring-app'
                sh 'docker tag spring-app:latest amena12/images:spring'
                sh 'docker push amena12/images:spring'
            }
        }
    }

    post {
        success {
            echo '✅ SUCCESS'
        }
        failure {
            echo '❌ FAILURE'
        }
        always {
            sh 'docker logout || true'
        }
    }
}
