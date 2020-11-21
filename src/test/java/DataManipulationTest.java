import org.junit.Assert;
import org.junit.Test;

import java.util.List;

public class DataManipulationTest {

    @Test
    public void byteToStringTest () {

        byte test = 73;
        Assert.assertEquals("I", DataManipulation.byteToString(test));
    }

    @Test
    public void stringToByteTest() {
        String test = "I";
        Assert.assertEquals(73, DataManipulation.stringTobyte(test));
    }



}