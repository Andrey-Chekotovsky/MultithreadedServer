
import java.net.*;
import java.util.*;
import java.io.*;

public class ClientControler
{
    public static void main(String[] args)
    {
        try {
            Client client = new Client();
            client.runClient();
        }
        catch (IOException excp)
        {
            excp.printStackTrace();
        }
    }
    private static class Client {

        static private final String menu = """
                Menu
                1. Exit
                2. Print tours with accessible price
                3. Show tour information
                """;
        private final DataOutputStream out;
        private final DataInputStream in;
        private final Socket socket =  new Socket("127.0.0.1", 4445);
        public Client() throws IOException
        {
            in = new DataInputStream(socket.getInputStream());
            out = new DataOutputStream(socket.getOutputStream());
        }
        private void close()
        {
            try {
                socket.close();
                if (out != null)
                    out.close();
                if (in != null)
                    in.close();
            }
            catch(IOException excp)
            {
                excp.printStackTrace();
            }
        }
        public String readMasage() throws IOException
        {
            String str = null;
            try {
                str = in.readUTF();
            }
            catch (EOFException ignored) {}
            return str;
        }
        private void getToursWithLowerPrice(Scanner scanner){
            int price;
            String serverMasage;
            try {
                System.out.println("Enter price of tour:");
                price = scanner.nextInt();
                scanner.nextLine();
                out.writeUTF(Integer.toString(price));
                out.flush();
                serverMasage = readMasage();
                System.out.println(serverMasage);
            }
            catch(IOException excp)
            {
                excp.printStackTrace();
            }
        }
        private void getTourInfo(Scanner scanner)
        {
            String serverMasage;
            try {
                while (true)
                {
                    System.out.println("Write name of tour or End if you want to exit");
                    serverMasage = scanner.nextLine();
                    out.writeUTF(serverMasage);
                    out.flush();
                    if (serverMasage.equals("End"))
                        return;
                    System.out.print(readMasage());
                }
            }
            catch(IOException excp)
            {
                excp.printStackTrace();
            }
        }


        public void runClient() {
            int switcher = 0;
            try (Scanner scanner = new Scanner(System.in)) {
                while (switcher != 1) {
                    System.out.println(menu);
                    switcher = scanner.nextInt();
                    scanner.nextLine();
                    if (switcher > 0 && switcher < 4) {
                        out.writeUTF(Integer.toString(switcher));
                        out.flush();
                    }
                    switch (switcher) {
                        case 1:
                            break;
                        case 2:
                            getToursWithLowerPrice(scanner);
                            break;
                        case 3:
                            getTourInfo(scanner);
                            break;
                        default:
                            System.out.println("Incorrect input");

                    }
                }
            } catch (IOException excp) {
                excp.printStackTrace();
            } finally {
                close();
            }
        }
    }
}

