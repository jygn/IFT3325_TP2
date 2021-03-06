import java.util.Scanner;

/**
 * Classe conetenant la fonction principale du programme.
 * Elle donne un interface utilisateur pour la simulation du protocole Go-Back-N sans ou avec erreurs.
 */
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
                    "> 2. Transmission lost (REJ) \n" +
                    "> 3. Transmission error (REJ) \n" +
                    "> 4. Aknowledgement lost (TIME OUT) \n" +
                    "> 5. Exit");

            boolean isChoiceValid = false;

            while(!isChoiceValid) {
                System.out.print("> ");
                choice = scanner.nextLine();
                switch (choice){
                    case "1":
                        isChoiceValid = true;
                        break;
                    case "2":
                        Sender.frameLost_error = true;
                        isChoiceValid = true;
                        break;
                    case "3":
                        Sender.bitFlip_error = true;
                        isChoiceValid = true;
                        break;
                    case "4":
                        Receiver.acknowledgementLost = true;
                        isChoiceValid = true;
                        break;
                    case "5":
                        System.exit(0);
                        break;
                    default:
                        System.out.println("select a scenario between 1 and 4");
                }
            }

            System.out.println("type commands (q to exit program) :");

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
                        try {
                            sender = new Sender(command[1], Integer.parseInt(command[2]), command[3], Integer.parseInt(command[4]));
                        } catch (NumberFormatException e) {
                            System.out.println("Error occured");
                            break;
                        }
                        sender.start();
                        isCommandsSenderDone = true;
                        break;
                    case "Receiver":
                        if(command.length != 2) {
                            System.out.println("Error : Receiver need 1 arguments");
                            break;
                        }
                        try {
                            receiver = new Receiver(Integer.parseInt(command[1]));
                        } catch (NumberFormatException e) {
                            System.out.println("Error occured");
                            break;
                        }
                        receiver.start();
                        isCommandReceiverDone = true;
                        break;
                    case "q":
                        System.exit(0);
                        break;
                    default :
                        System.out.println("Error: " + command[0] + " is not valid, please try again.");
                }
            }

            sender.join();
            receiver.join();

        }

    }

}

