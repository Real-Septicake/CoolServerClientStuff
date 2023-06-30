import java.io.Serializable;

public class ClientInfo implements Serializable {
    final ClientID ID;
    int x, y, z;

    public ClientInfo(ClientID id, int x, int y, int z){
        ID = id;
        this.x = x;
        this.y = y;
        this.z = z;
    }
}
