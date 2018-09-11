def call(imageName, imageRepo, imageTag, deployYmlPath, environment='staging') {
    def label = "kubectl"
    def podYaml = libraryResource 'podtemplates/kubeDeploy.yml'
    podTemplate(name: 'kubectl', label: label, namespace: 'cb-deploy',  yaml: podYaml) {
      node(label) {
        container("kubectl") {
          sh("sed -i.bak 's#REPLACE_IMAGE_TAG#946759952272.dkr.ecr.us-east-1.amazonaws.com/${imageRepo}/${imageName}:${imageTag}#' ./${deployYmlPath}")
          sh "kubectl --namespace=cb-deploy apply -f ${deployYmlPath}"
        }
      }
    }
}
