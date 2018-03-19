def call(imageName, imageRepo, imageTag, environment) {
    container("kubectl") {
      sh "kubectl set image deployment/${name} ${name}-app=${imageRepo}/${name}:${imageTag}"
    }
}
