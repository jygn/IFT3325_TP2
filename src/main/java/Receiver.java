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

    public static boolean acknowledgementLost = false;


    public Receiver(int port){
        this.port = port;
    }

    public void initReceiverConnection(){
        try{
            server = new ServerSocket(port);
            socket = server.accept();
            //receive
            in = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
            out = new DataOutputStream(new BufferedOutputStream(socket.getOutputStream()));

            tester = new GBNTester();
            tester.setOutputFile("src/test/text/testOutput.txt");

        } catch (IOException i){
        System.out.println(i);
    }

    }

    public void run(){
        this.initReceiverConnection();
        FramesManager fm = new FramesManager();
        Frame frameInput;
        Frame frameOutput = null;
        expected_frame = 0;
        boolean REJ_sent = false;


        try{
            String input = "";
            boolean executeOnceAckLost = true;
            int ackignore = Sender.WINDOW_SIZE; // for the simulation of ack lost and timeout

            //read from sender
            while(true){

                input = in.readUTF();
                input = fm.frameExtract(input);
                frameInput = new Frame(input);

                if (fm.containsError(input) | (REJ_sent && frameInput.getNum() != expected_frame)) {
                    continue; // discard the frame
                } else if(frameInput.getNum() != expected_frame && frameInput.getType() == 'I'){
                    System.out.println("                                          " +
                            "RECEIVER out of order! received frame #" + frameInput.getNum() +
                            ", expected : frame #" + expected_frame);
                    frameOutput = fm.getREJ(expected_frame); // send rej because frames out-of-order
                    REJ_sent = true;
                } else {

                    frameOutput = fm.getFrameByType(frameInput.getType(), frameInput.getNum());

                    if (frameInput.getType() == 'I') {

                        //Simulate aknolegement lost + time out error
                        if (acknowledgementLost && ackignore >0) {

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
                            tester.writeDataFrame(frameInput.getData());
                            continue;
                        }

                        expected_frame = (expected_frame + 1) % NUMBER_OF_FRAME;

                        tester.writeDataFrame(frameInput.getData());

//                        System.out.println("RECEIVER RR : " + expected_frame);

                    } else if (frameInput.getType() == 'P') {
                        frameOutput = fm.getFrameByType(frameInput.getType(), expected_frame);
                        System.out.println("                                          " +
                                "RECEIVER RR poll : " + expected_frame);
                    }

                    REJ_sent = false;
                }

                System.out.println("                                          " +
                        "RECEIVER (" + (char) frameOutput.getType() + ", "+ frameOutput.getNum() + ")");
                //send
                out.writeUTF(fm.getFrameToSend(frameOutput));
                out.flush();

                if(frameOutput.getType() == 'F') break;

            }

            this.closeConnection();

        } catch (IOException e){
            e.printStackTrace();
        }

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
