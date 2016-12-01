package com.github.jasoma.graft.internal

/**
 * Describes a {@link com.github.jasoma.graft.Node} entity and how it is persisted to neo4j.
 */
class NodeSchema extends EntitySchema {

    private Set<String> labels

    /**
     * Creates the schema.
     *
     * @param nodeType the type of the node.
     */
    NodeSchema(Class<?> nodeType) {
        super(nodeType)
        this.labels = Collections.unmodifiableSet(Reflection.nodeLabels(nodeType))
    }

    /**
     * @return the set of labels for this node in neo4j.
     */
    List<String> getLabels() {
        return labels
    }
}
