pipeline {
    agent any
    stages {
        stage('Cloner GitHub') {
            steps {
                echo '1. Clonage du projet depuis GitHub'
                git branch: 'oussama',
                    url: 'https://github.com/BenGamraOussama/Student_Management.git'
            }
        }
        stage('Builder Application') {
            steps {
                echo '2. Construction de l application (sans les tests)'
                sh 'mvn clean package -DskipTests'
            }
        }
        stage('Construire Image Docker') {
            steps {
                echo '3. Construction image Docker'
                sh 'docker build -t oussamabengamra/student-app:latest .'
            }
        }
        stage('Tester Image') {
            steps {
                echo '4. Test de l image Docker'
                sh '''
                    docker run --rm \
                    -e SPRING_PROFILES_ACTIVE=ci \
                    oussamabengamra/student-app:latest
                '''

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
            sh 'docker logout || true'  // || true pour éviter échec si pas loggé
        }
    }
}
