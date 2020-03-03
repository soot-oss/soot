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

        stage('Deploy Artifacts to Directories') {
            when {
                anyOf {
                      branch 'master'
                }
            }
            environment {
                POM_VERSION=sh(script: 'mvn -q -Dexec.executable="echo" -Dexec.args=\'${project.version}\' --non-recursive org.codehaus.mojo:exec-maven-plugin:1.6.0:exec', , returnStdout: true).trim()
             }
            parallel {
                stage('Copy Jars'){
                  steps {                  
                      sh 'rm -r /data/out/origin/$BRANCH_NAME/soot/soot-$BRANCH_NAME/build || true'
                      sh 'mkdir -p /data/out/origin/$BRANCH_NAME/soot/soot-$BRANCH_NAME/${POM_VERSION}/build'
                      sh 'cp ./target/*.jar /data/out/origin/$BRANCH_NAME/soot/soot-$BRANCH_NAME/${POM_VERSION}/build'
                  }
                }

                stage('Copy JavaDoc'){
                  steps { 
                    sh 'rm -r /data/out/origin/$BRANCH_NAME/soot/soot-$BRANCH_NAME/${POM_VERSION}/jdoc || true'
                    sh 'mkdir -p /data/out/origin/$BRANCH_NAME/soot/soot-$BRANCH_NAME/${POM_VERSION}/jdoc'
                    sh 'unzip ./target/sootclasses-trunk-javadoc.jar -d /data/out/origin/$BRANCH_NAME/soot/soot-$BRANCH_NAME/${POM_VERSION}/jdoc/'
                  }
                }

                stage('Copy Options'){
                  steps { 
                    sh 'rm -r /data/out/origin/$BRANCH_NAME/soot/soot-$BRANCH_NAME/${POM_VERSION}/options || true'
                    sh 'mkdir -p /data/out/origin/$BRANCH_NAME/soot/soot-$BRANCH_NAME/${POM_VERSION}/options'
                    sh 'cp doc/soot_options.htm /data/out/origin/$BRANCH_NAME/soot/soot-$BRANCH_NAME/${POM_VERSION}/options'
                  }
                }
           }
        }

         stage('Deploy Snapshot Artifacts to Directories') {
            when {
                anyOf {
                      branch 'develop'
                }
            }
            parallel {
              stage('Copy Jars'){
                steps {   
                  sh 'rm -r /data/out/origin/$BRANCH_NAME/soot/soot-$BRANCH_NAME/build || true'
                  sh 'mkdir -p /data/out/origin/$BRANCH_NAME/soot/soot-$BRANCH_NAME/build'
                  sh 'cp ./target/*.jar /data/out/origin/$BRANCH_NAME/soot/soot-$BRANCH_NAME/build'
                }
              }

              stage('Copy JavaDoc'){
                steps { 
                  sh 'rm -r /data/out/origin/$BRANCH_NAME/soot/soot-$BRANCH_NAME/jdoc || true'
                  sh 'mkdir -p /data/out/origin/$BRANCH_NAME/soot/soot-$BRANCH_NAME/jdoc'
                  sh 'unzip ./target/sootclasses-trunk-javadoc.jar -d /data/out/origin/$BRANCH_NAME/soot/soot-$BRANCH_NAME/jdoc/'
                }
              }

              stage('Copy Options'){
                steps { 
                  sh 'rm -r /data/out/origin/$BRANCH_NAME/soot/soot-$BRANCH_NAME/options || true'
                  sh 'mkdir -p /data/out/origin/$BRANCH_NAME/soot/soot-$BRANCH_NAME/options'
                  sh 'cp doc/soot_options.htm /data/out/origin/$BRANCH_NAME/soot/soot-$BRANCH_NAME/options'
                }
              }
           }
        }

    }
}
