package com.github.jasoma.graft

/**
 * The set of states a {@link Node} or {@link Relation} class can be in.
 */
enum EntityState {

    /**
     * A new entity created locally but not yet saved to the database.
     */
    Unsaved,

    /**
     * An entity loaded from the database with no local modifications.
     */
    Unmodified,

    /**
     * An entity loaded from the database and modified locally.
     */
    Modified,

    /**
     * A reference to an entity that was loaded from the database and then deleted.
     */
    Deleted

}
