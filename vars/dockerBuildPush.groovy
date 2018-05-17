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
         image: beedemo/kaniko:jenkins-k8s-5
         imagePullPolicy: Always
         command:
         - /busybox/sh
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
        container('kaniko') {
          body()
          sh 'cp target/* /kaniko'
          sh 'ls -la /kaniko'
          sh "/kaniko/executor -f Dockerfile -c /kaniko -d beedemo/${name}:${tag}"
        }
      }
    }
}
