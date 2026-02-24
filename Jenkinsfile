pipeline {
    agent any

    stages {
        stage('Checkout') {
            steps {
                echo 'Checking out code...'
                checkout scm
            }
        }

        stage('Build') {
            steps {
                echo 'Building all services...'
                sh '''
                    export JAVA_HOME=$(dirname $(dirname $(readlink -f $(which java))))
                    ./mvnw clean package -DskipTests
                '''
            }
        }

        stage('Docker Build') {
            steps {
                echo 'Building Docker images...'
                sh 'docker-compose build'
            }
        }

        
    stage('Push to DockerHub') {
        steps {
            withCredentials([usernamePassword(credentialsId: 'dockerhub-credentials', usernameVariable: 'DOCKER_USER', passwordVariable: 'DOCKER_PASS')]) {
                sh 'echo $DOCKER_PASS | docker login -u $DOCKER_USER --password-stdin && docker-compose push && docker logout'
            }
        }
    }
    stage('Deploy') {
            steps {
                echo 'Deploying services...'
                sh '''
                    docker-compose stop $(docker-compose config --services | grep -v jenkins) || true
                    docker-compose rm -f $(docker-compose config --services | grep -v jenkins) || true
                    docker-compose up -d $(docker-compose config --services | grep -v jenkins)
                '''
            }
        }
    }

    post {
        success {
            echo 'Pipeline completed successfully!'
        }
        failure {
            echo 'Pipeline failed!'
        }
    }
}
