package org.mule.modules.docker.config;

import com.github.dockerjava.api.DockerClient;

public interface DockerConfig {

    DockerClient getDockerClient();

}
