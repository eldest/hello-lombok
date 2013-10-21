package lombok;

import org.junit.Test;

/**
 * Created with IntelliJ IDEA for hello-lombok.
 * User: EremenkoAA
 * Date: 21.10.13
 */
public class HelloWorldTest {

    @Test
    public void helloWorldTest() throws Throwable {
        TestSubject.class.getMethod("helloWorld").invoke(new TestSubject());
    }
}
