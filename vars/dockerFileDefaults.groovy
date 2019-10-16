// vars/dockerFileDefaults.groovy
def call(body) {
    def repoName
    def tag
    def pushBranch
    
    tokens = "${env.JOB_NAME}".tokenize('/')
    repo = tokens[tokens.size()-2]
    tag = tokens[tokens.size()-1]
    
    if(env.BRANCH_NAME=="master" || pushBranch) {
      stage 'Image Build and Push'
      dockerBuildPush("${repoName}", "${tag}",'./') {
        unstash 'everything'
      }
    }
}
