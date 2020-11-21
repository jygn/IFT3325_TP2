import java.net.*;
import java.io.*;

public class Receiver extends Thread {

    private Socket socket = null;
    private ServerSocket server = null;
    private DataInputStream in = null;
    private int port;
    private DataOutputStream out = null;
    public static final int WINDOW_SIZE = 7;

    public Receiver(int port){
        this.port = port;
    }

    public void initReceiverConnection(){
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

    public void run(){
        System.out.println("RECEIVER thread is running");
        this.initReceiverConnection();
        FramesManager fm = new FramesManager();
        Frame frameInput;
        Frame frameOutput = null;
        boolean allFrameReceived = false;

        try{
            String input = "";
            int ack;

            //read from sender
            receiver:
            while(true){
                input = in.readUTF();
                System.out.println("RECEIVER frame receive: " + input);
                input = fm.handleInput(input);

                if (fm.containsError(input)) {
                    System.out.println("This frame contains errors");
                    // TODO : REJ
                }

                frameInput = new Frame(input);

                //evaluate wich type of frame we receive
                switch (frameInput.getTypeInString()) {
                    case "I": //information
                        //ack is the number of the frame + 1
                        frameOutput = fm.getFrameAck(frameInput, WINDOW_SIZE);
                        break;
                    case "C": // Connection request
                        frameOutput = fm.getFrameConnectionConfirmation(frameInput);
                        break;
                    case "F": // end of communication
                        frameOutput = new Frame("F", 0);
                        allFrameReceived = true;
                    case "P": //  P bit
                        //TODO
                        break;
                    default:
                        //TODO throw error??
                }

                //send
                if(frameOutput != null){
                    out.writeUTF(fm.getFrameToSend(frameOutput));
                    out.flush();
                    System.out.println("RECEIVER response sent");
                } else {
                    //TODO throw error?
                    System.out.println("RECEIVER error frameOuput is null");
                }

                if(allFrameReceived) break;

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
            System.out.println("RECEIVER Closing connection");
        } catch(IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String args[]){
//        Receiver receiver = new Receiver(Integer.parseInt(args[0]));
    }
}
