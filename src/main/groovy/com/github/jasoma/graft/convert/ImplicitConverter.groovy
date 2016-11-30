package com.github.jasoma.graft.convert

import com.github.jasoma.graft.Deserialize
import com.github.jasoma.graft.Serialize

import java.lang.reflect.Method

/**
 * A property converter for classes that can self toDbValue using methods annotated with {@link Serialize} and
 * {@link Deserialize}.
 */
class ImplicitConverter implements PropertyConverter {

    private final Method serialize
    private final Method deserialize

    /**
     * Check if a class has the methods required for implicit conversion.
     *
     * @param type the type to check.
     * @return true if the class contains both serialize and deserialize methods.
     */
    public static boolean isImplicitlyConvertable(Class<?> type) {
        def serialize = type.methods.find { it.getAnnotation(Serialize) != null }
        def deserialize = type.methods.find { it.getAnnotation(Deserialize) != null }
        return serialize && deserialize
    }

    /**
     * Create an implicit converter for a type.
     *
     * @param type the type to build the converter from.
     */
    public ImplicitConverter(Class<?> type) {
        def serialize = type.methods.find { it.getAnnotation(Serialize) != null }
        def deserialize = type.methods.find { it.getAnnotation(Deserialize) != null }
        this.serialize = Objects.requireNonNull(serialize, "serialize method cannot be null")
        this.serialize.setAccessible(true)
        this.deserialize = Objects.requireNonNull(deserialize, "deserialize method cannot be null")
        this.deserialize.setAccessible(true)
    }

    @Override
    Object toDbValue(Object value) {
        return serialize.invoke(null, value)
    }

    @Override
    Object fromDbValue(Object dbValue, Class<?> targetType) {
        return deserialize.invoke(null, dbValue)
    }
}
