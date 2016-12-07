package com.github.jasoma.graft.cypher

import groovy.transform.PackageScope

/**
 * Wraps a map of values taken from an entity and translates between the parameter names assigned each property
 * in a cypher query.
 */
@PackageScope
class ParameterTranslationMap implements Map<String, Object> {

    @Delegate private Map<String, Object> wrapped
    private Map<String, String> keyMapping

    /**
     * Constructor.
     *
     * @param wrapped the map of values.
     * @param keyMapping the mapping from parameter name to key in the wrapped map.
     */
    ParameterTranslationMap(Map<String, Object> wrapped, Map<String, String> keyMapping) {
        this.wrapped = wrapped
        this.keyMapping = keyMapping
    }

    @Override
    Object get(Object key) {
        String translatedKey = keyMapping[key]
        if (!translatedKey) {
            throw new NoSuchElementException("Parameter mapping does not contain key $key")
        }
        return wrapped[translatedKey]
    }
}
