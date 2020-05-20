package io.vlingo.xoom.starter.task.docker.steps;

import io.vlingo.xoom.starter.task.Property;
import io.vlingo.xoom.starter.task.TaskExecutionContext;
import io.vlingo.xoom.starter.task.docker.DockerCommandException;
import io.vlingo.xoom.starter.task.option.OptionValue;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.nio.file.Paths;
import java.util.Arrays;

import static io.vlingo.xoom.starter.task.option.OptionName.CURRENT_DIRECTORY;

public class DockerSettingsLoadStepTest {

    @Test
    public void testDockerSettingsLoad() {
        final String propertiesAbsolutePath =
                Paths.get(System.getProperty("user.dir"), "src", "test", "resources").toString();

        final OptionValue currentDirectory =
                OptionValue.with(CURRENT_DIRECTORY, propertiesAbsolutePath);

        final TaskExecutionContext context =
                TaskExecutionContext.withOptions(Arrays.asList(currentDirectory));

        new DockerSettingsLoadStep().process(context);

        Assertions.assertEquals("xoom-app", context.propertyOf(Property.DOCKER_IMAGE));
        Assertions.assertEquals("vlingo/xoom-app", context.propertyOf(Property.DOCKER_REPOSITORY));
    }

    @Test
    public void testMissingDockerSettings() {
        final String propertiesAbsolutePath =
                Paths.get(System.getProperty("user.dir")).toString();

        final OptionValue currentDirectory =
                OptionValue.with(CURRENT_DIRECTORY, propertiesAbsolutePath);

        final TaskExecutionContext context =
                TaskExecutionContext.withOptions(Arrays.asList(currentDirectory));

        Assertions.assertThrows(DockerCommandException.class, () -> {
            new DockerSettingsLoadStep().process(context);
        });
    }
}
