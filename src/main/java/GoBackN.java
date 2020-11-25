import java.util.Scanner;

public class GoBackN {

    public static void main(String args[]){

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
            System.out.print(
                    "> "
            );

            String choice = scanner.nextLine();

            String[] command = choice.split(" ");

            switch (command[0]){
                case "Sender":
                    if(command.length != 5) {
                        System.out.println("Error : Sender need 4 arguments");
                        break;
                    }
                    Sender sender = new Sender(command[1], Integer.parseInt(command[2]), command[3], Integer.parseInt(command[4]));
                    sender.start();
                    break;
                case "Receiver":
                    if(command.length != 2) {
                        System.out.println("Error : Receiver need 1 arguments");
                        break;
                    }
                    Receiver receiver = new Receiver(Integer.parseInt(command[1]));
                    receiver.start();
                    break;
                default :
                    System.out.println("Error: " + command[0] + " is not valid, please try again.");
            }

        }

    }

}

