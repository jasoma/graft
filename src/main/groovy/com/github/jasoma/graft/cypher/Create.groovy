package com.github.jasoma.graft.cypher

import com.github.jasoma.graft.access.NeoQueryRunner
import com.github.jasoma.graft.ast.EntityProxy
import com.github.jasoma.graft.convert.ConcurrentMapRegistry
import com.github.jasoma.graft.convert.ConverterRegistry
import com.github.jasoma.graft.internal.EntitySchema
import com.github.jasoma.graft.internal.NodeSchema
import com.github.jasoma.graft.internal.PropertySchema

/**
 * Generates a {@code CREATE} create clause for a single node and it's properties. The generate clause will be for
 * the creation of a single node based on the entity. If the entity contains any relations they will be not be part
 * of the generated cypher statement and separate {@link Relate} instances should be created for each of them.
 * <p>
 * Property names in the generated cypher will not match the property names of entity but will be namespaced by the
 * {@link #identifier} of the clause. This is to allow multiple clauses to be batched into a single parameterized query.
 * <p>
 * Example:
 * <pre><code>
 *     @Node
 *     class MyNode {
 *         String foo
 *         String bar
 *     }
 *
 *     def create = new Create(new MyNode(foo: "foo", bar: "bar"), "n")
 *     assert create.parameterizedCypher == "CREATE (n:MyNode {foo: {n_foo}, bar: {n_bar}})"
 * </code></pre>
 */
class Create<T> implements CypherQuery {

    private final T node
    private final String identifier
    private final NodeSchema schema
    private Map<String, String> parametersToProperties

    /**
     * Generates a create clause for a node. A fixed identifier will be used.
     *
     * @param node the node to generate the create clause for.
     */
    Create(T node) {
        this(node, "n")
    }

    /**
     * Generates a create clause for a node with a specific identifier.
     *
     * @param node the node to generate the create clause for.
     * @param identifier the identifier to use for the node and parameters in the generated cypher.
     */
    Create(T node, String identifier) {
        this.node = Objects.requireNonNull(node, "entity cannot be null")
        this.identifier = Objects.requireNonNull(identifier, "identifier cannot be null")
        this.schema = new NodeSchema(nodeType())
        this.parametersToProperties = parameterMapping(node, schema, identifier)
    }

    private static Map<String, String> parameterMapping(def values, EntitySchema schema, String identifier) {
        Map<String, String> mapping = new HashMap<>()
        for (PropertySchema prop : schema.getScalarSchema()) {
            def value = values[prop.name]
            if (value == null) {
                continue
            }
            def param = "${identifier}_$prop.name"
            mapping[param] = prop.name
        }
        return mapping
    }

    private Class<?> nodeType() {
        if (node instanceof EntityProxy) {
            return node.entityClass
        }
        return node.class
    }

    /**
     * @return the identifier to be used in the create statement.
     */
    String getIdentifier() {
        return identifier
    }

    /**
     * Returns the name of the property on {@link #node} represented by a parameter name.
     *
     * @param parameterName the name of the generated parameter name in the clause.
     * @return the name of the property on the node.
     */
    public String propertyName(String parameterName) {
        return parametersToProperties[parameterName]
    }

    @Override
    public Map<String, Object> parameters(ConverterRegistry converters) {
        return buildQueryParameters(node, parametersToProperties, schema, converters)
    }

    private static Map<String, Object> buildQueryParameters(def values, Map<String, String> parameterMapping, EntitySchema schema, ConverterRegistry converters) {
        Map<String, Object> map = new HashMap<>()
        parameterMapping.each { param, prop ->
            def propertySchema = schema.getPropertySchema(prop)
            map[param] = propertySchema.toDbValue(values[prop], converters)
        }
        return map
    }

    @Override
    public String parameterizedCypher() {
        return buildCypher(parametersToProperties, identifier, schema.labels)
    }

    private static String buildCypher(Map<String, String> parameterMapping, String identifier, Collection<String> labels) {
        def builder = new StringBuilder("CREATE (")
        builder << identifier
        labels.each { builder << ":$it " }

        def paramList = parameterMapping.collect { "`$it.value`: {$it.key}"}
        builder << "{${paramList.join(", ")}})"
        return builder.toString()
    }

    /**
     * Executes the create query and returns the resulting node. If further operations are
     * to be performed locally the returned object should be used an not any initial instance
     * passed in.
     *
     * @param runner the query runner to execute the query.
     * @param converters a registry of converters for properties that need them.
     * @param reuseLocal whether or not to re-use the local {@link #node} instance after the
     *                   create call returns.
     * @returns a local instance of the created node.
     */
    def T create(NeoQueryRunner runner, ConverterRegistry converters, boolean reuseLocal = true) {
        def query = "${parameterizedCypher()} RETURN $identifier"
        def results = runner.run(parameters(converters), query)
        def createdNode = results.next().node(identifier)
        results.close()

        def localInstance = node
        if (!reuseLocal) {
            localInstance = schema.createInstance(createdNode.properties(), converters)
        }
        return (T) new EntityProxy(localInstance, createdNode.graphId(), schema.mappedProperties)
    }

    /**
     * Builds a create clause from a map of values passed directly and the class of the node to create.
     * Saves the values to the database and then creates and returns a local instance of the class.
     *
     * @param values the values to assign as properties of the node.
     * @param nodeType the local class of the node.
     * @param runner the query runner to execute the query.
     * @param converters a registry of converters for properties that need them.
     */
    def static <T> T create(Map values, Class<T> nodeType, NeoQueryRunner runner, ConcurrentMapRegistry converters) {
        def schema = new NodeSchema(nodeType)
        def parameterMapping = parameterMapping(values, schema, "n")
        def query = "${buildCypher(parameterMapping, "n", schema.labels)} RETURN n"
        def parameters = buildQueryParameters(values, parameterMapping, schema, converters)

        def results = runner.run(parameters, query)
        def createdNode = results.next().node("n")
        results.close()

        def local = schema.createInstance(createdNode.properties(), converters)
        return (T) new EntityProxy(local, createdNode.graphId(), schema.mappedProperties)
    }

}
