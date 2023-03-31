import java.net.*;
import java.io.*;
import java.util.*;
import java.lang.*;
import org.apache.log4j.*;



public class Server {
    private static final Logger logger = Logger.getLogger(Server.class);
    final static private TouristOffer[] serverData = new TouristOffer[]{
            new TouristOffer("Tropical enigmerence", 499, 14, Transport.Ship),
            new TouristOffer("True power of mountins", 799, 14, Transport.Bus),
            new TouristOffer("Wild life without injuries", 599, 10, Transport.Plane),
            new TouristOffer("Magnifisent caribian journey", 699, 21, Transport.Ship),
            new TouristOffer("Breathtaken scineries of Gavana", 899, 14, Transport.Bus)};

    static ServerSocket serverSocket = null;
    static int numOfClients = 0;
    static File IDfile = new File("D://labs//CN//Multithreaded server//ID.txt");
    static int newClientID = 0;
    static public void log(String str)
    {
        logger.info(str);
        System.out.println(str);
    }

    static public void updateID() throws IOException
    {
        FileReader fileReader = new FileReader(IDfile);
        Scanner scanner = new Scanner(fileReader);
        newClientID = Integer.parseInt(scanner.nextLine());
        scanner.close();
    }

    static void changeIDFile() throws IOException
    {
        FileWriter fileWriter = new FileWriter(IDfile);
        fileWriter.write(Integer.toString(newClientID));
        fileWriter.close();
    }
    static void close()
    {
        if (serverSocket != null) {
            try {
                serverSocket.close();
                changeIDFile();
            }
            catch (IOException excp)
            {
                logger.error("IOExcepton");
                excp.printStackTrace();
            }
        }
    }

    static public void main(String[] args)
    {
        try{
            serverSocket = new ServerSocket(4445);
            serverSocket.setReuseAddress(true);
            updateID();
            while(true)
            {
                Socket client = serverSocket.accept();
                ClientHandler clientHandler = new ClientHandler(client);
                log(client.toString());
                log("Clients connected: " + numOfClients);
                new Thread(clientHandler).start();
            }
        }
        catch (IOException excp)
        {
            logger.error("IOExcepton");
            excp.printStackTrace();
        }
        finally {
            close();
        }
    }
    private static class ClientHandler implements Runnable {
        final private Socket clientSocket;
        final private DataOutputStream out;
        final private DataInputStream in;
        final private int clientID;
        // Constructor
        public ClientHandler(Socket socket) throws IOException
        {
            numOfClients++;
            clientID = newClientID;
            newClientID++;
            this.clientSocket = socket;
            out = new DataOutputStream(clientSocket.getOutputStream());
            in = new DataInputStream(clientSocket.getInputStream());
        }
        public String filterServerData(int price)
        {
            List<TouristOffer> dataForUser = new ArrayList<>(Arrays.asList(serverData.clone()));
            dataForUser.removeIf(touristOffer -> price < touristOffer.price);
            StringBuilder stringBuilder = new StringBuilder();
            for (TouristOffer touristOffer : dataForUser) {
                stringBuilder.append(touristOffer.getNameOfTour()).append("\n");
            }
            return new String(stringBuilder);
        }
        public void findAccessibleTours() throws IOException
        {
            int price;
            String serverAnswer;
            price = Integer.parseInt(readMassage());
            log("Client number " + clientID + " sent price = " + price);
            serverAnswer = filterServerData(price);
            out.writeUTF(serverAnswer);
            out.flush();
            log("Server answer for client number " + clientID +
                    ": " + serverAnswer);
        }

        public TouristOffer findOffer(String name)
        {
            for (TouristOffer touristOffer : serverData) {
                if (name.equals(touristOffer.getNameOfTour()))
                    return touristOffer;
            }
            return null;
        }
        public void checkInfo() throws IOException
        {
            String clientRequest;
            String serverAnswer;
            TouristOffer touristOffer;
            while(true)
            {
                clientRequest = readMassage();
                if (clientRequest.equals("End")) {
                    return;
                }
                log("Client number " + clientID + " checks info about " + clientRequest);
                touristOffer = findOffer(clientRequest);
                if (touristOffer != null)
                {
                    serverAnswer = touristOffer.toString();
                    out.writeUTF(serverAnswer);
                    out.flush();
                }
                else
                {
                    serverAnswer = "Tour with this name doesn't exist\n";
                    out.writeUTF(serverAnswer);
                    out.flush();
                }
                log("Server answer for client number " + clientID +
                        ": " + serverAnswer);
            }
        }
        private void close()
        {
            try {

                if (clientSocket != null)
                    clientSocket.close();
                if (out != null)
                    out.close();
                if (in != null)
                    in.close();
            }
            catch(IOException excp)
            {
                excp.printStackTrace();
            }
            finally {
                numOfClients--;
                log("Client number " + clientID + " exited");
                log("Clients connected: " + numOfClients);
            }
        }

        public String readMassage() throws IOException
        {
            String str = null;
            try {
                str = in.readUTF();
            }
            catch (EOFException ignored) {}
            return str;
        }
        @Override
        public void run() {
            int switcher = 0;
            try {
                while (switcher != 1)
                {
                    switcher = Integer.parseInt(readMassage());
                    switch (switcher) {
                        case 2 -> {
                            log("Client number " + clientID + " checking accessible tours");
                            findAccessibleTours();
                        }
                        case 3 -> {
                            log("Client number " + clientID + " checking info");
                            checkInfo();
                        }
                    }
                }
            }
            catch(IOException excp)
            {
                logger.error("IOExcepton");
                excp.printStackTrace();
            }
            finally {
                close();
            }
        }
    }

}
