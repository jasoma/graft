package com.github.jasoma.graft.internal

import com.github.jasoma.graft.Node
import com.github.jasoma.graft.Relation
import org.apache.commons.beanutils.PropertyUtils

import java.beans.PropertyDescriptor
import java.lang.reflect.AnnotatedElement
import java.lang.reflect.Field

/**
 * Internal utility class for performing reflection tasks.
 */
class Reflection {

    /**
     * Search for a named field on a class or anywhere in that class's type hierarchy.
     *
     * @param type the type to start the search at.
     * @param name the name of the field to find.
     * @return any matching field on the type or its superclasses.
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
     * Returns all possible sources for graft annotations for a particular property.
     *
     * @param type the type containing the property to find annotations for.
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
     * @param type the type containing the property to find annotations for.
     * @param property the descriptor of the property to check.
     * @return a list of annotation sources to check.
     */
    public static List<AnnotatedElement> annotationSources(Class<?> type, PropertyDescriptor property) {
        def sources = [property.readMethod, property.writeMethod, findField(type, property.name)]
        sources.removeAll { it == null }
        return sources
    }

    /**
     * Checks if a class is an entity type by looking for the presence of either of the {@link Node}
     * or {@link Relation} annotations.
     *
     * @param type the type to check.
     * @return true if an entity annotation is present anywhere in the class hierarchy, false otherwise.
     */
    public static boolean isEntity(Class<?> type) {
        return type.getAnnotation(Node) || type.getAnnotation(Relation)
    }

}