public class Main {

    public static void main(String args[]){

        System.out.println("Program start");

        Receiver receiver = new Receiver(5000);
        receiver.start();

//        Sender sender = new Sender("127.0.0.1", 5000);
//        sender.run();
        SenderV2 senderV2 = new SenderV2("127.0.0.1", 5000);
        senderV2.sendFrames();

    }
}
