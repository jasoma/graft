package com.github.jasoma.graft

import com.github.jasoma.graft.access.NeoDatabase
import com.github.jasoma.graft.internal.Reflection

/**
 * Created by jason on 11/25/16.
 */
class Graft {

    private final NeoDatabase db

    Graft(NeoDatabase db) {
        this.db = db
    }

    /**
     * @return the underlying database access.
     */
    NeoDatabase getDb() {
        return db
    }

    /**
     * Saves an object graph to the database. The root object passed into the method and all nodes/relationships
     * attached to it will be created or updated in the database. If any of the objects being persisted are new
     * then they will have their graphId assigned after the method call completed.
     *
     * @param root the root of the object graph to save.
     */
    def void save(def root) {
        assertEntity(root, "save")
    }

    private def void assertEntity(def obj, String action) {
        if (!Reflection.isEntity(obj.class)) {
            throw new IllegalArgumentException("Cannot perform '$action' on obect (${obj.class.getSimpleName()}, $obj) " +
                    "as it is not a @Node or @Relation type")
        }
    }

}
