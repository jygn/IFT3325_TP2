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

    public String generateBitFlipError(String binFrame, int frame_num) {
        Random random = new Random();
        System.out.println("GÉNÉRATION D'ERREUR (FLIPBIT) au frame #" + frame_num);
        int max_bit_index = binFrame.length()-(8 + 16); // w/o flag and CRC
        int ran_bit_index = random.nextInt(max_bit_index - 8) + 8;  // w/o flag
        return DataManipulation.bitFlip(binFrame, ran_bit_index);   // flip a random bit
    }

    public void checkReceiverOutput() throws IOException {
        if (Utils.filesEquals("src/test/text/test.txt", this.OutputFileName))
            System.out.println("Receiver received all frames");
        else
            System.out.println("Receiver did'nt receive all frames");
    }

    public BufferedWriter getWriter() { return this.writer; }

}
