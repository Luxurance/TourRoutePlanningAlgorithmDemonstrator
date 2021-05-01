package webapp;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.*;

public class WebServletsTest {
    private static Method isValidPlace;

    @BeforeAll
    static void init() throws NoSuchMethodException {
        Class<WebServlets> webServletsClass = WebServlets.class;
        isValidPlace = webServletsClass.getDeclaredMethod("isValidPlace", String.class);
        isValidPlace.setAccessible(true);
    }

    @Test
    void testIsValidPlaceNoSuchPlace() throws InvocationTargetException, IllegalAccessException {
        assertFalse((boolean) isValidPlace.invoke(null, "Mars"));
    }

    @Test
    void testIsValidPlaceExistSuchPlace() throws InvocationTargetException, IllegalAccessException {
        assertTrue((boolean) isValidPlace.invoke(null, "Angel"));
    }
}
