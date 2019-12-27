pipeline {
    agent any

    stages {

/*
        stage('Build') {
          parallel{
            stage('Build with JDK8'){

              agent {
                docker {
                  image 'maven:3-jdk-8-alpine'
                  args '-v $HOME/.m2:/root/.m2'
                }
              }

              steps {
                sh 'mvn clean compile'
              }

            }



            stage('Build with JDK9'){

              agent {
                docker {
                  image 'maven:3-jdk-9-slim'
                  args '-v $HOME/.m2:/root/.m2'
                }
              }

              steps {
                sh 'mvn clean compile'
              }

            }

            stage('Build with JDK11'){

              agent {
                docker {
                  image 'maven:3-jdk-11-slim'
                  args '-v $HOME/.m2:/root/.m2'
                }
              }

              steps {
                sh 'mvn clean compile'
              }

            }


          }
        }
        */

/*
	    stage('Test') {
        parallel {

          stage('Test JDK8'){

            agent {
              docker {
                image 'maven:3-jdk-8-alpine'
                args '-v $HOME/.m2:/root/.m2'

              }
            }

            steps {
              sh 'mvn test -PJava8'

            }

          }

	        stage('Test JDK9'){

            agent {
              docker {
                image 'maven:3-jdk-9-slim'
                args '-v $HOME/.m2:/root/.m2'
              }
            }

            steps {
              sh 'mvn test -PJava9'

            }

          }

            stage('Test JDK11'){

            agent {
              docker {
                image 'maven:3-jdk-11-slim'
                args '-v $HOME/.m2:/root/.m2'
              }
            }

            steps {
              sh 'mvn test -PJava11'

            }

          }


	       }
		}*/




        stage ('Deploy') {
            when {
                anyOf {
                       branch 'master'
                       branch 'develop'
                       branch 'mergeJ9'
                      }
                }

            steps {
               withCredentials([string(
                   credentialsId: 'artifact-signing-key-password',
                   variable: 'SIGN_KEY')]) {
                        configFileProvider(
                            [configFile(fileId: '10647dc3-5621-463b-a290-85290f0ad119', variable: 'MAVEN_SETTINGS')]) {
                            sh 'mvn -s $MAVEN_SETTINGS deploy -P deploy -DskipTests'
                        }
                  }
            }
        }

    }
}
