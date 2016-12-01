package com.github.jasoma.graft.cypher;

import com.github.jasoma.graft.convert.ConverterRegistry;

import java.util.Map;

/**
 * Interface for all cypher queries.
 */
public interface CypherQuery {

    /**
     * Generate the cypher query text with placeholders for any parameters that are used.
     *
     * @return the text of the cypher query.
     */
    String parameterizedCypher();

    /**
     * Build a parameter map for executing the query with.
     *
     * @param converters the set of converters to use when converting properties to neo4j values.
     * @return the parameters to be placed into the cypher query.
     */
    Map<String, Object> parameters(ConverterRegistry converters);

}
