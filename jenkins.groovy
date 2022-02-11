pipeline{
    agent any
    tools{
        maven 'Maven3.8.4'
    }
    stages{
        stage(Shell_Script)
        {
            steps 
            {
                sh '''echo "Hello World"
                        echo $JOB_URL
                        echo $JOB_NAME
                        echo $GIT_BRANCH'''
            }
            post
            {
                always
                {
                    echo "shell script result will be shown "
                }
                success
                {
                    echo "shell script success"
                }
                failure
                {
                    echo "shell script failure"
                }
            }
        }
        stage(Git_Clone)
        {
            steps 
            {
                checkout([$class: 'GitSCM', branches: [[name: '*/master']], extensions: [], userRemoteConfigs: [[url: 'https://github.com/Karunasaran/simple-java-maven-app.git']]])
            }
            post
            {
                always
                {
                    echo "git clone result will shown "
                }
                success
                {
                    echo "clone success"
                }
                failure
                {
                    echo "clone failure"
                }
            }
        }
        stage(Packaging)
        {
            steps 
            {
                sh 'mvn -B -DskipTests clean package'
            }
            post
            {
                always
                {
                    echo "Packaging result will shown "
                }
                success
                {
                    echo "package success"
                    archive "target/*.jar"
                }
                failure
                {
                    echo "package failure"
                }
            }
        }
        stage(Testing)
        {
            steps 
            {
                sh 'mvn test'
            }
            post
            {
                always
                {
                    echo "test Packaging result will shown "
                    junit "target/surefire-reports/*.xml"
                }
                success
                {
                    echo "test success"
                }
                failure
                {
                    echo "test failure"
                }
            }
        }
        
        stage ("sonar scanning") {
            steps {
                script { 
                    def scannerHome = tool name: 'mysonarqube';
                    withSonarQubeEnv("mysonarqube") {
                        sh "${tool("mysonarqube")}/bin/sonar-scanner \
                        -Dsonar.projectKey=simple-java-maven-app \
                        -Dsonar.sources=. \
                        -Dsonar.java.binaries=target \
                        -Dsonar.host.url=http://3.145.137.81:9000 \
                        -Dsonar.login=a58f813a1d8749942e21107c586d971322a89cb1"
                    }
                }
            }
    }
}
}
