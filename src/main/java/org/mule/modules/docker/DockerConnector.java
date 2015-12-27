package org.mule.modules.docker;

import com.github.dockerjava.api.DockerException;
import com.github.dockerjava.api.command.CreateContainerResponse;
import com.github.dockerjava.api.model.*;
import com.github.dockerjava.core.async.ResultCallbackTemplate;
import org.mule.api.annotations.Config;
import org.mule.api.annotations.Connector;
import org.mule.api.annotations.Processor;
import org.mule.api.annotations.Source;
import org.mule.api.annotations.display.Placement;
import org.mule.api.annotations.param.Default;
import org.mule.api.annotations.param.Optional;
import org.mule.api.callback.SourceCallback;
import org.mule.modules.docker.config.DockerConfig;

import java.util.List;

@Connector(name = "docker", friendlyName = "Docker") public class DockerConnector {

    @Config DockerConfig config;

    @Processor
    public Info getDockerInfo() throws DockerException, InterruptedException {
        return config.getDockerClient().infoCmd().exec();
    }

    @Processor
    public List<Container> getContainerList() throws DockerException, InterruptedException {
        return config.getDockerClient().listContainersCmd().exec();
    }

    @Processor
    public List<Image> getImageList() throws DockerException, InterruptedException {
        return config.getDockerClient().listImagesCmd().exec();
    }

    @Processor
    public CreateContainerResponse createContainer(String imageName, @Placement(group = "Stream Type") @Default("false") boolean attachStandardError,
            @Placement(group = "Stream Type") @Default("false") boolean attachStandardInput, @Placement(group = "Stream Type") @Default("false") boolean attachStandardOut,
            @Optional List<String> commands, @Optional List<Bind> bindList, @Optional String containerIdFile, @Optional  Integer cpuPeriod, @Optional List<ExposedPort> exposedPorts) {

        CreateContainerResponse exec = config.getDockerClient().createContainerCmd(imageName)
                .withAttachStderr(attachStandardError)
                .withAttachStdin(attachStandardInput)
                .withAttachStdout(attachStandardOut)
                .withCmd(commands.toArray(new String[commands.size()]))
                .withBinds(bindList.toArray(new Bind[bindList.size()]))
                .withContainerIDFile(containerIdFile)
                .withCpuPeriod(cpuPeriod)
                .withExposedPorts(exposedPorts.toArray(new ExposedPort[exposedPorts.size()]))
                .exec();

        return exec;

    }

    @Source
    public void retrieveContainerStatistics(SourceCallback sourceCallback, String containerId) {
        config.getDockerClient().statsCmd().withContainerId(containerId).exec(new SourceCallBack<Statistics>(sourceCallback));
    }

    @Source
    public void retrieveContainerLogs(SourceCallback sourceCallback, String containerId, @Default("false") boolean showTimeStamp, @Default("false") boolean followStream,
            @Placement(group = "Stream Type") @Default("false") boolean standardOut, @Default("false") @Placement(group = "Stream Type") boolean standardError, @Default("1") int showSince) {

        config.getDockerClient().logContainerCmd(containerId)
                .withTimestamps(showTimeStamp)
                .withFollowStream(followStream)
                .withStdOut(standardOut)
                .withStdErr(standardError)
                .withSince(showSince)
                .withTailAll()
                .exec(new SourceCallBack<Frame>(sourceCallback));
    }

    @Processor
    public void startContainer(String containerId) {
        config.getDockerClient().startContainerCmd(containerId).exec();
    }

    private class SourceCallBack<T> extends ResultCallbackTemplate<SourceCallBack<T>, T> {

        private final SourceCallback callback;

        SourceCallBack(SourceCallback sourceCallback) {
            this.callback = sourceCallback;
        }

        @Override
        public void onNext(T t) {
            if (t != null) {
                try {
                    this.callback.process(t);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public DockerConfig getConfig() {
        return config;
    }

    public void setConfig(DockerConfig config) {
        this.config = config;
    }

}