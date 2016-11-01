package com.github.jasoma.graft.convert

import groovy.util.logging.Slf4j

import java.util.concurrent.ConcurrentHashMap

/**
 * Basic implementation of {@link ConverterRegistry} based on a {@link ConcurrentHashMap}.
 */
@Slf4j
class ConcurrentMapRegistry implements ConverterRegistry {

    private def ConcurrentHashMap<Class, PropertyConverter> cache = new ConcurrentHashMap<>()

    /**
     * Create a new empty registry.
     */
    ConcurrentMapRegistry() { }

    /**
     * Creates a registry with an initial set of converters.
     *
     * @param converters a set of pre-constructed converters to add to the registry.
     */
    ConcurrentMapRegistry(PropertyConverter... converters) {
        for (PropertyConverter c : converters) {
            cache[c.class] = c
        }
    }

    @Override
    void add(PropertyConverter converter) {
        def existing = cache[converter.class] = converter
        if (existing) {
            log.warn("Registration of new converter of $converter replaces existing converter $existing of the same type")
        }
    }

    @Override
    def <T extends PropertyConverter> T get(Class<T> converterType) {
        def found = cache[converterType]
        if (found) {
            return found as T
        }
        log.debug("Creating instance of converter type $converterType on demand")
        def created = createConverter(converterType)
        cache[converterType] = created
        return created
    }

    /**
     * Create an instance of a converter if none is found in the internal cache.
     *
     * @param converterType the type of converter to create.
     * @return the created instance.
     */
    protected <T extends PropertyConverter> T createConverter(Class<T> converterType) {
        return converterType.newInstance()
    }
}
