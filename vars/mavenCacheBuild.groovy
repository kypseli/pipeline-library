def call(String name, String team='default') {
  def label = "kubectl-mvn"
  def podYaml = libraryResource 'podtemplates/kubectlMaven.yml'
  def pvcYaml = libraryResource 'podtemplates/mavenCachePVC.yml'
  podTemplate(name: 'kubectl-mvn', label: label, namespace: 'jenkins-agents',  yaml: podYaml) {
    node(label) {
      container("kubectl") {
        try {
          sh("sed -i.bak 's#REPLACE_NAME#${team}#' ${pvcYaml}")
          sh "kubectl --namespace=jenkins-agents apply -f ${pvcYaml}"
        } catch(error) {
          echo 'boom'
        }
      }
      container("maven") {
        sh "mvn dependency:resolve"
      }
    }
  }
}
