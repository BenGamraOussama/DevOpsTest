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

        stage('SonarQube Analysis') {
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
        }

        // --- STAGES DOCKER AJOUTÉS ---
        stage('Docker Build') {
            steps {
                script {
                    echo "6. Construction de l'image Docker locale..."
                    // Construire l'image locale à partir du Dockerfile et mettre à jour l'image de base
                    sh 'DOCKER_BUILDKIT=1 docker build --pull -t ${LOCAL_IMAGE} .'

                    // Tag unique pour ce build
                    env.BUILT_IMAGE = "${env.DOCKER_NAMESPACE}/${env.DOCKER_IMAGE_NAME}:${env.BUILD_NUMBER}"
                    sh "docker tag ${LOCAL_IMAGE} ${BUILT_IMAGE}"
                }
            }
        }

        // Nettoyage des ressources existantes (conteneur/image) si elles bloquent le déploiement
        stage('Docker Cleanup (existant)') {
            steps {
                script {
                    echo '6.b. Nettoyage des ressources Docker existantes...'
                    // Arrêter et supprimer le conteneur existant si présent
                    sh 'docker ps -a --format "{{.Names}}" | grep -w student-app >/dev/null && docker rm -f student-app || true'

                    // Supprimer l'image :latest locale si elle existe (pour éviter les conflits de digest)
                    sh 'docker images --format "{{.Repository}}:{{.Tag}}" | grep -w ${DOCKER_NAMESPACE}/${DOCKER_IMAGE_NAME}:latest >/dev/null && docker rmi -f ${DOCKER_NAMESPACE}/${DOCKER_IMAGE_NAME}:latest || true'

                    // Nettoyer les images dangling
                    sh 'docker images -f dangling=true -q | xargs -r docker rmi -f || true'
                }
            }
        }

        stage('Deploy') {
            steps {
                script {
                    echo '7. Déploiement avec Docker Compose...'
                    // Essayer de récupérer l'image si poussée sinon continuer avec locale
                    sh 'docker pull ${BUILT_IMAGE} || true'
                    // Forcer la recréation et supprimer les orphelins
                    sh "DEPLOY_IMAGE=${env.BUILT_IMAGE ?: env.LOCAL_IMAGE} docker compose up -d --force-recreate --remove-orphans student-app"
                    sh 'docker ps --format "table {{.ID}}\t{{.Image}}\t{{.Status}}\t{{.Ports}}\t{{.Names}}"'
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