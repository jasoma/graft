package com.github.jasoma.graft.convert

import com.github.jasoma.graft.Deserialize
import com.github.jasoma.graft.Serialize
import groovy.transform.EqualsAndHashCode
import infra.TestClass
import org.junit.Test

class ImplicitConverterTest implements TestClass {

    @Test
    def void testNoMethods() {
        assert !ImplicitConverter.isImplicitlyConvertable(NoMethods)
        assertThrows { new ImplicitConverter(NoMethods) }
    }

    @Test
    def void testGetterSetterOnly() {
        assert !ImplicitConverter.isImplicitlyConvertable(SerializeOnly)
        assertThrows { new ImplicitConverter(SerializeOnly) }
        assert !ImplicitConverter.isImplicitlyConvertable(DeserializeOnly)
        assertThrows { new ImplicitConverter(DeserializeOnly) }
    }

    @Test
    def void testIsImplicitlyConvertable() {
        assert ImplicitConverter.isImplicitlyConvertable(TestType)
    }

    @Test
    def void testConversion() {
        def converter = new ImplicitConverter(TestType)
        def obj = new TestType(value: 42)
        def dbValue = converter.toDbValue(obj)
        def andBack = converter.fromDbValue(dbValue, TestType)
        assert obj == andBack
    }

    @EqualsAndHashCode
    private static class TestType {

        int value

        @Deserialize
        public static TestType fromString(String value) {
            def t = new TestType()
            t.value = Integer.parseInt(value)
            return t
        }

        @Serialize
        public static String toString(TestType obj) {
            return Integer.toString(obj.value)
        }

    }

    private static class NoMethods { }

    private static class SerializeOnly {
        @Serialize
        public static String toDbValue(SerializeOnly value) {
            return ""
        }
    }

    private static class DeserializeOnly {
        @Deserialize
        public static DeserializeOnly fromDbValue(Object value) {
            return new DeserializeOnly()
        }
    }

}
