pipeline {
    agent any

    options { buildDiscarder(logRotator(numToKeepStr: '1')) }

    stages {

    
  
            stage('Stylecheck') {
                steps {
                    catchError(buildResult: 'FAILURE', stageResult: 'FAILURE') {
                        sh "mvn clean checkstyle:check -Dcheckstyle.failOnViolation=true"
                    }
                }
            }


            stage('Licensecheck') {
                steps {
                    catchError(buildResult: 'FAILURE', stageResult: 'FAILURE') {
                        sh "mvn clean license:check-file-header -Dlicence-check.failOnMissingHeader=true"
                    }
                }
            }
        

          stage('Build and Test JDK8'){

            agent {
              docker {
                image 'maven:3-jdk-8-alpine'
                args '-v $HOME/.m2:/root/.m2'
                reuseNode true
              }
            }

            steps {
              sh 'mvn clean test -PJava8'
            }

          }

          stage('Build and Test JDK9'){

            agent {
              docker {
                image 'maven:3-jdk-9-slim'
                args '-v $HOME/.m2:/root/.m2'
                reuseNode true
              }
            }

            steps {
              sh 'mvn clean test -PJava9'
            }
          }

            stage('Build and Test JDK11'){

            agent {
              docker {
                image 'maven:3-jdk-11-slim'
                args '-v $HOME/.m2:/root/.m2'
                reuseNode true
              }
            }

            steps {
              sh 'mvn clean test -PJava11'

            }

          }
        
         


        stage ('Deploy to Maven Central') {
            when {
                anyOf {
                       branch 'master'
                       branch 'develop'
                       branch 'umbrella'
                      }
                }

            steps {
               withCredentials([string(
                   credentialsId: 'artifact-signing-key-password',
                   variable: 'SIGN_KEY')]) {
                        configFileProvider(
                            [configFile(fileId: '10647dc3-5621-463b-a290-85290f0ad119', variable: 'MAVEN_SETTINGS')]) {
                            sh 'mvn -s $MAVEN_SETTINGS clean deploy -P deploy -DskipTests -Dcheckstyle.failOnViolation=true -Dgpg.passphrase=$SIGN_KEY'
                        }
                  }            
            }
        }
    }
}
