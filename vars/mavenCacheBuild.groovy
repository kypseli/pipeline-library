def call(String name) {
  def label = "mvn-cache"
  def podYaml = libraryResource 'podtemplates/kubeDeploy.yml'
  def pvcYaml = libraryResource 'podtemplates/kubeDeploy.yml'
  podTemplate(name: 'kubectl', label: label, namespace: 'jenkins-agents',  yaml: podYaml) {
    node(label) {
      container("kubectl") {
        try {
          sh("sed -i.bak 's#REPLACE_NAME#${name}#' ${pvcYaml}")
          sh "kubectl --namespace=jenkins-agents apply -f ${pvcYaml}"
        } catch(error) {
          echo 'boom'
        }
      }
    }
  }
}
