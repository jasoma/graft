package com.github.jasoma.graft.deserialize;

import com.github.jasoma.graft.access.NeoEntity;
import com.github.jasoma.graft.access.ResultRow;

/**
 * Interface for conversions between nodes and types.
 */
interface EntityDeserializer {

    /**
     * Convert an entity from the database, along with the result row it came from, into a local type.
     *
     * @param type the type to deserialize the node to.
     * @param entity the entity to deserialize.
     * @param row the result row the node came from.
     * @return the created and populated type.
     * @throws EntityDeserializationException if the node cannot be deserialized.
     */
    public <T> T convert(Class<T> type, NeoEntity entity, ResultRow row) throws EntityDeserializationException;

}
