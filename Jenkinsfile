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
            stage('Build Docker Image') {
                         steps {
                              sh 'docker build -t $LOCAL_IMAGE .'
                          }
              }

              stage('Login Docker Hub') {
                  steps {
                      echo '5. Authentification à Docker Hub'
                      script {
                      }
                  }
              }

                      stage('Push Image Docker Hub') {
                          steps {
                              echo '6. Push vers Docker Hub'
                              sh 'docker push ghofraneidriss/student-app:latest'
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