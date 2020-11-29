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
        Assert.assertFalse(fm.containsError(string_frame));
    }

    @Test
    public void xor_divTest() {
        Assert.assertEquals("0", Checksum.xor_div(string_frame));
    }

}
