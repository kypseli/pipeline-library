// vars/dockerBuildPush.groovy
def call(String name, String tag, String target = ".", Closure body) {
    def label = "kubectl-${UUID.randomUUID().toString()}"
    podTemplate(name: 'kubectl', label: label, namespace: 'kaniko', yaml: """
     kind: Pod
     metadata:
       name: kaniko
     spec:
       containers:
       - name: kubectl
         image: lachlanevenson/k8s-kubectl:v1.9.3
         command:
         - cat
         tty: true
         volumeMounts:
           - name: podinfo
             mountPath: /etc/podinfo
             readOnly: false
       - name: kaniko
         image: gcr.io/kaniko-project/executor:debug
         command:
         - /busybox/sh
         tty: true
         volumeMounts:
           - name: jenkins-docker-cfg
             mountPath: /root/.docker
       volumes:
         - name: podinfo
           downwardAPI:
             items:
               - path: "name"
                 fieldRef:
                   fieldPath: metadata.name
         - name: jenkins-docker-cfg
           secret:
             secretName: jenkins-docker-cfg
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
          sh "kubectl exec ${podName} -c kaniko -- ls -la /"
          sh "kubectl exec ${podName} -c kaniko -- /kaniko/executor -c / -d ${name}:${tag}"
        }
      }
    }
}
