import java.net.*;
import java.io.*;
import java.util.ArrayList;

/**
 * Classe représentant l'émetteur dans un protocole Go-Back-N.
 * La connexion entre l'émetteur et le destinataire se fait à l'aide de l'API socket de Java.
 * Celle-ci permet une communication point-à-point entre ces 2 entités.
 */
public class Sender extends Thread{

    private String address = "";
    private int port;
    private Socket socket = null;
    private int connectionType;
    private DataInputStream in = null;
    private DataOutputStream out = null;
    private String fileName;

    private int windowMin; // inferior limit of the window
    private int windowMax; //upper limit of the window
    private int windowIndex; // at where we are in the list
    private boolean allFrameSent;
    private boolean closeConfirmation;
    private String input;
    private boolean poll_req;
    private GBNTester tester;

    private FramesManager fm;
    private ArrayList<Frame> framesList;
    private Frame frameInput;
    private Frame frameToSend;
    private String frameWithError;

    // Variables globales pour le simulation du scénario out-of-order (REJ)
    public static boolean frameLost_error = false;
    public static boolean bitFlip_error = false;

    public static final int NUMBER_OF_FRAME = 8; // 2^3
    public static final int WINDOW_SIZE = NUMBER_OF_FRAME - 1;// (2^3) - 1 = 7
    public static final int TIME_OUT_INTERVAL = 3; // 3 seconds time out in go-back-N
    public static final String path = new File("").getAbsolutePath();

    /**
     * Constructeur de l'émetteur
     * @param address adresse ip
     * @param port  port pour la communication avec le destinataire
     * @param fileName  nom du fichier utilisé pour l'envoi des frames
     * @param connectionType type de connection 0 = Go-Back-N
     */
    public Sender(String address, int port, String fileName, int connectionType){
        this.address = address;
        this.port = port;
        this.fileName = fileName;
        this.connectionType = connectionType;

        windowMin = 0; // inferior limit of the window
        windowMax = WINDOW_SIZE - 1; //upper limit of the window
        windowIndex = 0; // at where we are in the list
        allFrameSent = false;
        closeConfirmation = false;
        poll_req = false;

        tester = new GBNTester();
        // tester.createInputFile(fileName,30); //create a input file for tests
        GBNTester.setFileInputName(fileName);
    }

    /**
     * Établie la connexion entre l'émetteur et le destinataire.
     * Le l'émetteur envoi une requête pour la demande de connexion.
     */
    public void setupConnection() {

        Frame connectionFrame = new Frame('C', connectionType);
        send(connectionFrame.toSendFormat());

        try {
            // wait for confirmation of the connection
            frameInput = readInput();

            if(frameInput.getType() == 'A' && frameInput.getNum() == 0){
                System.out.println("SENDER connection go-back-N established");
            } else if (frameInput.getType() == 'F'){
                this.closeConnection();
                System.out.println("SENDER go-back-N connection NOT established");
                System.exit(0);
            } else {
                System.out.println("SENDER error in establishing connection");
                System.exit(0);
            }
            //set up time out exception after 3 seconds
            socket.setSoTimeout(TIME_OUT_INTERVAL * 1000);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Fonction principale de la classe sender, lorsque appelé le thread démarre.
     * L'émetteur suit le protocole Go-Back-N à l'aide des fenêtres gissantes.
     */
    public void run(){
        this.initSenderConnection();
        this.initFrames();  // initialise frames list to send
        setupConnection();  // establish connection

        // start to send all the data
        while (true) {

            while (windowIndex <= windowMax & !allFrameSent) {

                if (windowIndex >= framesList.size()) {    // check if all frame are sent
                    allFrameSent = true;
                    frameToSend = new Frame('F', 0);

                } else if (bitFlip_error & windowIndex==8) { // out of order error due to a frame with errors

                    frameWithError = tester.generateBitFlipError(framesList.get(windowIndex), windowIndex);
                    send(frameWithError);
                    windowIndex++;
                    bitFlip_error = false;
                    continue;

                } else {
                    frameToSend = framesList.get(windowIndex);
                }

                printInfos(frameToSend);
                send(frameToSend.toSendFormat());

                if (allFrameSent) { // close communication
                    System.out.println("SENDER All frames sent");
                    break;
                }

                // update window index
                windowIndex++;

                // simulation out of order error due to a frame lost
                if(frameLost_error && windowIndex == 10) {
                    windowIndex = tester.simulateFrameLost(windowIndex);
                    frameLost_error = false;
                }
            }

            try {
                frameInput = readInput();

                // do an action according to the input
                handleResponse(frameInput);

            } catch (SocketTimeoutException e) {
                System.out.println("SENDER TIMEOUT EXCEPTION");
                handleTimeOut();
            } catch (IOException e) {
                e.printStackTrace();
            }

            //close connection from server received
            if(closeConfirmation) {
                closeConnection();
                break;
            }
        }
    }

    /**
     * Envoi un poll request dû à une time out. On a pas reçu d'acquittement du destinataire dans les délais.
     * On envoi donc un poll request pour demander à celui-ci une confirmation de la trame qu'il est rendu.
     */
    public void handleTimeOut() {
        System.out.println("SENDER sending poll request...");
        frameToSend = new Frame('P', 0);
        send(frameToSend.toSendFormat());
        poll_req = true;
    }

    /**
     * Envoi une trame au destinataire
     * @param frame séquence de caractères
     */
    public void send(String frame) {
        try {
            out.writeUTF(frame);
            out.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Lit le input du destinataire
     * @return trame lue
     * @throws IOException
     */
    public Frame readInput() throws IOException {
        input = in.readUTF();
        return fm.frameExtract(input);
    }

    /**
     * Effectue une action spécifique prore au type de trame reçue du destinataire
     * @param frameInput trame reçue
     */
    public void handleResponse(Frame frameInput) {

        switch (frameInput.getType()){
            case 'A':
                if (poll_req) { // poll request : retransmission des frames
                    windowIndex=  nextWindowIndex(windowIndex, frameInput.getNum());
                    windowMin = newWindowMin(windowMin, frameInput.getNum());
                    windowMax = windowMin + (WINDOW_SIZE - 1);
                    System.out.println("SENDER Frames retransmission...");
                    poll_req = false;
                    if (allFrameSent)
                        allFrameSent = false;
                } else {
                    //update the window
                    windowMin = newWindowMin(windowMin, frameInput.getNum()); //shift the limit inferior of the window (
//                    System.out.println("SENDER windowMin: " + windowMin);

                    windowMax = windowMin + (WINDOW_SIZE - 1);
//                    System.out.println("SENDER windowMax: " + windowMax);
                }
                break;
            case 'R':
                windowIndex = previousWindowIndex(windowIndex, frameInput.getNum());  // frames retransmission
                windowMax = windowMin + WINDOW_SIZE-1;
                System.out.println("SENDER Frames retransmission...");
                if (allFrameSent)
                    allFrameSent = false;
                break;
            case 'F':
                closeConfirmation = true;
                break;
            default:
                System.out.println("SENDER frame contains error");
        }
    }

    /**
     * function that initiate a frame manager to transform all the data that we read into frames
     */
    public void initFrames(){
        System.out.println(path + "/" + fileName);
        byte[][] data = Utils.readLines(path + "/" + fileName);
        fm = new FramesManager();
        fm.createFramesList(data, NUMBER_OF_FRAME);
        framesList = fm.getFramesList();
    }

    /**
     * initialize the connection with the receiver
     */
    public void initSenderConnection() {
        try {
            socket = new Socket(address, port);
            in = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
            out = new DataOutputStream(new BufferedOutputStream(socket.getOutputStream()));

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Ferme la connexion entre de l'émetteur
     */
    public void closeConnection () {
        try {
            out.close();
            socket.close();
            in.close();
            System.out.println("SENDER sender close connection");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Find the next windowin after sender received an aknoledgement
     * @param windowMin
     * @param ack
     * @return
     */
    public int newWindowMin (int windowMin, int ack) {

        int indexWindowMin = windowMin%NUMBER_OF_FRAME;

        while (indexWindowMin != ack){
            windowMin++;
            indexWindowMin = (indexWindowMin + 1)%NUMBER_OF_FRAME;
        }

        return windowMin;
    }

    /**
     * Find what is the index to returned to after a REJ.
     * @param windowIndex
     * @param rej
     * @return
     */
    public int previousWindowIndex (int windowIndex, int rej){

        int index = (windowIndex%NUMBER_OF_FRAME) - 1;
        windowIndex--;

        while (index != rej){
            windowIndex--;
            index = (index == 0 ? (NUMBER_OF_FRAME-1) : index - 1)%NUMBER_OF_FRAME;
        }

        return windowIndex;
    }

    /**
     * Find the next window index
     * @param windowIndex
     * @param num
     * @return
     */
    private int nextWindowIndex(int windowIndex, int num) {

        int index = (windowIndex%NUMBER_OF_FRAME);

        while (index != num){
            windowIndex--;
            index = (index == 0 ? (NUMBER_OF_FRAME-1) : index)%NUMBER_OF_FRAME;
        }

        return windowIndex;
    }

    /**
     * Imprime les informations de l'envoi d'un trame
     * @param frame trame
     */
    private void printInfos (Frame frame) {
        System.out.println("SENDER (" + (char) frame.getType()+ ", "+
                frame.getNum() +", index " + (windowIndex) + ")");
    }


}
