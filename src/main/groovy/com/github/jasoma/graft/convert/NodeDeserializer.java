package com.github.jasoma.graft.convert;

import com.github.jasoma.graft.access.NeoNode;
import com.github.jasoma.graft.access.ResultRow;

/**
 * Interface for conversions between nodes and types.
 */
interface NodeDeserializer {

    /**
     * Convert a node from the database, along with the result row it came from, into a local type.
     *
     * @param type the type to convert the node to.
     * @param node the node to convert.
     * @param row the result row the node came from.
     * @return the created and populated type.
     * @throws NodeDeserializationException if the node cannot be deserialized.
     */
    public <T> T convert(Class<T> type, NeoNode node, ResultRow row) throws NodeDeserializationException;

}
