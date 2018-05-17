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
         image: gcr.io/kaniko-project/executor
         imagePullPolicy: Always
         command:
         - tail -f /dev/null
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
          sh 'cp target/* /kaniko'
          sh 'ls -la /kaniko'
          sh "/kaniko/executor -f Dockerfile -c /kaniko -d beedemo/${name}:${tag}"
        }
      }
    }
}
