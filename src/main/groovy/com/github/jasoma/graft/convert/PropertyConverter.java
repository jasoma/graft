package com.github.jasoma.graft.convert;

/**
 * Interface for converting complex properties into values suitable for storage in neo4j and back. Implementations
 * must be thread safe.
 */
public interface PropertyConverter {

    /**
     * Convert a local property value to neo4j compatible types for storage.
     *
     * @param value the value to toDbValue.
     * @return a neo4j compatible type.
     */
    Object toDbValue(Object value);

    /**
     * Convert a neo4j value into a local type.
     *
     * @param dbValue the value stored in the database.
     * @param targetType the local type to toDbValue from.
     * @return the converted value.
     */
    Object fromDbValue(Object dbValue, Class<?> targetType);

}
