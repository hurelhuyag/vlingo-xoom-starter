package io.vlingo.xoomstarter.template.steps;

import io.vlingo.xoomstarter.template.TemplateGenerationContext;
import io.vlingo.xoomstarter.template.TemplateGenerationException;

import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;

import static io.vlingo.xoomstarter.template.steps.PropertiesKeys.TARGET_FOLDER;

public class StatusHandler {

    private static final String FAILURE_MESSAGE = "vlingo/xoom template generation failed.";
    private static final String SUCCESS_MESSAGE = "vlingo/xoom template has been successfully generated under %s.";
    private static final List<StatusHandler> HANDLERS =
            Arrays.asList(StatusHandler.successHandling(), StatusHandler.failureHandling());

    private final Predicate<Integer> predicate;
    private final Consumer<TemplateGenerationContext> handler;

    private StatusHandler(Predicate<Integer> predicate, Consumer<TemplateGenerationContext> handler) {
        this.predicate = predicate;
        this.handler = handler;
    }

    public static StatusHandler forStatus(final Integer status) {
        return HANDLERS.stream().filter(handler -> handler.canHandle(status)).findFirst().get();
    }

    public boolean canHandle(final int status) {
        return predicate.test(status);
    }

    public void handle(final TemplateGenerationContext context) {
        handler.accept(context);
    }

    private static StatusHandler successHandling() {
        return new StatusHandler(
                status -> status == 0,
                context -> {
                    System.out.println(String.format(SUCCESS_MESSAGE, context.propertyOf(TARGET_FOLDER)));
                });
    }

    private static StatusHandler failureHandling() {
        return new StatusHandler(
                status -> status != 0,
                context -> {
                    throw new TemplateGenerationException(FAILURE_MESSAGE);
                });
    }

}
