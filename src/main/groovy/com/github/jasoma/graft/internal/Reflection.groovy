package com.github.jasoma.graft.internal

import com.github.jasoma.graft.Node
import com.github.jasoma.graft.Relation
import com.github.jasoma.graft.Unmapped
import com.github.jasoma.graft.ast.EntityProxy
import org.apache.commons.beanutils.PropertyUtils

import java.beans.PropertyDescriptor
import java.lang.annotation.Annotation
import java.lang.reflect.AnnotatedElement
import java.lang.reflect.Field
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ConcurrentMap
/**
 * Internal utility class for performing reflection tasks.
 */
class Reflection {

    private static final String GROOVY_METACLASS_PROPERTY = "metaClass"
    private static final ConcurrentMap<Class, Set<PropertySchema>> MAPPED_PROPERTY_CACHE = new ConcurrentHashMap<>()

    /**
     * Find all properties for a class that do not have the {@link Unmapped} annotation. Result of this
     * operation are cached internally.
     *
     * @param type the class to find properties for.
     * @return the set of all property names that are mapped to the database.
     */
    public static Set<PropertySchema> mappedProperties(Class<?> type) {
        return MAPPED_PROPERTY_CACHE.computeIfAbsent(type, { key -> readMappedProperties(key)})
    }

    /**
     * Reads all properties for a class that do not have the {@link Unmapped} annotation. Bypasses the cache
     * used in {@link #mappedProperties(java.lang.Class)}.
     *
     * @param type the class to find properties for.
     * @return the set of all property names that are mapped to the database.
     */
    public static Set<PropertySchema> readMappedProperties(Class<?> type) {
        HashSet<PropertySchema> set = new HashSet<>()
        def descriptors = PropertyUtils.getPropertyDescriptors(type).toList()
        descriptors.removeAll { it.name == GROOVY_METACLASS_PROPERTY || it.readMethod == null || it.writeMethod == null }

        for (PropertyDescriptor property : descriptors) {
            def sources = annotationSources(type, property)
            if (sources.any { it.isAnnotationPresent(Unmapped) } ) {
                continue
            }
            set.add(PropertySchema.forDescriptor(type, property))
        }
        return set
    }

    /**
     * Clears the internal cache used by {@link #mappedProperties}.
     */
    public static void clearPropertyNameCache() {
        MAPPED_PROPERTY_CACHE.clear()
    }

    /**
     * Search for a named field on a class or anywhere in that class's entityType hierarchy.
     *
     * @param type the entityType to start the search at.
     * @param name the name of the field to find.
     * @return any matching field on the entityType or its superclasses.
     */
    public static Field findField(Class<?> type, String name) {
        Class<?> current = type
        while (current && current != Object) {
            def field = current.declaredFields.find { it.name == name }
            if (field) {
                return field
            }
            current = current.superclass
        }
        return  null
    }

    /**
     * Search for an annotation on a property looking in each of the locations returned by {@link #annotationSources}
     *
     * @param propertyOwner the class that owns the property.
     * @param descriptor the property descriptor.
     * @param annotationType the entityType of annotation to find.
     * @return the annotation if it is present on any of the sources, null otherwise.
     */
    public static <T extends Annotation> T findAnnotation(Class<?> propertyOwner, PropertyDescriptor descriptor, Class<T> annotationType) {
        for (AnnotatedElement element : annotationSources(propertyOwner, descriptor)) {
            def annotation = element.getAnnotation(annotationType)
            if (annotation) {
                return annotation
            }
        }
        return null
    }

    /**
     * Returns all possible sources for graft annotations for a particular property.
     *
     * @param type the entityType containing the property to find annotations for.
     * @param propertyName the name of the property.
     * @return a list of annotation sources to check.
     */
    public static List<AnnotatedElement> annotationSources(Class<?> type, String propertyName) {
        def property = PropertyUtils.getPropertyDescriptors(type).find { it.name == propertyName }
        return (property) ? annotationSources(type, property) : null
    }

    /**
     * Returns all possible sources for graft annotations for a particular property.
     *
     * @param type the entityType containing the property to find annotations for.
     * @param property the descriptor of the property to check.
     * @return a list of annotation sources to check.
     */
    public static List<AnnotatedElement> annotationSources(Class<?> type, PropertyDescriptor property) {
        def sources = [property.readMethod, property.writeMethod, findField(type, property.name)]
        sources.removeAll { it == null }
        return sources
    }

    /**
     * Checks if an object is an entity entityType by looking for the presence of either of the {@link Node}
     * or {@link Relation} annotations on its class.
     *
     * @param obj the instance to check.
     * @return true if an entity annotation is present anywhere in the class hierarchy, false otherwise.
     */
    public static boolean isEntity(def obj) {
        if (obj instanceof EntityProxy) {
            return true
        }
        return isNode(obj.class) || isRelation(obj.class)
    }

    /**
     * Checks if a class is an entity entityType by looking for the presence of either of the {@link Node}
     * or {@link Relation} annotations.
     *
     * @param type the entityType to check.
     * @return true if an entity annotation is present anywhere in the class hierarchy, false otherwise.
     */
    public static boolean isEntity(Class<?> type) {
        return isNode(type) || isRelation(type)
    }

    /**
     * Checks if an object is a {@link Node} entityType.
     *
     * @param obj the instance to check.
     * @return true if the annotation is present.
     */
    public static boolean isNode(def obj) {
        if (obj instanceof EntityProxy) {
            return isNode(obj.entityClass)
        }
        return isNode(obj.class)
    }

    /**
     * Checks if a class has the {@link Node} annotation.
     *
     * @param type the class to check.
     * @return true if the annotation is present.
     */
    public static boolean isNode(Class<?> type) {
        return type.getAnnotation(Node)
    }

    /**
     * Checks if an object is a {@link Relation} entityType.
     *
     * @param obj the instance to check.
     * @return true if the annotation is present.
     */
    public static boolean isRelation(def obj) {
        if (obj instanceof EntityProxy) {
            return isRelation(obj.entityClass)
        }
        return isRelation(obj.class)
    }

    /**
     * Checks if a class has the {@link Relation} annotation.
     *
     * @param type the class to check.
     * @return true if the annotation is present.
     */
    public static boolean isRelation(Class<?> type) {
        return type.getAnnotation(Relation)
    }

    /**
     * Gets the list of labels used to mark a node class in neo4j.
     *
     * @param nodeType the node class.
     * @return the set of labels to apply to the node in neo4j.
     */
    public static Set<String> nodeLabels(Class<?> nodeType) {
        if (!isNode(nodeType)) {
            throw new IllegalArgumentException("$nodeType does not have the @Node annotation")
        }
        def annotation = nodeType.getAnnotation(Node)
        if (annotation.labels().length == 0) {
            return Collections.singleton(nodeType.simpleName)
        }
        return new HashSet<String>(annotation.labels().toList())
    }

    /**
     * The type name used to mark a relation class in neo4j.
     *
     * @param relationClass the relation class.
     * @return the name of the relation type in neo4j.
     */
    public static String relationType(Class<?> relationClass) {
        if (!isRelation(relationClass)) {
            throw new IllegalArgumentException("$relationClass does not have the @Relation annotation")
        }
        def annotation = relationClass.getAnnotation(Relation)
        if (annotation.value().isEmpty() || annotation.value().isAllWhitespace()) {
            return relationClass.simpleName
        }
        return annotation.value()
    }
}
