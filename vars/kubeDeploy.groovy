#!/usr/bin/groovy

def call(String imageName, String imageRepo, String imageTag, String environment) {
    container("kubectl") {
      sh "kubectl set image deployment/${name} ${name}-app=${imageRepo}/${name}:${imageTag}"
    }
}
