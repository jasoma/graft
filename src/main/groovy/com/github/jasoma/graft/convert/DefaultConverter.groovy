package com.github.jasoma.graft.convert

import com.github.jasoma.graft.access.NeoEntity

/**
 * An property converter that reads and writes simple properties based on JavaBeans naming conventions.
 * Does not perform and type conversions on write and only groovy casting on read.
 */
class DefaultConverter implements PropertyConverter {

    /**
     * Shared instance of the converter.
     */
    public static final DefaultConverter INSTANCE = new DefaultConverter()

    @Override
    void write(Object value, String name, PropertyConverter.PropertyWriter writer) {
        writer.addProperty(name, value)
    }

    @Override
    def <T> T read(Class<T> type, String name, NeoEntity entity) {
        return entity.properties()[name]?.asType(type)
    }
}
