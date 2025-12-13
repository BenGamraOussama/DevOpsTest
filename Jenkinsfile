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
    }

    // add an optional parameter to override credentials id at build time
    parameters {
        string(name: 'DOCKERHUB_CREDENTIALS_ID', defaultValue: 'docker-hub-credentials', description: 'Jenkins credentials ID for Docker Hub (optional)')
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

        /* stage('SonarQube Analysis') {
            steps {
                script {
                    echo '5. Analyse de la qualité de code avec SonarQube (configuration fournie)'
                    sh """
                        mvn clean verify sonar:sonar \
                          -Dsonar.projectKey=Student-Management \
                          -Dsonar.projectName='Student-Management' \
                          -Dsonar.host.url=http://localhost:9000 \
                          -Dsonar.token=${env.SONAR_TOKEN ?: 'sqp_c12214b751ee7a42bd312c0c8a018761ccc617a1'}
                    """
                }
            }
        } */
        stage('Build Docker Image') {
                   steps {
                        sh 'docker build -t $LOCAL_IMAGE .'
                    }
        }

        stage('Login Docker Hub') {
            steps {
                echo '5. Authentification à Docker Hub'
                script {
                  def credId = params.DOCKERHUB_CREDENTIALS_ID ?: 'docker-hub-credentials'
                  try {
                    withCredentials([usernamePassword(credentialsId: credId, usernameVariable: 'DOCKER_USER', passwordVariable: 'DOCKER_PASS')]) {
                      sh "echo \$DOCKER_PASS | docker login -u \$DOCKER_USER --password-stdin"
                      env.DOCKER_PUSH_ENABLED = 'true'
                    }
                  } catch (err) {
                    echo "Skipping Docker login: credentials '${credId}' not found or login failed. Error: ${err}"
                    env.DOCKER_PUSH_ENABLED = 'false'
                  }
                }
            }
        }

         stage('Push Image Docker Hub') {
            when {
                expression { return env.DOCKER_PUSH_ENABLED == 'true' }
            }
            steps {
                echo '6. Push vers Docker Hub'
                sh 'docker push oussamabengamra/student-app:latest'
            }
            post {
                unsuccessful {
                    echo 'Push failed'
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