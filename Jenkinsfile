pipeline {
    agent any

    environment {
        DOCKERHUB_USERNAME = 'dhivyadev'
        DOCKERHUB_CREDENTIALS = 'dockerhub-credentials'
    }

    triggers {
        githubPush()
    }

    stages {

        stage('Setup Tools') {
            steps {
                sh 'which npm || (apt-get update -qq && apt-get install -y nodejs npm -qq)'
                sh 'test -f /usr/local/bin/docker-compose || (curl -sL https://github.com/docker/compose/releases/download/v2.24.0/docker-compose-linux-aarch64 -o /usr/local/bin/docker-compose && chmod +x /usr/local/bin/docker-compose); docker-compose --version'
            }
        }
        stage('Checkout') {
            steps {
                echo 'Checking out code...'
                checkout scm
            }
        }

        stage('API Build') {
            steps {
                echo 'Building all backend services...'
                sh '''
                    export JAVA_HOME=$(dirname $(dirname $(readlink -f $(which java))))
                    ./mvnw clean package -DskipTests
                '''
            }
        }

        stage('UI Build') {
            steps {
                echo 'Building Angular frontend...'
                sh '''
                    cd frontend
                    npm ci --legacy-peer-deps
                    npx ng build --configuration=production
                '''
            }
        }

        stage('Docker Build - API') {
            steps {
                echo 'Building backend Docker images...'
                sh '''
                    docker-compose build config-server eureka-server api-gateway \
                        auth-service customer-service account-service \
                        document-service notification-service
                '''
            }
        }

        stage('Docker Build - UI') {
            steps {
                echo 'Building frontend Docker image...'
                sh 'docker-compose build frontend'
            }
        }

        stage('Push API Images') {
            steps {
                withCredentials([usernamePassword(
                    credentialsId: 'dockerhub-credentials',
                    usernameVariable: 'DOCKER_USER',
                    passwordVariable: 'DOCKER_PASS'
                )]) {
                    sh '''
                        echo "$DOCKER_PASS" | docker login -u "$DOCKER_USER" --password-stdin
                        docker-compose push config-server eureka-server api-gateway \
                            auth-service customer-service account-service \
                            document-service notification-service
                        docker logout
                    '''
                }
            }
        }

        stage('Push UI Image') {
            steps {
                withCredentials([usernamePassword(
                    credentialsId: 'dockerhub-credentials',
                    usernameVariable: 'DOCKER_USER',
                    passwordVariable: 'DOCKER_PASS'
                )]) {
                    sh '''
                        echo "$DOCKER_PASS" | docker login -u "$DOCKER_USER" --password-stdin
                        docker-compose push frontend
                        docker logout
                    '''
                }
            }
        }

        stage('Deploy') {
            steps {
                echo 'Deploying services...'
                sh '''
                    docker ps -aq --filter name=securebank | grep -v $(docker inspect --format="{{.Id}}" securebank-jenkins 2>/dev/null || echo NONE) | xargs -r docker rm -f
                    docker-compose up -d zookeeper kafka oracle-db config-server eureka-server api-gateway auth-service customer-service account-service document-service notification-service frontend
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
