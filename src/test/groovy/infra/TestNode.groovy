package infra

import com.github.jasoma.graft.access.NeoNode

/**
 * NeoNode implementation for testing, all properties can be set externally.
 */
class TestNode implements NeoNode {

    Map<String, Object> properties
    long id = 0
    List<String> labels

    /**
     * Creates an empty node.
     */
    TestNode() {
        this(new HashMap<>())
    }

    /**
     * Create a node with a set of properties.
     *
     * @param properties the properties to pre-assign.
     */
    TestNode(Map<String, Object> properties) {
        this.properties = properties
    }

    /**
     * Set a property on the test node.
     *
     * @param key the key to store the property under.
     * @param value the value of the property.
     * @return the previous value for the property.
     */
    def putAt(String key, def value) {
        properties[key] = value
    }


    @Override
    Iterable<String> labels() {
        return labels;
    }

    @Override
    long graphId() {
        return id
    }

    @Override
    Object get(String key) {
        return properties[key]
    }

    @Override
    Map<String, Object> properties() {
        return properties
    }

    @Override
    def <T> T unwrap(Class<T> type) {
        throw new UnsupportedOperationException()
    }
}
