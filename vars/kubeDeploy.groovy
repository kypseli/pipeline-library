#!/usr/bin/groovy

def call(String imageName, String imageRepo, String imageTag, String environment) {
  node('kubernetes') {
    container("kubectl") {
      sh "kubectl set image deployment/${name}-${environment}-deployment ${name}-app=${imageRepo}/${name}:${imageTag}"
    }
  }
}
