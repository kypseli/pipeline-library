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
         image: gcr.io/kaniko-project/executor:debug
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
        body()
        container('kaniko') {
          sh "#!/busybox/sh \n" +
              'cp target/* /kaniko'
          sh "#!/busybox/sh \n" +
              'ls -la /kaniko'
          sh "#!/busybox/sh \n" +
              "/kaniko/executor -f Dockerfile -c /kaniko -d beedemo/${name}:${tag}"
        }
      }
    }
}
