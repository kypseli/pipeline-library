//must be called from an agent that supports Docker
def call(org, name, tag, dir, pushCredId) {
    sh "docker build -t $org/$name:$tag $dir"
    withDockerRegistry(registry: [credentialsId: "$pushCredId"]) { 
        sh "docker push $org/$name:$tag"
    }
    publishEvent generic("$org/$name") 
}
