package syncserver;

import clojure.lang.*;
import clojure.java.api.Clojure;
import java.io.StringReader;
import java.util.concurrent.Callable;

public class UIState {
        
    private static IFn jmodify, jdelete, jchange, jcommit, conj, jconj, delta;

    public final static PersistentVector EMPTY_VECTOR = (PersistentVector) Clojure.read("[]");
    private final static IPersistentMap EMPTY_MAP = (IPersistentMap) Clojure.read("{}");
    private final PersistentVector tx; 
    public final IPersistentMap state;
    
    static {
        IFn require = Clojure.var("clojure.core", "require");
        require.invoke(Clojure.read("syncserver.core"));
        jmodify = Clojure.var("syncserver.core", "jmodify");
        jdelete = Clojure.var("syncserver.core", "jdelete");
        jchange = Clojure.var("syncserver.core", "jchange");
        jconj = Clojure.var("syncserver.core", "jconj");
        jcommit = Clojure.var("syncserver.core", "commit");
        conj = Clojure.var("clojure.core","conj");
        delta = Clojure.var("syncserver.core", "delta");
    }
    
    public UIState() {
        this(UIState.EMPTY_MAP,UIState.EMPTY_VECTOR);
    }


    public UIState(IPersistentMap state, PersistentVector tx) {
        this.tx = tx;
        this.state = state;
    }

    public UIState change(Object value, String... path) {
        PersistentVector tx = (PersistentVector) conj.invoke(this.tx, jchange.invoke(value, path));
        return new UIState(this.state, tx);
    }
    
    public UIState addToVector(Object value, String... path) {
        PersistentVector tx = (PersistentVector) conj.invoke(this.tx, jconj.invoke(value, path));
        return new UIState(this.state, tx);
    }

    public UIState modify(IFunction1 fn, String... path) {
        PersistentVector tx = (PersistentVector) conj.invoke(this.tx, jmodify.invoke(fn, path));
        return new UIState(this.state, tx);
    }
    
    public UIState delete(String... path) {
        PersistentVector tx = (PersistentVector) conj.invoke(this.tx, jdelete.invoke(path));
        return new UIState(this.state, tx);
    }
    
    public UIState commit() {
        return new UIState((IPersistentMap)jcommit.invoke(this.state, this.tx), UIState.EMPTY_VECTOR);
    }
    
    public static void delta(UIState t1, UIState t2) {
        delta.invoke(t1.state,t2.state); 
    }

    public static void main(String[] args) {
        UIState t1 = new UIState().change(UIState.EMPTY_VECTOR,"foo","bar").commit();
        UIState t2 = t1.addToVector(12,"foo","bar")
                    .addToVector(14,"foo","bar")
                    .addToVector(13,"foo","bar")
                    .commit(); 
        delta(t1,t2);           
    }
    

}
