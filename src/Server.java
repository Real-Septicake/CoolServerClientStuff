import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;

public class Server extends Thread{
    static ServerSocket serverSocket;
    static List<ClientHandler> clients;

    public Server(int port){
        try{
            serverSocket = new ServerSocket(port);
            clients = Collections.synchronizedList(new ArrayList<>());
            this.start();
        }catch(IOException ioe){
            ioe.printStackTrace();
        }
    }

    public void run(){
        while(true){
            try{
                Socket client = serverSocket.accept();
                ClientHandler c = new ClientHandler(client, generateID());
                clients.add(c);
                c.start();
            }catch(IOException ioe){
                ioe.printStackTrace();
            }
        }
    }

    public static long generateID(){
        return Math.round((new Random().nextDouble() + new Random().nextDouble())*Math.pow(10, 15));
    }

    public static void main(String[] args) {
        new Server(6665);
    }
}

class ClientHandler extends Thread{
    final Socket CLIENT;
    final ClientID ID;

    ObjectOutputStream out;
    ObjectInputStream in;

    public ClientHandler(Socket client, long id){
        CLIENT = client;
        ID = new ClientID(Constants.NAMES.get((int)(Math.random()*Constants.NAMES.size())), id);
        try{
            (out = new ObjectOutputStream(client.getOutputStream())).writeObject(new Shell(ID, Constants.ID_FLAG));
            out.flush();
            in = new ObjectInputStream(client.getInputStream());
        }catch(IOException ioe){
            ioe.printStackTrace();
        }
    }

    @Override
    public void run() {
        Shell buffer;
        while(true){
            try{
                buffer = (Shell) in.readObject();
                new SendShell(buffer);
            }catch(IOException | ClassNotFoundException e){
                e.printStackTrace();
            }
        }
    }
}

class SendShell extends Thread {
    final Shell INFO;

    public SendShell(Shell info){
        INFO = info;
        this.start();
    }

    @Override
    public void run() {
        for(ClientHandler c : Server.clients){
            try{
                c.out.writeObject(INFO);
                c.out.flush();
            }catch(IOException e){
                e.printStackTrace();
            }
        }
    }
}
