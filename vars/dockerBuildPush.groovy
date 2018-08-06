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
         image: gcr.io/kaniko-project/executor:debug
         command:
         - /busybox/cat
         tty: true
         volumeMounts:
           - name: jenkins-docker-cfg
             mountPath: /root/.docker
       volumes:
       - name: jenkins-docker-cfg
         projected:
           sources:
           - secret:
               name: jenkins-docker-cfg
               items:
                 - key: .dockerconfigjson
                   path: config.json
       serviceAccountName: kaniko
"""
    ) {
      node(label) {
        container(name: 'kaniko', shell: '/busybox/sh') {
          body()
          withEnv(['PATH+EXTRA=/busybox']) {
            sh """#!/busybox/sh
              /kaniko/executor -f ${pwd()}/${dockerFile} -c ${pwd()} -d ${name}:${tag}
            """
          }
        }
      }
    }
}
