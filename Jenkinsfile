pipeline {
    agent any

    environment {
        // Docker Credentials
        DOCKER_CREDENTIALS_ID = 'docker-hub-token'

        // Image tags
        SPRING_LOCAL_IMAGE = 'spring-app:latest'
        SPRING_REMOTE_IMAGE = 'amena12/images:spring'

        // SonarQube (décommenté si nécessaire)
        SONAR_LOCAL_IMAGE = 'sonarqube:latest'
        SONAR_REMOTE_IMAGE = 'amena12/images:sonarqube'

        // Maven settings
        MAVEN_OPTS = '-Xmx1024m'
    }

    options {
        timeout(time: 30, unit: 'MINUTES')
        buildDiscarder(logRotator(numToKeepStr: '10'))
        disableConcurrentBuilds()
    }

    stages {
        // Étape 1: Récupération du code
        stage('Checkout SCM') {
            steps {
                echo '📦 Clonage du projet depuis GitHub'
                git branch: 'amena',
                    url: 'https://github.com/BenGamraOussama/Student_Management.git',
                    poll: false

                sh 'echo "Dernier commit: $(git log -1 --oneline)"'
            }

            post {
                success {
                    echo '✅ Checkout réussi'
                }
                failure {
                    echo '❌ Échec du checkout'
                }
            }
        }

        // Étape 2: Build Maven
        stage('Build') {
            steps {
                echo '🔨 Compilation du projet'
                sh 'mvn clean compile -DskipTests'
            }

            post {
                success {
                    echo '✅ Build réussi'
                }
                failure {
                    echo '❌ Échec du build'
                }
            }
        }

        // Étape 3: Tests
        stage('Test') {
            steps {
                echo '🧪 Exécution des tests'
                sh 'mvn test'
            }

            post {
                always {
                    junit 'target/surefire-reports/*.xml'
                }
                success {
                    echo '✅ Tests réussis'
                }
                failure {
                    echo '❌ Tests échoués'
                }
            }
        }

        // Étape optionnelle: Analyse SonarQube (décommenter si nécessaire)
        stage('SonarQube Analysis') {
            when {
                expression { return env.SONAR_TOKEN != null }
            }
            steps {
                echo '📊 Analyse de qualité du code avec SonarQube'
                withCredentials([string(credentialsId: 'sonar-token', variable: 'SONAR_TOKEN')]) {
                    sh """
                        mvn clean verify sonar:sonar \
                        -Dsonar.projectKey=Student-Management \
                        -Dsonar.projectName=Student-Management \
                        -Dsonar.host.url=http://localhost:9000 \
                        -Dsonar.token=${SONAR_TOKEN}
                    """
                }
            }
        }

        // Étape 4: Connexion à Docker Hub
        stage('Docker Login') {
            steps {
                echo '🔐 Connexion à Docker Registry'
                script {
                    withCredentials([
                        usernamePassword(
                            credentialsId: env.DOCKER_CREDENTIALS_ID,
                            usernameVariable: 'DOCKER_USER',
                            passwordVariable: 'DOCKER_PASS'
                        )
                    ]) {
                        sh '''
                            echo "$DOCKER_PASS" | docker login \
                            -u "$DOCKER_USER" \
                            --password-stdin
                        '''
                    }
                }
            }

            post {
                success {
                    echo '✅ Connexion Docker réussie'
                }
                failure {
                    echo '❌ Échec de la connexion Docker'
                    error('Impossible de se connecter à Docker Registry')
                }
            }
        }

        // Étape 5: Build de l'image Spring
        stage('Docker Build - Spring App') {
            steps {
                echo '🐳 Construction de l\'image Docker Spring'
                script {
                    // Construction de l'image
                    sh "docker build -t ${env.SPRING_LOCAL_IMAGE} ./spring-app"

                    // Tag pour le registry distant
                    sh "docker tag ${env.SPRING_LOCAL_IMAGE} ${env.SPRING_REMOTE_IMAGE}"

                    // Push vers Docker Hub
                    sh "docker push ${env.SPRING_REMOTE_IMAGE}"
                }
            }

            post {
                success {
                    echo '✅ Image Spring construite et poussée avec succès'
                }
                failure {
                    echo '❌ Échec de la construction de l\'image Spring'
                }
            }
        }

        // Étape optionnelle: Build SonarQube (décommenter si nécessaire)
        stage('Docker Build - SonarQube') {
            when {
                expression { return false } // Désactivé par défaut
            }
            steps {
                echo '🐳 Construction de l\'image Docker SonarQube'
                script {
                    sh "docker build -t ${env.SONAR_LOCAL_IMAGE} ./sonarqube"
                    sh "docker tag ${env.SONAR_LOCAL_IMAGE} ${env.SONAR_REMOTE_IMAGE}"
                    sh "docker push ${env.SONAR_REMOTE_IMAGE}"
                }
            }
        }
    }

    post {
        always {
            echo '🧹 Nettoyage des ressources'
            script {
                // Déconnexion Docker
                sh 'docker logout || true'

                // Nettoyage des images locales
                sh """
                    docker rmi ${env.SPRING_LOCAL_IMAGE} 2>/dev/null || true
                    docker rmi ${env.SPRING_REMOTE_IMAGE} 2>/dev/null || true
                    docker rmi ${env.SONAR_LOCAL_IMAGE} 2>/dev/null || true
                    docker rmi ${env.SONAR_REMOTE_IMAGE} 2>/dev/null || true
                """

                // Nettoyage Maven
                sh 'mvn clean 2>/dev/null || true'
            }

            // Archivage des résultats
            archiveArtifacts artifacts: 'target/*.jar', allowEmptyArchive: true
        }

        success {
            echo '🎉 PIPELINE TERMINÉ AVEC SUCCÈS'
            script {
                // Ici vous pouvez ajouter des notifications
                // slackSend channel: '#jenkins', message: "Build réussi: ${env.JOB_NAME} #${env.BUILD_NUMBER}"
                // emailext body: 'Build réussi!', subject: "SUCCESS: ${env.JOB_NAME}", to: 'team@example.com'
            }
        }

        failure {
            echo '💥 PIPELINE ÉCHOUÉ'
            script {
                // Notifications d'échec
                // slackSend channel: '#jenkins-alerts', message: "Build échoué: ${env.JOB_NAME} #${env.BUILD_NUMBER}"
                // emailext body: 'Build échoué!', subject: "FAILURE: ${env.JOB_NAME}", to: 'team@example.com'
            }
        }

        unstable {
            echo '⚠️ PIPELINE INSTABLE'
        }

        aborted {
            echo '🛑 PIPELINE INTERROMPU'
        }
    }
}