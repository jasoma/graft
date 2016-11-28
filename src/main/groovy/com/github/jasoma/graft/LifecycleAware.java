package com.github.jasoma.graft;

/**
 * An interface for node or relation types that want to be notified of lifecycle events. All methods have no-op default implementations.
 */
public interface LifecycleAware {

    /**
     * Called before the object is saved to the database for the first time.
     * <p>
     * Whether or not this method or {@link #beforeUpdate} is called is based on the presence of a graphId on the object.
     */
    default void beforeCreate() {}

    /**
     * Called before the object has changes saved to the database.
     * <p>
     * Whether or not this method or {@link #beforeCreate} is called is based on the presence of a graphId on the object.
     */
    default void beforeUpdate() {}

    /**
     * Called before the object is deleted from the database.
     */
    default void beforeDelete() {}

    /**
     * Called after the object is recreated from database results but before it is returned to the application.
     */
    default void afterRead() {}

}
