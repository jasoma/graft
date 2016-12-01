package com.github.jasoma.graft.cypher

import com.github.jasoma.graft.access.NeoQueryRunner
import com.github.jasoma.graft.ast.EntityProxy
import com.github.jasoma.graft.convert.ConverterRegistry
import com.github.jasoma.graft.internal.EntitySchema
import com.github.jasoma.graft.internal.NodeSchema
import com.github.jasoma.graft.internal.PropertySchema
import com.github.jasoma.graft.internal.Reflection

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
class Create implements CypherQuery {

    private final def node
    private final String identifier
    private final EntitySchema schema
    private Map<String, String> parametersToProperties = new HashMap<>()

    /**
     * Generates a create clause for a node. A fixed identifier will be used.
     *
     * @param node the node to generate the create clause for.
     */
    Create(def node) {
        this(node, "n")
    }

    /**
     * Generates a create clause for a node with a specific identifier.
     *
     * @param node the node to generate the create clause for.
     * @param identifier the identifier to use for the node and parameters in the generated cypher.
     */
    Create(def node, String identifier) {
        this.node = Objects.requireNonNull(node, "entity cannot be null")
        this.identifier = Objects.requireNonNull(identifier, "identifier cannot be null")
        this.schema = new NodeSchema(nodeType())

        for (PropertySchema prop : schema.scalarSchema()) {
            def value = node[prop.name]
            if (value == null || Reflection.isEntity(value)) {
                continue
            }
            def param = "${identifier}_$prop.name"
            parametersToProperties[param] = prop.name
        }
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
        Map<String, Object> map = new HashMap()
        parametersToProperties.each { param, prop ->
            def propertySchema = schema.propertySchema(prop)
            map[param] = propertySchema.toDbValue(node[prop], converters)
        }
        return map
    }

    @Override
    public String parameterizedCypher() {
        def builder = new StringBuilder("CREATE (")
        builder << identifier
        node.labels.each { builder << ":$it " }

        def paramList = parametersToProperties.collect { "`$it.value`: {$it.key}"}
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
     * @returns a local instance of the created node.
     */
    def create(NeoQueryRunner runner, ConverterRegistry converters) {
        def query = "${parameterizedCypher()} RETURN ID($identifier) AS id"
        def results = runner.run(parameters(converters), query)
        def id = results.next().get("id") as long
        results.close()
        return new EntityProxy(node, id, schema.scalarProperties())
    }

}
