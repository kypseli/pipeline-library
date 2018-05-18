// vars/dockerBuildPush.groovy
def call(String name, String tag, String target = ".", Closure body) {
    def label = "kubectl-${UUID.randomUUID().toString()}"
    podTemplate(name: 'kubectl', label: label, namespace: 'kaniko', yaml: """
     kind: Pod
     metadata:
       name: kubectl
     spec:
       serviceAccountName: jenkins
       containers:
       - name: kubectl
         image: lachlanevenson/k8s-kubectl:v1.9.3
         command:
         - cat
         tty: true
     """
    ) {
      node(label) {
        container('kubectl') {
          body()
          sh 'kubectl get pods'
        }
      }
    }
}
