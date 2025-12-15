pipeline {
    agent any
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
           environment {
             DOCKER_USERNAME = 'ghofraneidriss'
             DOCKER_REPOSITORY = 'images'
             DOCKER_TAG = 'latest'
             DOCKER_IMAGE = 'ghofraneidriss/images:latest'
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
                    echo "Running tests..."
                    sh 'mvn -v'
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
                    echo 'Analyse SonarQube en cours...'

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
