pipeline {
    agent any
    environment {
        // Nom d'image par défaut si aucun registre n'est fourni
        LOCAL_IMAGE = 'oussamabengamra/student-app:latest'
        // Image distante demandée par l'utilisateur pour le déploiement
        DEPLOY_IMAGE_DEFAULT = 'oussamabengamra/student-app:latest'
        // Espace de nom et nom d'image pour le build/push
        DOCKER_NAMESPACE = 'oussamabengamra'
        DOCKER_IMAGE_NAME = 'student-app'
        // ID Jenkins Credentials par défaut pour Docker Hub (Username with password)
        // Si vous avez créé un credentials avec l'ID "dockerhub-creds", la pipeline
        // pourra l'utiliser automatiquement (voir stage "Docker Login (optionnel)")
        DOCKER_CREDENTIALS_ID = 'docker-hub-token'
        // Registre Docker (laisser vide pour Docker Hub)
        DOCKER_REGISTRY = ''
        // Indicateurs runtime pour gérer le logout en fin de pipeline
        DOCKER_LOGGED_IN = ''
        DOCKER_REGISTRY_EFFECTIVE = ''
    }
    triggers {
            githubPush()
        }
    stages {
        stage('GitHub') {
            steps {
                echo '1. Clonage du projet depuis GitHub'
                git branch: 'oussama',
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
        // Connexion optionnelle à Docker Hub (ou autre registre) avant les pulls/deploys
        stage('Docker Login (optionnel)') {
            when {
                anyOf {
                    expression { return env.DOCKER_CREDENTIALS_ID?.trim() }
                    allOf {
                        expression { return env.DOCKERHUB_USER?.trim() }
                        expression { return env.DOCKERHUB_TOKEN?.trim() }
                    }
                }
            }
            steps {
                script {
                    echo '6. Connexion au registre Docker...'
                    def defaultHub = 'https://index.docker.io/v1/'
                    def reg = env.DOCKER_REGISTRY?.trim()
                    if (!reg || reg == 'docker.io' || reg == defaultHub) {
                        reg = ''
                    }

                    if (env.DOCKER_CREDENTIALS_ID?.trim()) {
                        withCredentials([usernamePassword(credentialsId: env.DOCKER_CREDENTIALS_ID, usernameVariable: 'REG_USER', passwordVariable: 'REG_PASS')]) {
                            sh """
                              echo "${REG_PASS}" | docker login ${reg} -u "${REG_USER}" --password-stdin
                            """
                        }
                    } else {
                        sh """
                          echo "${DOCKERHUB_TOKEN}" | docker login ${reg} -u "${DOCKERHUB_USER}" --password-stdin
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
        stage('Docker Build') {
            steps {
                script {
                    echo '6. Construction de l\'image Docker locale...'
                    // Construire l'image locale à partir du Dockerfile
                     sh 'docker build -t oussamabengamra/student-app:latest .'
                }
            }
        }
        // Ce stage pousse l'image vers le registre si des identifiants sont fournis
        stage('Docker Push') {
            when {
                anyOf {
                    expression { return env.DOCKER_CREDENTIALS_ID?.trim() }
                    allOf {
                        expression { return env.DOCKERHUB_USER?.trim() }
                        expression { return env.DOCKERHUB_TOKEN?.trim() }
                    }
                }
            }
            steps {
                script {
                    echo '7. Push de l\'image vers le registre...'
                    def latestTag = "${env.DOCKER_NAMESPACE}/${env.DOCKER_IMAGE_NAME}:latest"
                    sh "docker push ${latestTag}"
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
            // Nettoyage optionnel des images orphelines pour garder l'agent propre
            script {
                sh 'docker image prune -f || true'
                // Se déconnecter du registre si on s'est connecté pendant le build
                if (env.DOCKER_LOGGED_IN?.trim()) {
                    def reg = env.DOCKER_REGISTRY_EFFECTIVE?.trim()
                    sh "docker logout ${reg ?: ''} || true"
                }
            }
        }
    }
}