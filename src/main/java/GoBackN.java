import java.util.Scanner;

public class GoBackN {

    private static Sender sender;
    private static Receiver receiver;

    public static void main(String args[]) throws InterruptedException {

        Scanner scanner = new Scanner(System.in);

        //ligne de commande
        System.out.println("\n" +
                "░██████╗░░█████╗░░░░░░░██████╗░░█████╗░░█████╗░██╗░░██╗░░░░░░███╗░░██╗\n" +
                "██╔════╝░██╔══██╗░░░░░░██╔══██╗██╔══██╗██╔══██╗██║░██╔╝░░░░░░████╗░██║\n" +
                "██║░░██╗░██║░░██║█████╗██████╦╝███████║██║░░╚═╝█████═╝░█████╗██╔██╗██║\n" +
                "██║░░╚██╗██║░░██║╚════╝██╔══██╗██╔══██║██║░░██╗██╔═██╗░╚════╝██║╚████║\n" +
                "╚██████╔╝╚█████╔╝░░░░░░██████╦╝██║░░██║╚█████╔╝██║░╚██╗░░░░░░██║░╚███║\n" +
                "░╚═════╝░░╚════╝░░░░░░░╚═════╝░╚═╝░░╚═╝░╚════╝░╚═╝░░╚═╝░░░░░░╚═╝░░╚══╝\n" +
                "\n" +
                "░██████╗██╗███╗░░░███╗██╗░░░██╗██╗░░░░░░█████╗░████████╗░█████╗░██████╗░\n" +
                "██╔════╝██║████╗░████║██║░░░██║██║░░░░░██╔══██╗╚══██╔══╝██╔══██╗██╔══██╗\n" +
                "╚█████╗░██║██╔████╔██║██║░░░██║██║░░░░░███████║░░░██║░░░██║░░██║██████╔╝\n" +
                "░╚═══██╗██║██║╚██╔╝██║██║░░░██║██║░░░░░██╔══██║░░░██║░░░██║░░██║██╔══██╗\n" +
                "██████╔╝██║██║░╚═╝░██║╚██████╔╝███████╗██║░░██║░░░██║░░░╚█████╔╝██║░░██║\n" +
                "╚═════╝░╚═╝╚═╝░░░░░╚═╝░╚═════╝░╚══════╝╚═╝░░╚═╝░░░╚═╝░░░░╚════╝░╚═╝░░╚═╝\n");

        while(true){
            String choice;
            System.out.println("Choose your scenario \n" +
                    "> 1. Normal communication (no error) \n" +
                    "> 2. Transmission lost (time out) \n" +
                    "> 3. Transmission error (bit flip) \n" +
                    "> 4. Tranmission lost and Transmission error ");

            System.out.print("> ");

            choice = scanner.nextLine();
            boolean isChoiceValid = false;

            while(!isChoiceValid) {
                switch (choice){
                    case "1":
                        isChoiceValid = true;
                        break;
                    case "2":
                        Sender.TimeOutError = true;
                        isChoiceValid = true;
                        break;
                    case "3":
                        //TODO
                        isChoiceValid = true;
                        break;
                    case "4":
                        Sender.TimeOutError = true;
                        //TODO
                        isChoiceValid = true;
                        break;
                    default:
                        System.out.println("select a scenario between 1 and 4");
                }
            }

            System.out.println("type commands");

            boolean isCommandsSenderDone = false;
            boolean isCommandReceiverDone = false;
            while(!isCommandsSenderDone || !isCommandReceiverDone){
                System.out.print(
                        "> "
                );

                choice = scanner.nextLine();
                String[] command = choice.split(" ");

                switch (command[0]){
                    case "Sender":
                        if(command.length != 5) {
                            System.out.println("Error : Sender need 4 arguments");
                            break;
                        }
                        sender = new Sender(command[1], Integer.parseInt(command[2]), command[3], Integer.parseInt(command[4]));
                        sender.start();
                        isCommandsSenderDone = true;
                        break;
                    case "Receiver":
                        if(command.length != 2) {
                            System.out.println("Error : Receiver need 1 arguments");
                            break;
                        }
                        receiver = new Receiver(Integer.parseInt(command[1]));
                        receiver.start();
                        isCommandReceiverDone = true;
                        break;
                    default :
                        System.out.println("Error: " + command[0] + " is not valid, please try again.");
                }
            }

//            receiver.join();
//            System.out.println("RECEIVER thread ends");
//            sender.join();
//            System.out.println("SENDER thread ends");
            //Sender 124.0.0.1 5000 src/test/text/test.txt 0

        }

    }

}

