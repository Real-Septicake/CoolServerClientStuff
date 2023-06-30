import java.io.Serializable;

public class Shell implements Serializable {
    public final String FLAG;
    public final Object INFO;

    public Shell(Object o, String flag){
        INFO = o;
        FLAG = flag;
    }
}
