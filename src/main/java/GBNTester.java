import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Random;

public class GBNTester {

    private BufferedWriter writer;
    private String OutputFileName;

    public void setOutputFile(String fileOutputName) {
        OutputFileName = fileOutputName;
        try {
            writer = new BufferedWriter(new FileWriter(fileOutputName));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void writeDataFrame(byte[] data) {
        try {
            if (data != null) {
                String data_string = new String(data, StandardCharsets.UTF_8);
                writer.write(data_string);
                writer.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public int simulateFrameLost(int windowIndex){
        System.out.println("SENDER (I, 2, index " + windowIndex+" LOST)");
        windowIndex++;

        return windowIndex;
    }

    public String generateBitFlipError(Frame frame, int index) {
        Random random = new Random();
        System.out.println("SENDER (" + (char) frame.getType()+ ", "+
                frame.getNum() +", BIT FLIP index " + index + ")");
        String stringFrame = frame.toSendFormat();
        int max_bit_index = stringFrame.length()-(8 + 16); // w/o flag and CRC
        int ran_bit_index = random.nextInt(max_bit_index - 8) + 8;  // w/o flag
        return DataManipulation.bitFlip(stringFrame, ran_bit_index);   // flip a random bit
    }

    public void createInputFile (String fileName, int frames_nb) {
        try {
            BufferedWriter b_writer = new BufferedWriter(new FileWriter(fileName));
            for (int i = 1; i <= frames_nb; i++) {
                b_writer.write("Frame data #" + i);
                b_writer.newLine();
            }

            b_writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void checkReceiverOutput() throws IOException {
        if (Utils.filesEquals("src/test/text/test.txt", this.OutputFileName))
            System.out.println("Receiver received all frames");
        else
            System.out.println("Receiver did'nt receive all frames");
    }

    public BufferedWriter getWriter() { return this.writer; }

}
