import java.io.*;
import java.net.Socket;

public class DataFlowController extends Thread {

    private Socket socket = null;
    private DataInputStream in = null;
    private DataOutputStream out = null;
    private int frame_sent;

    public DataFlowController (Socket socket) {

        try {
            //receive
            in = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
            //send
            out = new DataOutputStream(new BufferedOutputStream(socket.getOutputStream()));
            frame_sent = 0;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Listener
     */
    public void run () {
        try {
            while (true) {
                String data = in.readUTF();
                frame_sent--;
                System.out.println("ack : " + data);

            }

        } catch (IOException e) {
//            closeConnection();
        }
    }

    public void send(String data) {
        try {
            out.writeUTF(data);
            out.flush();
            System.out.println("frame sent : " + data);
            frame_sent++;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void closeConnection () {
        try {
            in.close();
            out.close();
            socket.close();
            System.out.println("Socket closed");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }



//        try{
//
//            String frame, ack = "";
//
//            //repeat as long as the client does not send a null string
//
//            //read from sender
//            while((!(frame = in.readUTF()).equals("end"))){
//                System.out.println("frame receive: " + frame);
//                ack = frame;
//
//                //send
//                out.writeUTF(ack);
//                out.flush();
//            }
//
//            System.out.println("Reciver Closing connection");
//            socket.close();
//            in.close();
////                ps.close();
//            out.close();
//            server.close();
//
//        } catch (IOException e){
//            e.printStackTrace();
//        }
//
//    } catch (IOException i){
//        System.out.println(i);
//    }


}
