import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {

    private static Socket socket = null;
    private static ServerSocket server = null;
    private static DataInputStream in = null;
    private static int port = 5000;
    private static DataOutputStream out = null;
    public static final int WINDOW_SIZE = 7;
    private static int expected_frame;

    public static void initReceiverConnection(){
        try{
            server = new ServerSocket(port);
            System.out.println("RECEIVER Server started");

            socket = server.accept();
            System.out.println("RECEIVER Sender accepted");

            //receive
            in = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
            out = new DataOutputStream(new BufferedOutputStream(socket.getOutputStream()));

        } catch (IOException i){
            System.out.println(i);
        }

    }

    public static void main(String[] args) {

            System.out.println("RECEIVER thread is running");
            initReceiverConnection();
            FramesManager fm = new FramesManager();
            Frame frameInput;
            Frame frameOutput = null;
            expected_frame = 0;
            boolean REJ_sent = false;

            try{
                String input = "";
                int ack;

                //read from sender
                while(true){

                    input = in.readUTF();
                    input = fm.frameExtract(input);
                    frameInput = new Frame(input);


                    if (fm.containsError(input) | (REJ_sent && frameInput.getNum() != expected_frame)) {
                        continue; // discard the frames
                    } else if (frameInput.getNum() != expected_frame && frameInput.getType() == 'I') {
                        System.out.println("RECEIVER receive frame #" + frameInput.getNum() + " = out-of-order " +
                                "-> Expected : frame # " + expected_frame);
                        frameOutput = fm.getREJ(expected_frame); // send rej because frames out-of-order
                        REJ_sent = true;
                    } else {

                        frameOutput = fm.getFrameByType(frameInput.getType(), frameInput.getNum());

                        if (frameInput.getType() == 'I') {

                            expected_frame = (expected_frame + 1) % WINDOW_SIZE;


                            if (Sender.TimeOutError & expected_frame == 1) {    // TODO timeout error bonne facon de faire ou faire dans sender?
                                Sender.TimeOutError = false;
                                continue;
                            }

                        } else if (frameInput.getType() == 'P') {
                            frameOutput = fm.getFrameByType((byte) 'I', expected_frame);
                            expected_frame = (expected_frame + 1) % WINDOW_SIZE;

                        }

                        REJ_sent = false;
                    }

                    //send
                    out.writeUTF(fm.getFrameToSend(frameOutput));
                    out.flush();

                    if(frameOutput.getType() == 'F') break;

                }

                closeConnection();

            } catch (IOException e){
                e.printStackTrace();
            }

        }

        public static void closeConnection() {
            try{
                in.close();
                out.close();
                socket.close();
                server.close();
                System.out.println("RECEIVER Closing connection");
            } catch(IOException e) {
                e.printStackTrace();
            }
        }
}

