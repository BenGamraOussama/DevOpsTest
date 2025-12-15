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
                 DOCKER_REPOSITORY = 'student-app'
                 DOCKER_TAG = 'latest'
                 DOCKER_IMAGE = "${DOCKER_USERNAME}/${DOCKER_REPOSITORY}:${DOCKER_TAG}"
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

              stage('Build Docker Image') {
                  steps {
                      echo 'Building Docker image'
                      sh 'docker build -t $DOCKER_IMAGE .'
                  }
              }

              stage('Login Docker Hub') {
                  steps {
                      echo 'Login to Docker Hub'
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
                      echo 'Pushing image to Docker Hub'
                      sh 'docker push $DOCKER_IMAGE'
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