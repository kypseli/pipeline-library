// vars/dockerBuildPush.groovy
def call(String name, String tag, String target = ".", Closure body) {
    def label = "kaniko-${UUID.randomUUID().toString()}"
    podTemplate(name: 'kaniko', label: label, namespace: 'kaniko', yaml: """
     kind: Pod
     metadata:
       name: kaniko
     spec:
       containers:
       - name: kaniko
         image: beedemo/kaniko:jenkins-k8s-3 # we need a patched version of kaniko for now
         imagePullPolicy: Always
         command:
         - cat
         tty: true
         volumeMounts:
           - name: jenkins-docker-cfg
             mountPath: /root/.docker
       volumes:
         - name: jenkins-docker-cfg
           secret:
             secretName: jenkins-docker-cfg
             items:
             - key: .dockerconfigjson
               path: config.json
     """
    ) {
      node(label) {
        body()
        container('kaniko') {
          sh "cd ${target} && ls -la"
          sh "cd ${target} && /kaniko/executor -c . --destination=beedemo/${name}:${tag}"
        }
      }
    }
}
