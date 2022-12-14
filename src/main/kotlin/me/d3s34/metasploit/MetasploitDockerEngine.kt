package me.d3s34.metasploit

import kotlinx.coroutines.CoroutineScope
import me.d3s34.docker.ContainerAttachClient
import me.d3s34.docker.DockerClientManager
import kotlin.coroutines.CoroutineContext

class MetasploitDockerEngine(
    override val coroutineContext: CoroutineContext
): CoroutineScope {

    private val dockerClientManager = DockerClientManager()

    private val dockerClient = dockerClientManager.dockerClient

    fun tryGetShell(MetasploitRequest: MetasploitRequest): ContainerAttachClient {
        val containerId = dockerClientManager
            .createMetasploitContainer(MetasploitRequest)!!

        return ContainerAttachClient(containerId, dockerClient, this)
    }

}
