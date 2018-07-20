// vars/dockerBuildPush.groovy
def call(String name, String tag, String target = ".", String dockerFile="Dockerfile", Closure body) {
    def label = "kubectl-${UUID.randomUUID().toString()}"
    podTemplate(name: 'kubectl', label: label, namespace: 'kaniko', yaml: """
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
           secret:
             name: regcred
             items:
             - key: .dockerconfigjson
               path: config.json
       serviceAccountName: kaniko
"""
    ) {
      node(label) {
        container('kubectl') {
          dir('context') {
            body()
          }
          sh 'ls -la context'
          def podName = sh returnStdout: true, script: "cat /etc/podinfo/name"
          sh "kubectl cp ./context ${podName}:/ -c kaniko"
            sh "kubectl exec ${podName} -c kaniko -- /kaniko/executor -v debug -f ${dockerFile} -c /${target} -d ${name}:${tag}"
        }
      }
    }
}
