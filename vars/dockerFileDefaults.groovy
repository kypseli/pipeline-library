// vars/dockerFileDefaults.groovy
def call(body) {
    def repoName
    def tag
    def pushBranch
    def enableLifecyclePolicy = true
    
    tokens = "${env.JOB_NAME}".tokenize('/')
    repo = tokens[tokens.size()-2]
    tag = tokens[tokens.size()-1]
    
    def label = "aws-cli-${UUID.randomUUID().toString()}"
    def podYaml = libraryResource 'podtemplates/awsCli.yml'
    podTemplate(name: 'aws-cli', label: label, yaml: podYaml) {
      node(label) {
        checkout scm
        def d = [repo: repo, tag: tag, pushBranch: false, enableLifecyclePolicy: true]
        def props = readProperties defaults: d, file: 'dockerBuildPublish.properties'
        repoName = props['repo']
        tag = props['tag']
        pushBranch = props['pushBranch']
        echo "push non master branch: $pushBranch"
        enableLifecyclePolicy = props['enableLifecyclePolicy']
        echo "enableLifecyclePolicy: $enableLifecyclePolicy"
        //stash name: 'everything', includes: '**'
        stage 'Check Repository'
        container('aws-cli') {
          def errorMsg
          /*
          def lifecyclePolicy
          if(enableLifecyclePolicy.toBoolean()) {
            lifecyclePolicy = libraryResource 'aws/ecr/lifecycle-policy/tempImagePolicy.json'
          } else {
            lifecyclePolicy = libraryResource 'aws/ecr/lifecycle-policy/defaultPolicy.json'
          }
          sh """aws ecr put-lifecycle-policy --region us-east-1 --repository-name kypseli/${repoName} --lifecycle-policy-text '$lifecyclePolicy'"""
          */
          try {
            errorMsg = sh(returnStdout: true, script: "aws ecr create-repository --region us-east-1 --repository-name kypseli/${repoName} | tr -d '\n'")
          } catch(e) {
            //error other than for RepositoryAlreadyExistsException
            if(!errorMsg.contains("RepositoryAlreadyExistsException")) {
              throw e
            }
          }
        }
      }
    }
    
    if(env.BRANCH_NAME=="master" || pushBranch) {
      stage 'Image Build and Push'
      dockerBuildPush("${repoName}", "${tag}",'./') {
        //unstash 'everything'
        checkout scm
      }
    }
}

            def lifecyclePolicy = libraryResource 'aws/ecr/lifecycle-policy/tempImagePolicy.json'
