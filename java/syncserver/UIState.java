package syncserver;

import clojure.lang.*;
import clojure.java.api.Clojure;
import java.io.StringReader;
import java.util.concurrent.Callable;
import java.util.*;

public class UIState {
        
    public static IFn transact, delta;
    
    static {
        IFn require = Clojure.var("clojure.core", "require");
        require.invoke(Clojure.read("syncserver.core"));
        transact = Clojure.var("syncserver.core", "transact");
        delta = Clojure.var("syncserver.core", "delta");
    }
    
    public static Object transact(Object state, List tx) {
        return transact.invoke(state, tx);
    }
    

    public static Object delta(Object s) {
        return delta.invoke(s); 
    }

}
