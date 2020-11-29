import java.util.ArrayList;

/**
 * Classe servant de manager pour les trames. Crée la liste des trames à envoyer,
 * donne un trame selon son type, etc..
 */
public class FramesManager {

    private ArrayList<Frame> framesList;

    /**
     * Crée la liste des trames à envoyer par l'émetteur.
     * @param data trames à envoyer sous forme de bytes
     * @param frameNumMax taille maximale d'un numéro de trame (2^(nombre de bits))
     */
    public void createFramesList (byte[][] data, int frameNumMax) {

        framesList = new ArrayList<>();
        byte type;
        int num;
        Frame f;
        for (int i = 0; i < data.length; i++) {
            type = 'I';
            num = i % frameNumMax;
            f = new Frame(type, num, data[i]);
            framesList.add(f);
        }
    }

    /**
     * Donne la liste de trames à envoyer
     * @return liste de trames
     */
    public ArrayList<Frame> getFramesList() { return this.framesList; }

    /**
     * Donne la trame si la connexion s'est bien établie ou non
     * @param frame_num numéro de la trame
     * @return trame
     */
    public Frame getFrameConnectionConfirmation(int frame_num) {

        // go-back-N request
        if(frame_num == 0){
            //send RR0 -> is waiting for the first frame
            return new Frame('A',0);

        } else { //not supported
            //send an end of communication
            return new Frame('F', 0);
        }
    }

    /**
     * Donne la bonne trame selon son type.
     * @param type type de la trame (byte)
     * @param frame_num numéro de la trame (entier)
     * @return trame
     */
    public Frame getFrameByType (byte type, int frame_num) {

        switch (type) {
            case 'I': // information
                // ack is the number of the frame + 1
                return new Frame('A', (frame_num + 1)%Sender.NUMBER_OF_FRAME);
            case 'C': // Connection request
                return getFrameConnectionConfirmation(frame_num);
            case 'F': // end of communication
                return new Frame('F', 0);
            case 'P':
                return new Frame('A', frame_num);
            default:
                return new Frame('R', frame_num);
        }
    }

    /**
     * Extracte le trame du format d'envoi: enlève les flags et le bit stuffing
     * @param input séquence de bits avec flags et bit stuffing
     * @return trame 
     */
    public Frame frameExtract (String input) {
        input = input.substring(8, input.length() - 8);    // without flags
        input = DataManipulation.bitUnStuffing(input);    // remove bit stuffing
        return new Frame(input);
    }

}
