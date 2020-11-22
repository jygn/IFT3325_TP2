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
            while(true){

                input = in.readUTF();
                System.out.println("RECEIVER frame receive: " + input);

                input = fm.handleInput(input);
                frameInput = new Frame(input);

                if (fm.containsError(input)) {
                    System.out.println("frame contains error");
                    frameOutput = fm.getREJ(frameInput.getNum());
                } else {
                    frameOutput = fm.getFrameByType(frameInput, WINDOW_SIZE);
                }

                //send
                out.writeUTF(fm.getFrameToSend(frameOutput));
                out.flush();
                System.out.println("RECEIVER response sent");

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
            System.out.println("RECEIVER Closing connection");
        } catch(IOException e) {
            e.printStackTrace();
        }
    }

}
