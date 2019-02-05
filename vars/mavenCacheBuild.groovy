def call(String name, String team='default') {
  def label = "mvn-cache"
  def podYaml = libraryResource 'podtemplates/mvnCache.yml'
  podTemplate(name: 'mvn-cache', label: label, namespace: 'jenkins-agents',  yaml: podYaml) {
    node(label) {
      container("maven") {
        sh "mvn dependency:resolve"
      }
    }
  }
}
