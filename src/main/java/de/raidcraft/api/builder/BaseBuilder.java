package de.raidcraft.api.builder;

import lombok.Data;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * A base class that can be implemented to a fluent builder api.
 * The fluent api is used by plugins to create complex objects without the need of text configurations.
 *
 * @param <TResult> type of the result that is produced by this builder
 */
@Data
public abstract class BaseBuilder<TResult> {

    private final TResult result;
    private final List<Consumer<TResult>> consumers = new ArrayList<>();

    public BaseBuilder(TResult result) {
        this.result = result;
    }

    public <TBuilder extends BaseBuilder<TNewResult>, TNewResult> TBuilder withBuilder(Class<TBuilder> builderClass, TNewResult result, Consumer<TBuilder> callback) {
        try {
            TBuilder builder = builderClass.getDeclaredConstructor(result.getClass()).newInstance(result);

            if (callback != null) {
                callback.accept(builder);
            }

            return builder;
        } catch (InstantiationException | IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
            e.printStackTrace();
            return null;
        }
    }

    public <TBuilder extends BaseBuilder<TNewResult>, TNewResult> TBuilder withBuilder(Class<TBuilder> builderClass, TNewResult result) {
        return withBuilder(builderClass, result, null);
    }

    public <TBuilder extends BaseBuilder<TNewResult>, TNewResult> TBuilder build(Consumer<TResult> consumer) {
        consumers.add(consumer);
        return (TBuilder) this;
    }

    public TResult build() {
        getConsumers().forEach(consumer -> consumer.accept(getResult()));
        return getResult();
    }
}
