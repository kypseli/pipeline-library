import org.kypseli.util.DockerBuildPush

def call(name, tag) {
    def dockerBuildPush = new DockerBuildPush(this)
    dockerBuildPush.buildPush(name, tag)
}
