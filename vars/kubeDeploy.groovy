def call(imageName, imageRepo, imageTag, environment) {
    def label = "kubectl"
    def podYaml = libraryResource 'podtemplates/dockerBuildPush.yml'
    podTemplate(name: 'kubectl', label: label, namespace: 'agents',  yaml: podYaml) {
    container("kubectl") {
      sh "kubectl set image deployment/${name} ${name}-app=${imageRepo}/${name}:${imageTag}"
    }
}
