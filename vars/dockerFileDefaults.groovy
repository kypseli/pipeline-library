// vars/dockerFileDefaults.groovy
def call(body) {
    def repoName
    def tag
    def pushBranch
    
    tokens = "${env.JOB_NAME}".tokenize('/')
    repo = tokens[tokens.size()-2]
    tag = tokens[tokens.size()-1]
    
    def label = "aws-cli-${UUID.randomUUID().toString()}"
    def podYaml = libraryResource 'podtemplates/awsCli.yml'
    podTemplate(name: 'aws-cli', label: label, yaml: podYaml) {
      node(label) {
        checkout scm
        def d = [repo: repo, tag: tag, pushBranch: false]
        def props = readProperties defaults: d, file: 'dockerBuildPublish.properties'
        repoName = props['repo']
        tag = props['tag']
        pushBranch = props['pushBranch']
        echo "push non master branch: $pushBranch"
        stash name: 'everything', includes: '**'
        stage 'Check Repository'
        container('aws-cli') {
          sh "aws ecr create-repository --repository-name kypselie/${repoName}"
        }
      }
    }
    
    if(env.BRANCH_NAME=="master" || pushBranch) {
      stage 'Image Build and Push'
      dockerBuildPush("${repoName}", "${tag}",'./') {
        unstash 'everything'
      }
    }
}
