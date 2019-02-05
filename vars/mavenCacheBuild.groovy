def call(String name, String team='default') {
  def label = "mvn-cache"
  def podYaml = libraryResource 'podtemplates/mvnCache.yml'
  
  try {
    podTemplate(name: 'mvn', label: 'mvn', namespace: 'jenkins-agents', yaml: podYaml) {
      node('mvn') {
        checkout scm
        container("maven") {
          sh "mvn -o package"
        }
      }
    }
  } catch(error) {
    podTemplate(name: 'mvn-cache', label: label, namespace: 'jenkins-agents', idleMinutes: 480, yaml: podYaml) {
      node(label) {
        checkout scm
        container("maven") {
          sh "mvn package"
        }
      }
    }
  }
}
