// vars/dockerBuildPush.groovy
def call(String name, String tag, String target = ".", String dockerFile="Dockerfile", Closure body) {
    def label = "kaniko-${UUID.randomUUID().toString()}"
    podTemplate(name: 'kaniko', label: label, namespace: 'kaniko', yaml: """
     kind: Pod
     metadata:
       name: kaniko
     spec:
       containers:
       - name: kaniko
         image: gcr.io/kaniko-project/executor:debug-v0.3.0
         command:
         - /busybox/cat
         tty: true
         volumeMounts:
           - name: docker-config
             mountPath: /kaniko/.docker
       volumes:
         - name: docker-config
           configMap:
             name: docker-config
       serviceAccountName: kaniko
"""
    ) {
      node(label) {
        container(name: 'kaniko', shell: '/busybox/sh') {
          body()
          withEnv(['PATH+EXTRA=/busybox:/kaniko']) {
            sh """#!/busybox/sh
              executor -f ${pwd()}/${dockerFile} -c ${pwd()} -d ${name}:${tag} -d ${name}:latest
            """
          }
        }
      }
    }
}
