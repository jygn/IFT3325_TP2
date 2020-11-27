import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class Utils {

    public static byte[][] readLines(String filename) {

        int n = fileLinesCount(filename);
        byte[][] lines = new byte[n][];

        try {

            BufferedReader bufferedReader = new BufferedReader(new FileReader(filename));
            byte[] line_bytes;
            String line = null;
            int i = 0;

            while ((line = bufferedReader.readLine()) != null) {
                line_bytes = line.getBytes();
                lines[i++] = line_bytes;
            }

            bufferedReader.close();

        } catch (IOException e) {
            e.printStackTrace();
        }

        return lines;

    }

    public static int fileLinesCount(String filename) {

        int lines_counter = 0;

        try {
            BufferedReader bufferedReader = new BufferedReader(new FileReader(filename));
            while (bufferedReader.readLine() != null) lines_counter++;
            bufferedReader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return lines_counter;
    }

    public static boolean filesEquals (String filename1, String filename2) throws IOException {

        BufferedReader reader1 = new BufferedReader(new FileReader(filename1));
        BufferedReader reader2 = new BufferedReader(new FileReader(filename2));

        String line1 = reader1.readLine();
        String line2 = reader2.readLine();

        boolean areEqual = true;

        int lineNum = 1;
        while (line1 != null || line2 != null) {
            if(line1 == null || line2 == null) {
                areEqual = false;
                break;
            }
            else if(! line1.equalsIgnoreCase(line2)) {
                areEqual = false;
                break;
            }

            line1 = reader1.readLine();
            line2 = reader2.readLine();
            lineNum++;
        }

        reader1.close();
        reader2.close();
        return areEqual;
    }

}
