package org.kypseli.util

class DockerBuildPush implements Serializable {

  def steps
  
  def DockerBuildPush(steps) {
    this.steps = steps
  }
  
  def buildPush(name, tag) {
    steps.podTemplate(name: 'kaniko', label: label, yaml: """
     kind: Pod
     metadata:
       name: kaniko
     spec:
       containers:
       - name: kaniko
         image: csanchez/kaniko:jenkins # we need a patched version of kaniko for now
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
      steps.node(label) {
        steps.container('kaniko') {
          steps.sh "/kaniko/executor -c . --destination=beedemo/${name}:${tag}"
        }
      }
    }
    
  }

}
