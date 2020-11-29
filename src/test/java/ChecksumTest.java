import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class ChecksumTest {

    FramesManager fm;
    Frame frame;
    String string_frame;
    @Before
    public void init() {
        fm = new FramesManager();
        frame = new Frame((byte) 'I', 4, "Frame data #4".getBytes());
        string_frame = frame.toSendFormat();
        string_frame = fm.frameExtract(string_frame);
    }

    @Test
    public void containsErrorTest() {
        Assert.assertFalse(Checksum.containsError(string_frame));
    }

    @Test
    public void calculCRCTest() {
        Assert.assertEquals("", "");
        Assert.assertEquals("0001101110011000", Checksum.calculCRC("1000000000000000"));
    }

    @Test
    public void xor_divTest() {
        Assert.assertEquals("0", Checksum.xor_div(string_frame));
        Assert.assertEquals("1011101100100001", Checksum.xor_div("100000000010101010101001"));
        Assert.assertEquals("0", Checksum.xor_div("0000000000000000000000"));
        Assert.assertEquals("1", Checksum.xor_div("1"));
        Assert.assertEquals("", Checksum.xor_div(""));
    }

}
