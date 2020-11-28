import org.junit.Assert;
import org.junit.Test;

import java.util.List;

public class DataManipulationTest {

    @Test
    public void bytesToBinTest() {
        byte[] data = {'d','a','t','a','1','2','t','e','s','t'};
        Assert.assertEquals("01100100011000010111010001100001001100010011001001110100011001010111001101110100",
                DataManipulation.bytesToBin(data));
        byte[] data2 = {};
        Assert.assertEquals("", DataManipulation.bytesToBin(data2));
    }

    @Test
    public void stringToByteTest() {
        String test = "I";
        Assert.assertEquals(73, DataManipulation.stringTobyte(test));
    }

    @Test
    public void bitsPaddingTest() {
        Assert.assertEquals("", DataManipulation.bitsPadding(""));
        Assert.assertEquals("00000000", DataManipulation.bitsPadding("0"));
        Assert.assertEquals("00000101", DataManipulation.bitsPadding("0101"));
        Assert.assertEquals("01111111", DataManipulation.bitsPadding("1111111"));
        Assert.assertEquals("11111111", DataManipulation.bitsPadding("11111111"));
        Assert.assertEquals("0000000101010000", DataManipulation.bitsPadding("00101010000"));
    }

    @Test
    public void bitStuffingTest() {
        Assert.assertEquals("", DataManipulation.bitStuffing(""));
        Assert.assertEquals("00000000", DataManipulation.bitStuffing("00000000"));
        Assert.assertEquals("111110111", DataManipulation.bitStuffing("11111111"));
        Assert.assertEquals("001111", DataManipulation.bitStuffing("001111"));
        Assert.assertEquals("0011111000111110", DataManipulation.bitStuffing("00111110011111"));
    }

    @Test
    public void bitUnStuffingTest() {
        // TODO..
    }







}