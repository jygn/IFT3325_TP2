import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

/**
 * Classe de fonctions utlitaires
 */
public class Utils {

    /**
     * Lit un fichier ligne par ligne et transforme chaque ligne en array de bytes
     * @param filename Nom du fichier
     * @return Tableau de tableaux de bytes
     */
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

    /**
     * Compte le nombre de ligne d'un fichier
     * @param filename Nom du fichier
     * @return entier
     */
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

    /**
     * Vérifie si 2 fichiers sont identiques
     * @param filename1 Nom du premier fichier
     * @param filename2 Nom du deuxième fichier
     * @return boolean
     * @throws IOException
     */
    public static boolean filesEquals (String filename1, String filename2) throws IOException {

        BufferedReader reader1 = new BufferedReader(new FileReader(filename1));
        BufferedReader reader2 = new BufferedReader(new FileReader(filename2));

        String line1 = reader1.readLine();
        String line2 = reader2.readLine();

        boolean areEqual = true;

        while (line1 != null || line2 != null) {
            if (line1 == null || line2 == null) {
                areEqual = false;
                break;
            } else if (!line1.equalsIgnoreCase(line2)) {
                areEqual = false;
                break;
            }

            line1 = reader1.readLine();
            line2 = reader2.readLine();
        }

        reader1.close();
        reader2.close();
        return areEqual;

    }

}
