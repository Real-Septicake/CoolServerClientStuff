import javax.swing.*;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.HashMap;

public class Client {
    static Socket client;
    static ClientID id;
    static HashMap<ClientID, ClientInfo> infos;
    static ClientInfo self;

    public static void main(String[] args) throws IOException {
        infos = new HashMap<>();
        client = new Socket("127.0.0.1", 6665);
        new ReadThread().start();
        new UI(client.getOutputStream());
    }
}

class ReadThread extends Thread{
    ObjectInputStream in;
    Shell buffer;

    public ReadThread() {
        try{
            in = new ObjectInputStream(Client.client.getInputStream());
        }catch (IOException ioe){
            ioe.printStackTrace();
        }
    }

    @Override
    public void run() {
        while(true){
            try{
                buffer = (Shell) in.readObject();
                switch(buffer.FLAG){
                    case Constants.ID_FLAG -> {
                        Client.id = (ClientID) buffer.INFO;
                        Client.self = new ClientInfo((ClientID) buffer.INFO, 0, 0, 0);
                        System.out.println(((ClientID) buffer.INFO).ID+" "+ ((ClientID) buffer.INFO).NAME);
                    }
                    case Constants.INFO_FLAG -> {
                        ClientInfo info = (ClientInfo) buffer.INFO;
                        Client.infos.put(info.ID, info);
                        System.out.println("Client "+((info.ID.equals(Client.id))?"me :D":info.ID.NAME)+" has moved to x: "+info.x+", y: "+info.y+", z: "+info.z);
                    }
                }
            }catch(IOException | ClassNotFoundException e){
                e.printStackTrace();
            }
        }
    }
}

class UI {
    ObjectOutputStream out;

    public UI(OutputStream os){
        try{
            out = new ObjectOutputStream(os);
            start();
        }catch(IOException e){
            e.printStackTrace();
        }
    }

    private void start(){
        JFrame frame = new JFrame("Stanima");
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setLayout(new BoxLayout(frame.getContentPane(), BoxLayout.Y_AXIS));
        JTextField xInput = new JTextField(4);
        JTextField yInput = new JTextField(4);
        JTextField zInput = new JTextField(4);
        frame.add(new JLabel("X: "){{setAlignmentX(0.5f);}});
        frame.add(xInput);
        frame.add(new JLabel("Y: "){{setAlignmentX(0.5f);}});
        frame.add(yInput);
        frame.add(new JLabel("Z: "){{setAlignmentX(0.5f);}});
        frame.add(zInput);
        JButton button = new JButton("Send");
        button.addActionListener(e -> {
            try {
                out.writeObject(new Shell(new ClientInfo(Client.id,
                        Integer.parseInt(xInput.getText()),
                        Integer.parseInt(yInput.getText()),
                        Integer.parseInt(zInput.getText())
                ), Constants.INFO_FLAG));
                out.flush();

                xInput.setText("");
                yInput.setText("");
                zInput.setText("");
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        });
        frame.add(button);
        frame.pack();
        frame.setVisible(true);
    }
}
