package models
/**
 * A node model containing properties that are other related nodes.
 */
class LinkedNodeProperties {

    /**
     * Set of {@link SimplePropertiesNode} that are linked to this node via a {@link SimplePropertiesRelation}
     * that terminates at this node.
     */
//    @Incoming(SimplePropertiesRelation) Set<SimplePropertiesNode> simpleLinks

    /**
     * Set of {@link BeanPropertiesNode} that are linked to this node via a {@link BeanPropertiesNode} which
     * begins at this node.
     */
//    @Outgoing Related<BeanPropertiesRelation, BeanPropertiesNode> beanLinks

}
