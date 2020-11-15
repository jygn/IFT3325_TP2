import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class Utility {


    public static String readFile(String fileName) {

        StringBuilder data = new StringBuilder();
        String line = "";

        try {
            BufferedReader input = new BufferedReader(new FileReader(fileName));

            while ((line = input.readLine()) != null) {
                data.append(line);
            }


        } catch (IOException e) {
            e.printStackTrace();
        }

        return data.toString();

    }


    public static String bytesToBin(byte[] bts) {

        String bin = "";

        for (byte b : bts) {
            bin += Integer.toBinaryString(b);
        }

        return bin;
    }

}
