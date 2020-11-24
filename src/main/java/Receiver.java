import java.net.*;
import java.io.*;

public class Receiver extends Thread {

    private Socket socket = null;
    private ServerSocket server = null;
    private DataInputStream in = null;
    private int port;
    private DataOutputStream out = null;
    public static final int WINDOW_SIZE = 7;
    private int frame_num;

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
        frame_num = 0;

        try{
            String input = "";
            int ack;

            //read from sender
            while(true){

                input = in.readUTF();
                System.out.println("RECEIVER frame receive: " + input);

                input = fm.handleInput(input);
                frameInput = new Frame(input);

                if (fm.containsError(input)) {          // TODO : vérifier si frameInput.getNum() == frame_num
                    System.out.println("frame contains error");
                    frameOutput = fm.getREJ(frame_num);
                } else {
                    frameOutput = fm.getFrameByType(frameInput.getType(), frameInput.getNum());
                    frame_num = (frame_num+1) % 8;  // numero du frame
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
