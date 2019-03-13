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





		stage('Deploy'){
		    when {
			    branch 'master'
			}
	        steps {
	            echo 'WHEN - Master Branch!'
                withCredentials([
                    [$class: 'StringBinding', credentialsId: 'nexusUsername', variable: 'MVN_SETTINGS_nexusUsername'],
                    [$class: 'StringBinding', credentialsId: 'nexusPassword', variable: 'MVN_SETTINGS_nexusPassword']
                  ]) {
                    withEnv([
                      'nexusPublic=https://soot-build.cs.uni-paderborn.de/nexus/repository/soot-releases/'
                    ]) {
                      sh 'mvn -s settings.xml clean build'
                    }
                  }
	        }
		}

    }
}
