package syncserver;

import clojure.lang.*;
import clojure.java.api.Clojure;
import java.io.StringReader;
import java.util.concurrent.Callable;

public class Sync {
    
    
    
    private static IFn jmodify, jdelete, jchange, jcommit, conj, jconj;
    private PersistentVector tx; 
    
    static {
        IFn require = Clojure.var("clojure.core", "require");
        require.invoke(Clojure.read("syncserver.core"));
        jmodify = Clojure.var("syncserver.core", "jmodify");
        jdelete = Clojure.var("syncserver.core", "jdelete");
        jchange = Clojure.var("syncserver.core", "jchange");
        jconj = Clojure.var("syncserver.core", "jconj");
        jcommit = Clojure.var("syncserver.core", "commit");
        conj = Clojure.var("clojure.core","conj");
    }
    
    public Sync() {
        this.tx = Sync.emptyVector();
    }

    public static PersistentVector emptyVector() {
        return (PersistentVector) Clojure.read("[]");
    }

    public Sync change(Object value, String... path) {
        this.tx = (PersistentVector) conj.invoke(this.tx, jchange.invoke(value, path));
        return this;
    }
    
    public Sync addToVector(Object value, String... path) {
        this.tx = (PersistentVector) conj.invoke(this.tx, jconj.invoke(value, path));
        return this;
    }

    public Sync modify(ISyncFunction fn, String... path) {
        this.tx = (PersistentVector) conj.invoke(this.tx, jmodify.invoke(fn, path));
        return this;
    }
    
    public Sync delete(String... path) {
        this.tx = (PersistentVector) conj.invoke(this.tx, jdelete.invoke(path));
        return this;
    }
    
    public void commit() {
        jcommit.invoke(this.tx);
    }
    
    public static void main(String[] args) {
        new Sync().change(Sync.emptyVector(),"foo","bar")
        .addToVector(12,"foo","bar")
        .addToVector(14,"foo","bar")
        .addToVector(13,"foo","bar")
        .commit();  
    }
    

}
