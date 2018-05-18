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
       volumes:
         - name: podinfo
           downwardAPI:
             items:
               - path: "name"
                 fieldRef:
                   fieldPath: metadata.name
       serviceAccountName: kaniko
"""
    ) {
      node(label) {
        container('kubectl') {
          body()
            def podName = sh returnStdout: true, script: "cat /etc/podinfo/name"
            sh "kubectl describe pods/${podName}"
        }
      }
    }
}
