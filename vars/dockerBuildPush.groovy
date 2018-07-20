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
         image: gcr.io/kaniko-project/executor:debug-v0.2.0
         command:
         - /busybox/cat
         tty: true
         volumeMounts:
           - name: jenkins-docker-cfg
             mountPath: /root
       volumes:
       - name: jenkins-docker-cfg
         projected:
           sources:
           - secret:
               name: regcred
               items:
                 - key: .dockercfg
                   path: .docker/config.json
       serviceAccountName: kaniko
"""
    ) {
      node(label) {
        container(name: 'kaniko', shell: '/busybox/sh') {
          body()
          sh """#!/busybox/sh
            /kaniko/executor -f ${dockerFile} -c ./ -d ${name}:${tag}
          """
        }
      }
    }
}
