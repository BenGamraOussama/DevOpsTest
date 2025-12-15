pipeline {
    agent any
    stages {
        stage('GitHub') {
            steps {
                echo '1. Clonage du projet depuis GitHub'
                git branch: 'amena',
                    url: 'https://github.com/BenGamraOussama/Student_Management.git'

                script {
                    // Afficher les informations du commit
                    sh 'git log -1 --oneline'
                }
            }
        }

             environment {
                    // Nom d'image par défaut si aucun registre n'est fourni
                    LOCAL_IMAGE_SONAR = 'sonarqube:latest'
                    LOCAL_IMAGE_SPRING = 'spring-app:latest'
                    // Image distante Docker Hub
                    DEPLOY_IMAGE_SONAR = 'amena12/images:sonarqube'
                    DEPLOY_IMAGE_SPRING = 'amena12/images:spring'
                    // Espace de nom et nom d'image pour le build/push
                    DOCKER_NAMESPACE = 'amena12'
                    DOCKER_IMAGE_NAME_SONAR = 'images:sonarqube'
                    DOCKER_IMAGE_NAME_SPRING = 'images:spring'
                    // Jenkins Docker Hub credentials ID
                    DOCKER_CREDENTIALS_ID = 'docker-hub-token'
                    // Registre Docker (laisser vide pour Docker Hub)
                    DOCKER_REGISTRY = ''
                    DOCKER_LOGGED_IN = ''
                    DOCKER_REGISTRY_EFFECTIVE = ''
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
                    echo "Running tests..."
                    sh 'mvn test'
                }
            }
        }
    }
      stage('SonarQube Analysis') {
                steps {
                    script {
                        echo 'Analyse de la qualité de code avec SonarQube (configuration fournie)'
                        // Utilise la configuration demandée. Par sécurité, si une variable d'environnement SONAR_TOKEN est fournie
                        // dans Jenkins, elle sera utilisée à la place du token statique ci-dessous.
                        sh """
                            mvn clean verify sonar:sonar \
                              -Dsonar.projectKey=Student-Management \
                              -Dsonar.projectName='Student-Management' \
                              -Dsonar.host.url=http://localhost:9000 \
                              -Dsonar.token=${env.SONAR_TOKEN ?: 'adaa36c3f039654b3679b526b51b09c0aafff88bdf03fee91bdbb8fcf150d0eb'}
                        """
                    }
                }
            }
                stage('Docker Login (optionnel)') {
                        when {
                            anyOf {
                                expression { return env.DOCKER_CREDENTIALS_ID?.trim() }
                            }
                        }
                        steps {
                            script {
                                echo '4. Connexion au registre Docker...'
                                def reg = env.DOCKER_REGISTRY?.trim()
                                if (!reg || reg == 'docker.io') {
                                    reg = ''
                                }
                                withCredentials([usernamePassword(credentialsId: env.DOCKER_CREDENTIALS_ID, usernameVariable: 'REG_USER', passwordVariable: 'REG_PASS')]) {
                                    sh """
                                      echo "${REG_PASS}" | docker login ${reg} -u "${REG_USER}" --password-stdin
                                    """
                                }
                                env.DOCKER_LOGGED_IN = 'true'
                                env.DOCKER_REGISTRY_EFFECTIVE = reg
                            }
                        }
                    }

                    stage('Jar Packaging') {
                        steps {
                            script {
                                echo "5. Packaging du fichier JAR..."
                                sh 'mvn clean package -DskipTests'
                            }
                            archiveArtifacts artifacts: 'target/*.jar', fingerprint: true
                        }
                    }

                    stage('Docker Build - SonarQube') {
                        steps {
                            script {
                                echo '6. Construction de l\'image Docker SonarQube...'
                                sh "docker build -t ${LOCAL_IMAGE_SONAR} ./sonarqube"
                            }
                        }
                    }

                    stage('Docker Push - SonarQube') {
                        when {
                            expression { return env.DOCKER_LOGGED_IN?.trim() }
                        }
                        steps {
                            script {
                                echo '7. Push de l\'image SonarQube vers Docker Hub...'
                                sh "docker tag ${LOCAL_IMAGE_SONAR} ${DEPLOY_IMAGE_SONAR}"
                                sh "docker push ${DEPLOY_IMAGE_SONAR}"
                            }
                        }
                    }

                    stage('Docker Build - Spring App') {
                        steps {
                            script {
                                echo '8. Construction de l\'image Docker Spring App...'
                                sh "docker build -t ${LOCAL_IMAGE_SPRING} ./spring-app"
                            }
                        }
                    }

                    stage('Docker Push - Spring App') {
                        when {
                            expression { return env.DOCKER_LOGGED_IN?.trim() }
                        }
                        steps {
                            script {
                                echo '9. Push de l\'image Spring App vers Docker Hub...'
                                sh "docker tag ${LOCAL_IMAGE_SPRING} ${DEPLOY_IMAGE_SPRING}"
                                sh "docker push ${DEPLOY_IMAGE_SPRING}"
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