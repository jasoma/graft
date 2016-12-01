package com.github.jasoma.graft.internal

import com.github.jasoma.graft.Unmapped
import org.junit.Test

class ReflectionTest {

    @Test
    def void testMappedProperties() {
        def groovy = Reflection.mappedProperties(GroovyProperties)
        assert groovy.contains("mapped")
        assert !groovy.contains("unmapped")
    }

    @Test
    def void testMappedPropertiesCustomAccessors() {
        def javaBeans = Reflection.mappedProperties(JavaBeanProperties)
        assert javaBeans.contains("mapped")
        assert !javaBeans.contains("field")
        assert !javaBeans.contains("getter")
        assert !javaBeans.contains("setter")
    }

    @Test
    def void testMappedPropertiesSetterGetterOnly() {
        def mapped = Reflection.mappedProperties(SetterGetterOnly)
        assert mapped.empty
    }

    private static class GroovyProperties {
        String mapped
        @Unmapped String unmapped
    }

    private static class JavaBeanProperties {

        @Unmapped private String field
        private String getter
        private String setter
        private String mapped

        String getField() {
            return field
        }

        void setField(String field) {
            this.field = field
        }

        @Unmapped
        String getGetter() {
            return getter
        }

        void setGetter(String getter) {
            this.getter = getter
        }

        String getSetter() {
            return setter
        }

        @Unmapped
        void setSetter(String setter) {
            this.setter = setter
        }

        String getMapped() {
            return mapped
        }

        void setMapped(String mapped) {
            this.mapped = mapped
        }
    }

    private static class SetterGetterOnly {

        private String setter
        private String getter

        String getGetter() {
            return getter
        }

        void setSetter(String setter) {
            this.setter = setter
        }
    }

}
