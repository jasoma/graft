package com.github.jasoma.graft.convert;

/**
 * A container capable of holding or creating {@link PropertyConverter} instances. Implementations
 * must be thread safe.
 */
public interface ConverterRegistry {

    /**
     * Add a new converter instance to the registry.
     *
     * @param converter the converter to add.
     */
    void add(PropertyConverter converter);

    /**
     * Get a converter instance from the registry.
     *
     * @param converterType the type of converter required.
     * @return an instance of the converter type.
     */
    <T extends PropertyConverter> T get(Class<T> converterType);

}
