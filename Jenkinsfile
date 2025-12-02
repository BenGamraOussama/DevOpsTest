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