package com.github.jasoma.graft.convert

/**
 * Utility methods for mixing into {@link NodeDeserializer} implementations.
 */
trait DeserializerUtils {

    /**
     * Determines the type of a named property on an object by looking for JavaBeans named accessors. If no getter
     * or setter can be found returns null in case the property is dynamic.
     *
     * @param instance the instance to check the property type for.
     * @param name the name of the property to get the type for.
     * @return the type of the named property or null if it could not be determined.
     */
    Class getPropertyType(Object instance, String name) {
        def setterName = "set${name.capitalize()}".toString()
        def getterName = "get${name.capitalize()}".toString()

        def method = instance.class.methods.find { it.name == setterName || it.name == getterName }

        if (method == null) {
            return null
        }

        if (method.returnType == Void.class) {
            // setter, check the first argument.
            return method.parameterTypes[0]
        }

        return method.returnType
    }

}
