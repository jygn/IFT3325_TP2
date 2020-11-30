import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Random;

/**
 * Classe de tests pour la protocole Go-Back-N.
 * Cette classe permet d'effectuer des tests pendant la communication entre
 * l'émetteur et le destinataire. Parmis ces tests, ils y a en a qui simulent des
 * scénarios d'erreurs, comme la transmission d'un trame érronné, la perte d'un trame lors
 * de la transmission, etc.
 */
public class GBNTester {

    private BufferedWriter writer;
    private String fileOutputName;
    private static String fileInputName;

    /**
     * Création du fichier de input.
     * Chaque ligne de celui-ci représente les données d'un trame à envoyer au destinataire.
     * @param fileName nom du fichier
     * @param frames_nb nombre de trames à envoyer
     */
    public void createInputFile (String fileName, int frames_nb) {
        fileInputName = Sender.path + "/" + fileName;
        try {
            BufferedWriter b_writer = new BufferedWriter(new FileWriter(fileInputName));
            for (int i = 1; i <= frames_nb; i++) {
                b_writer.write("Frame data #" + i);
                b_writer.newLine();
            }

            b_writer.close();
        } catch (IOException e) {
            System.out.println("GNBTester input file not found");
        }
    }

    /**
     * Crée un fichier de output pour vérifier les données des trames reçues.
     * @param fileOutputName nom du fichier de output
     */
    public void setOutputFile(String fileOutputName) {
        this.fileOutputName = Sender.path + "/out/" + fileOutputName;
        try {
            writer = new BufferedWriter(new FileWriter(this.fileOutputName));
        } catch (IOException e) {
            System.out.println("GBNTest output file not found");
        }
    }

    /**
     * Convertie la ligne à écrire dans le fichier de string à bytes.
     * Écrie cette ligne dans le fichier de output.
     * @param data ligne à écrire
     */
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

    /**
     * Skip un trame dans la liste de trames pour la simulation du scénario où un trame est perdue.
     * @param windowIndex index dans la liste de trames
     * @return prochain index dans la lsite de trames
     */
    public int simulateFrameLost(int windowIndex){
        System.out.println("SENDER (I, 2, index " + windowIndex+" LOST)");
        windowIndex++;

        return windowIndex;
    }

    /**
     * Génère une erreur dans une trame soit en flippant un de ses bit en le choisissant
     * aléatoirement. Ainsi, on pourra simuler la scénario où un trame est erronée.
     * @param frame trame sans erreur
     * @param index index dans la liste de trames
     * @return trame avec erreur (en format binaire)
     */
    public String generateBitFlipError(Frame frame, int index) {
        Random random = new Random();
        System.out.println("SENDER (" + (char) frame.getType()+ ", "+
                frame.getNum() +", BIT FLIP index " + index + ")");
        String stringFrame = frame.toSendFormat();
        int max_bit_index = stringFrame.length()-(8 + 16); // w/o flag and CRC
        int ran_bit_index = random.nextInt(max_bit_index - 8) + 8;  // w/o flag
        return DataManipulation.bitFlip(stringFrame, ran_bit_index);   // flip a random bit
    }

    /**
     * Compare si le fichier de input est identique au fichier de output.
     * Si c'est le cas, le destinataire à bien reçu tous les données
     * que l'émetteur avait à envoyer.
     * @throws IOException
     */
    public void checkReceiverOutput() {

        try {
            if (Utils.filesEquals(fileInputName, this.fileOutputName))
                System.out.println("Receiver received all frames");
            else
                System.out.println("Receiver did'nt receive all frames");
        } catch (IOException e) {
        }
    }

    /**
     * Donne le l'écrivain du fichier de output.
     * @return
     */
    public BufferedWriter getWriter() { return this.writer; }

    public static void setFileInputName(String fileInputName) {
        GBNTester.fileInputName = fileInputName;
    }
}
