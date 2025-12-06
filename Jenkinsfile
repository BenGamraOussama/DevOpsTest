pipeline {
    agent any
    environment {
        // Nom d'image par défaut si aucun registre n'est fourni
        LOCAL_IMAGE = 'student-management:local'
        // Image distante demandée par l'utilisateur pour le déploiement
        DEPLOY_IMAGE_DEFAULT = 'oussamabengamra/student-app:latest'
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
        stage('Docker Build') {
            steps {
                script {
                    echo '6. Sélection de l\'image Docker pour le déploiement...'
                    // Ne plus construire d'image locale: on déploie l'image publique spécifiée
                    env.BUILT_IMAGE = env.DEPLOY_IMAGE_DEFAULT
                    // Tirer la dernière version de l'image avant déploiement (meilleure fraîcheur)
                    sh "docker pull ${env.BUILT_IMAGE} || true"
                }
            }
        }
        // Ce stage est optionnel: il ne s'exécute que si des identifiants de registre sont fournis.
        // On aligne la condition avec le stage "Docker Build" en vérifiant des valeurs non vides (trim).
        stage('Docker Push (désactivé)') {
            // Désactivé volontairement: nous utilisons l'image publique "oussamabengamra/student-app:latest"
            // et nous ne construisons/poussons plus d'image locale dans cette pipeline.
            when { expression { return false } }
            steps {
                echo 'Push désactivé: aucune image locale à publier.'
            }
        }
        stage('Deploy avec Docker Compose') {
            steps {
                script {
                    echo '8. Déploiement/rafraîchissement du service via docker compose...'
                    // Utiliser docker compose pour (re)déployer l\'application Spring Boot
                    // Le service s\'appelle "spring-app" dans docker-compose.yaml
                    // Toujours tenter un pull de l'image distante avant le déploiement
                    sh "docker pull ${env.BUILT_IMAGE} || true"
                    // Déployer l'image construite via une variable d'environnement DEPLOY_IMAGE
                    sh "DEPLOY_IMAGE=${env.BUILT_IMAGE} docker compose up -d spring-app"
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
            // Nettoyage optionnel des images orphelines pour garder l'agent propre
            script {
                sh 'docker image prune -f || true'
            }
        }
    }
}