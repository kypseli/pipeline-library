// vars/kaniko.groovy
def call(String name, String tag, String target = ".", String dockerFile="Dockerfile", String containerRegistry="gcr.io/core-workshop", Closure body) {
    def label = "kaniko"
    def podYaml = libraryResource 'podtemplates/kaniko.yml'
    podTemplate(name: 'kaniko', label: label, yaml: podYaml) {
      node(label) {
        container(name: 'kaniko', shell: '/busybox/sh') {
          body()
          withEnv(['PATH+EXTRA=/busybox:/kaniko']) {
            sh """#!/busybox/sh
              executor --cache=true -f ${pwd()}/${dockerFile} -c ${pwd()} -d ${containerRegistry}${name}:${tag} -d ${containerRegistry}${name}:latest
            """
          }
        }
      }
    }
}
