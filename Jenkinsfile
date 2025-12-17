pipeline {
    agent any

    environment {
        DOCKER_CREDENTIALS_ID = 'docker-hub-token'
        SPRING_IMAGE = 'spring-app:latest'
        REMOTE_IMAGE = 'amena12/images:spring'
    }

    options {
        timeout(time: 30, unit: 'MINUTES')
        buildDiscarder(logRotator(numToKeepStr: '5'))
    }

    stages {
        // Étape 1: Checkout
        stage('Checkout SCM') {
            steps {
                echo '📦 Checkout du code source'
                checkout([
                    $class: 'GitSCM',
                    branches: [[name: '*/amena']],
                    userRemoteConfigs: [[
                        url: 'https://github.com/BenGamraOussama/Student_Management.git'
                    ]],
                    extensions: [[$class: 'CleanBeforeCheckout']]
                ])

                // Vérification
                sh '''
                    echo "Répertoire: $(pwd)"
                    echo "Contenu:"
                    ls -la
                '''
            }
        }

        // Étape 2: Build Maven
        stage('Build') {
            steps {
                echo '🔨 Compilation du projet'
                sh '''
                    echo "Vérification du POM..."
                    if [ -f "pom.xml" ]; then
                        echo "POM.xml trouvé"
                        mvn clean compile -DskipTests -q
                    else
                        echo "ERREUR: pom.xml non trouvé"
                        exit 1
                    fi
                '''
            }

            post {
                success {
                    echo '✅ Build réussi'
                }
                failure {
                    echo '❌ Build échoué'
                }
            }
        }

        // Étape 3: Tests (correction de "Text" -> "Test")
        stage('Test') {
            steps {
                echo '🧪 Exécution des tests'
                sh 'mvn test -q'
            }

            post {
                always {
                    junit 'target/surefire-reports/*.xml'
                }
            }
        }

        // Étape 4: Docker Login
        stage('Docker Login') {
            steps {
                echo '🔐 Connexion à Docker Hub'
                script {
                    withCredentials([
                        usernamePassword(
                            credentialsId: env.DOCKER_CREDENTIALS_ID,
                            usernameVariable: 'DOCKER_USER',
                            passwordVariable: 'DOCKER_PASS'
                        )
                    ]) {
                        sh '''
                            echo "Tentative de login Docker..."
                            echo "$DOCKER_PASS" | docker login -u "$DOCKER_USER" --password-stdin

                            if [ $? -eq 0 ]; then
                                echo "✅ Login Docker réussi"
                            else
                                echo "❌ Login Docker échoué"
                                exit 1
                            fi
                        '''
                    }
                }
            }
        }

        stage('Docker Build - Spring App') {
            steps {
                echo '🐳 Construction de l\'image Docker'
                script {

                    // Vérifier la structure réelle
                    sh '''
                        echo "Structure du projet:"
                        ls -la
                        echo ""
                        echo "Dockerfile présent?"
                        ls -la Dockerfile || (echo "❌ Dockerfile non trouvé" && exit 1)
                    '''

                    // Construire l'image depuis la racine du projet
                    sh "docker build -t ${env.SPRING_IMAGE} ."

                    // Tag pour Docker Hub
                    sh "docker tag ${env.SPRING_IMAGE} ${env.REMOTE_IMAGE}"

                    // Push vers Docker Hub
                    sh "docker push ${env.REMOTE_IMAGE}"
                }
            }
        }


            post {
                success {
                    echo '✅ Image Docker construite et poussée avec succès'
                    sh '''
                        echo "Images Docker créées:"
                        docker images | grep spring
                    '''
                }
                failure {
                    echo '❌ Échec de la construction Docker'
                    sh 'docker images'
                }
            }
        }
    }

    post {
        always {
            echo '🧹 Nettoyage'
            script {
                // Déconnexion Docker
                sh 'docker logout 2>/dev/null || true'

                // Nettoyage des images locales (optionnel)
                sh """
                    docker rmi ${env.SPRING_IMAGE} 2>/dev/null || true
                    docker rmi ${env.REMOTE_IMAGE} 2>/dev/null || true
                """
            }

            // Archivage des artefacts
            archiveArtifacts artifacts: 'target/*.jar', allowEmptyArchive: true
            archiveArtifacts artifacts: '**/Dockerfile', allowEmptyArchive: true
        }

        success {
            echo '🎉 PIPELINE RÉUSSI!'
            emailext (
                subject: "✅ SUCCESS: Build #${env.BUILD_NUMBER}",
                body: "Le pipeline Jenkins a réussi!\n\nJob: ${env.JOB_NAME}\nBuild: #${env.BUILD_NUMBER}\nURL: ${env.BUILD_URL}",
                to: 'votre-email@example.com'
            )
        }

        failure {
            echo '💥 PIPELINE ÉCHOUÉ'
            emailext (
                subject: "❌ FAILURE: Build #${env.BUILD_NUMBER}",
                body: "Le pipeline Jenkins a échoué!\n\nJob: ${env.JOB_NAME}\nBuild: #${env.BUILD_NUMBER}\nURL: ${env.BUILD_URL}\n\nConsultez les logs pour plus de détails.",
                to: 'votre-email@example.com'
            )
        }

        unstable {
            echo '⚠️ PIPELINE INSTABLE'
        }
    }
}