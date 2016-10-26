package models

import com.github.jasoma.graft.Relation

/**
 * A node model containing the collection types supported by neo4j in various forms.
 */
@Relation
class CollectionPropertiesNode {

    List<Integer> integerList
    int [] intArray
    Map<String, Boolean> booleanMap

}

/**
 * A relation model containing the collection types supported by neo4j in various forms.
 */
@Relation
class CollectionPropertiesRelation {

    List<Integer> integerList
    int [] intArray
    Map<String, Boolean> booleanMap

}
