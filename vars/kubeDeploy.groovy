def call(imageName, imageRepo, imageTag, environment='staging') {
    def label = "kubectl"
    def podYaml = libraryResource 'podtemplates/kubeDeploy.yml'
    podTemplate(name: 'kubectl', label: label, namespace: 'kaniko',  yaml: podYaml) {
      node(label) {
        container("kubectl") {
          sh "kubectl set image deployment/${name} ${name}-app=${imageRepo}/${name}:${imageTag}"
        }
      }
    }
}
