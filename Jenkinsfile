pipeline {
    agent none

    stages {

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

            post {
              always {
                junit 'target/surefire-reports/**/*.xml'
               // stash includes: '**/target/coverage-reports/*', name: 'reports1'

              }
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
            post {
              always {
                junit 'target/surefire-reports/**/*.xml'
             //   stash includes: '**/target/coverage-reports/*', name: 'reports2'


              }
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
            post {
              always {
                junit 'target/surefire-reports/**/*.xml'
               // stash includes: '**/target/coverage-reports/*', name: 'reports3'


              }
            }
          }


	       }
		}




        stage ('Deploy') {
            when {
                anyOf {
                       branch 'java9'
                      }
                }

            steps {
                configFileProvider(
                    [configFile(fileId: '1b5a647e-aeee-4f0b-8870-3f4bb152395f', variable: 'MAVEN_SETTINGS')]) {
                    sh 'mvn -s $MAVEN_SETTINGS deploy -P ci -DskipTests'
                }
            }
        }

    }
}
