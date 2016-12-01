package infra

/**
 * Helper methods for tests.
 */
trait TestClass {

    def void assertThrows(Closure action) {
        try {
            action()
            throw new AssertionError("No exception thrown from action")
        }
        catch (Exception e) {}
    }

    def void assertThrows(Closure action, Class<? extends Exception> exception) {
        try {
            action()
            throw new AssertionError("${exception.simpleName} not thrown from action")
        }
        catch (Exception e) {
            if (!(exception.isInstance(e))) {
                throw new AssertionError("Expected exception of entityType ${exception.simpleName} but ${e.class.simpleName} was thrown", e)
            }
        }
    }

    def void assertContains(String container, String target) {
        if (container.contains(target)) {
            return
        }
        throw new AssertionError("Could not find target string '$target' inside of '$container'")
    }
}
