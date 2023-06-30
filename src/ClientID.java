import java.io.Serializable;

public class ClientID implements Serializable {
    public final String NAME;
    public final long ID;

    public ClientID(String name, long id){
        NAME = name;
        ID = id;
    }
}
