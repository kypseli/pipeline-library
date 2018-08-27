// vars/dockerBuildPush.groovy
def call(String name, String tag, String target = ".", String dockerFile="Dockerfile", String dockerRepo="946759952272.dkr.ecr.us-east-1.amazonaws.com/kypseli/", Closure body) {
    def label = "kaniko-${UUID.randomUUID().toString()}"
    def podYaml = libraryResource 'podtemplates/dockerBuildPusy.yml'
    podTemplate(name: 'kaniko', label: label, namespace: 'kaniko', yaml: podYaml) {
      node(label) {
        container(name: 'kaniko', shell: '/busybox/sh') {
          body()
          withEnv(['PATH+EXTRA=/busybox:/kaniko']) {
            sh """#!/busybox/sh
              executor -f ${pwd()}/${dockerFile} -c ${pwd()} -d ${dockerRepo}${name}:${tag} -d ${dockerRepo}${name}:latest
            """
          }
        }
      }
    }
}
