import java.net.*;
import java.io.*;

public class Receiver extends Thread {

    private Socket socket = null;
    private ServerSocket server = null;
    private DataInputStream in = null;
    private int port;
    private DataOutputStream out = null;
    public static final int NUMBER_OF_FRAME = 8;
    private int expected_frame;
    private GBNTester tester;
    private String input;

    private FramesManager fm;
    private Frame frameInput;
    private Frame frameOutput;
    boolean REJ_sent;

    // variables globales pour le test d'un acquittement perdu causant un time out
    public static boolean acknowledgementLost = false;
    private boolean executeOnceAckLost = true;
    private int ackignore = Sender.WINDOW_SIZE; // for the simulation of ack lost and timeout

    public Receiver(int port){
        this.port = port;

        fm = new FramesManager();
        expected_frame = 0;
        REJ_sent = false;
        tester = new GBNTester();
        tester.setOutputFile("src/test/text/testOutput.txt");
        input = "";
    }

    public void initReceiverConnection(){
        try{
            server = new ServerSocket(port);
            socket = server.accept();
            in = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
            out = new DataOutputStream(new BufferedOutputStream(socket.getOutputStream()));
        } catch (IOException e){
            e.printStackTrace();
        }
    }

    public void run(){
        this.initReceiverConnection();

        try {
            // read from sender
            while(true){

                input = in.readUTF();
                input = fm.frameExtract(input);
                frameInput = new Frame(input);

                int frameInNum = frameInput.getNum();
                byte frameInType = frameInput.getType();

                if (Checksum.containsError(input) | (REJ_sent & (frameInNum != expected_frame))) {
                    continue; // discard the frame

                } else if (frameInput.getNum() != expected_frame & frameInType == 'I') {
                    System.out.println("                                          " +
                            "RECEIVER out of order! received frame #" + frameInNum +
                            ", expected : frame #" + expected_frame);
                    frameOutput = new Frame('R', expected_frame); // send rej frames out-of-order
                    REJ_sent = true;

                } else {

                    frameOutput = fm.getFrameByType(frameInType, frameInNum);

                    if (frameInType == 'I') {

                        //Simulate aknolegement lost + time out error
                        if (acknowledgementLost && ackignore > 0) {
                            ackLostSimulation();
                            tester.writeDataFrame(frameInput.getData());
                            continue;
                        }

                        expected_frame = (expected_frame + 1) % NUMBER_OF_FRAME;
                        tester.writeDataFrame(frameInput.getData());
                    }

                    if (frameInType == 'P') {
                        frameOutput = fm.getFrameByType(frameInType, expected_frame);
                    }

                    REJ_sent = false;
                }


                // send
                printInfos(frameOutput);
                out.writeUTF(frameOutput.toSendFormat());
                out.flush();

                if(frameOutput.getType() == 'F') break;
            }

            closeConnection();

        } catch (IOException e){
            e.printStackTrace();
        }
    }

    public void ackLostSimulation() {
        if(executeOnceAckLost){
            System.out.println("                                          " +
                    "RECEIVER (A, " + frameInput.getNum() + ", LOST)");
            executeOnceAckLost = false;
        } else {
            System.out.println("                                          " +
                    "RECEIVER (A, " + frameInput.getNum() + ", NOT SEND)");
        }

        expected_frame = (expected_frame + 1) % NUMBER_OF_FRAME;
        ackignore--;
    }

    public void printInfos(Frame frame) {
        System.out.println("                                          " +
                "RECEIVER (" + (char) frame.getType() + ", "+ frame.getNum() + ")");
    }


    public void closeConnection() {
        try{
            in.close();
            out.close();
            socket.close();
            server.close();

            tester.getWriter().close();
            tester.checkReceiverOutput();

            System.out.println("RECEIVER Closing connection");
        } catch(IOException e) {
            e.printStackTrace();
        }
    }

}
