package org.mule.modules.docker.config;

import com.github.dockerjava.api.DockerClient;
import com.github.dockerjava.core.DockerClientConfig;
import com.github.dockerjava.core.DockerClientImpl;
import com.github.dockerjava.jaxrs.DockerCmdExecFactoryImpl;
import org.mule.api.ConnectionException;
import org.mule.api.annotations.*;
import org.mule.api.annotations.components.ConnectionManagement;
import org.mule.api.annotations.display.FriendlyName;
import org.mule.api.annotations.display.Password;
import org.mule.api.annotations.display.Path;
import org.mule.api.annotations.param.ConnectionKey;

@ConnectionManagement(configElementName = "full-config", friendlyName = "Full Docker Config") public class FullConfiguration implements DockerConfig {

    DockerClient docker;

    @Connect
    @TestConnectivity
    public void connect(@FriendlyName("URI") String URI, @ConnectionKey String username, @Password String password, @Path String certPath, @Path String configPath, String email, String version) throws ConnectionException {

        DockerClientConfig build = DockerClientConfig.createDefaultConfigBuilder()
                .withUsername(username)
                .withPassword(password)
                .withDockerCertPath(certPath)
                .withDockerCfgPath(configPath)
                .withPassword(password)
                .withEmail(email)
                .withVersion(version)
                .withUri(URI)
                .build();
        DockerCmdExecFactoryImpl dockerCmdExecFactory = new DockerCmdExecFactoryImpl();
        DockerClientImpl instance = DockerClientImpl.getInstance(build);
        docker = instance.withDockerCmdExecFactory(dockerCmdExecFactory);
        docker.pingCmd().exec();

    }

    @Disconnect
    public void disconnect() {
        docker = null;
    }

    @ValidateConnection
    public boolean isConnected() {
        return docker != null;
    }

    @ConnectionIdentifier
    public String connectionId() {
        return "001";
    }

    @Override
    public DockerClient getDockerClient() {
        return docker;
    }

}
