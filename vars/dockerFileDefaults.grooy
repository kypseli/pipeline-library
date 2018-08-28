// vars/dockerFileDefaults.groovy
def call(body) {
    def repoName
    def tag
    def pushBranch
    
    tokens = "${env.JOB_NAME}".tokenize('/')
    repo = tokens[tokens.size()-2]
    tag = tokens[tokens.size()-1]
    
    node('default-jnlp') {
      checkout scm
      def d = [repo: repo, tag: tag, pushBranch: false]
      def props = readProperties defaults: d, file: 'dockerBuildPublish.properties'
      repoName = props['repo']
      tag = props['tag']
      pushBranch = props['pushBranch']
      echo "push non master branch: $pushBranch"
      stash name: 'everything', includes: '*/*'
    }
    
    if(env.BRANCH_NAME=="master" || pushBranch) {
      dockerBuildPush("${repoName}", "${tag}",'./') {
        unstash 'everything'
      }
    }
}
