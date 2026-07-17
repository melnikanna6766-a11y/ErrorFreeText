package com.github.melnikanna6766a11y.errorfreetext;

import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class ArrayHandlerTest {

    @Test
    public void createResponseArrayWhereArray10000Test() {
        ArrayHandler arrayHandler = new ArrayHandler();
        String[] strings = new String[10000];
        Arrays.fill(strings, "мяу");
        strings[3332] = "jjj";
        String[] testArray = arrayHandler.createResponseArray(strings, 0, arrayHandler.calculateLimit(strings));
        assertEquals(3333, testArray.length);
        assertEquals("jjj", testArray[3332]);
    }

    @Test
    public void createResponseArrayWhereArray20000Test() {
        int index = 0;
        ArrayHandler arrayHandler = new ArrayHandler();
        String[] strings = new String[20000];
        Arrays.fill(strings, "мяу");
        int limit =  arrayHandler.calculateLimit(strings);
        String[] testArray = null;
        while (index <= strings.length) {
            testArray = arrayHandler.createResponseArray(strings, index, limit);
            index += limit;
        }
        assertEquals(2, testArray.length);
    }

    @Test
    public void createResponseArrayWhereArray19000Test() {
        int index = 0;
        ArrayHandler arrayHandler = new ArrayHandler();
        String[] strings = new String[19000];
        Arrays.fill(strings, "мяу");
        int limit =  arrayHandler.calculateLimit(strings);
        String[] testArray = null;
        while (index <= strings.length-1) {
            testArray = arrayHandler.createResponseArray(strings, index, limit);
            index += limit;
        }
        assertEquals(2335, testArray.length);
    }

    @Test
    public void createResponseArrayWhereIndexNegativeTest() {
        int index = -1;
        ArrayHandler arrayHandler = new ArrayHandler();
        String[] strings = null;
        int limit = -1;
        String[] testArray = arrayHandler.createResponseArray(strings, index, limit);
        assertNull(testArray);
    }
}
