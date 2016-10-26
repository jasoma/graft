package models

/**
 * A node model where at least one property is a complex type that follows the JavaBeans conventions.
 */
class BeanPropertiesNode {

    String name
    JavaBeanType bean

}

/**
 * A relation model where at least one property is a complex type that follows the JavaBeans conventions.
 */
class BeanPropertiesRelation {

    String name
    JavaBeanType bean

}

class JavaBeanType {

    boolean aBoolean
    String aString
    long aLong
    double aDouble

}
