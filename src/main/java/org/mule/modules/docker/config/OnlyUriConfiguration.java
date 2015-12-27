package org.mule.modules.docker.config;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.core.DockerClientConfig;
import com.github.dockerjava.core.DockerClientImpl;
import com.github.dockerjava.jaxrs.DockerCmdExecFactoryImpl;
import org.mule.api.ConnectionException;
import org.mule.api.ConnectionExceptionCode;
import org.mule.api.annotations.*;
import org.mule.api.annotations.components.ConnectionManagement;
import org.mule.api.annotations.display.FriendlyName;
import org.mule.api.annotations.param.ConnectionKey;

@ConnectionManagement(configElementName = "uri-config", friendlyName = "Only URI Configuration") public class OnlyUriConfiguration implements DockerConfig {

    DockerClient docker;

    /**
     * @param URI URI String
     * @throws ConnectionException
     */
    @Connect
    @TestConnectivity
    public void connect(@FriendlyName("URI") @ConnectionKey String URI) throws ConnectionException {
        try {
            DockerClientConfig build = DockerClientConfig.createDefaultConfigBuilder().withUri(URI).build();
            DockerCmdExecFactoryImpl dockerCmdExecFactory = new DockerCmdExecFactoryImpl();
            DockerClientImpl instance = DockerClientImpl.getInstance(build);
            docker = instance.withDockerCmdExecFactory(dockerCmdExecFactory);
            docker.pingCmd().exec();
        } catch (Exception e) {
            throw new ConnectionException(ConnectionExceptionCode.UNKNOWN, e.getMessage(), e.getMessage(), e);
        }
    }

    /**
     * Disconnect
     */
    @Disconnect
    public void disconnect() {
        docker = null;
    }

    /**
     * Are we connected
     */
    @ValidateConnection
    public boolean isConnected() {
        return docker != null;
    }

    /**
     * Are we connected
     */
    @ConnectionIdentifier
    public String connectionId() {
        return "123";
    }

    public DockerClient getDockerClient() {
        return docker;
    }
}