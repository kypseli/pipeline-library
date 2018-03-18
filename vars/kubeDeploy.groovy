#!/usr/bin/groovy

def call(String imageName, String imageTag, String environment) {
  node('kubernetes') {
    container("kubectl") {
      sh "kubectl set image deployment/${name}-${environment}-deployment ${name}=${name-app}:${imageTag}"
    }
  }
}
