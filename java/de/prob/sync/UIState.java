package de.prob.sync;

import clojure.lang.*;
import clojure.java.api.Clojure;
import java.io.StringReader;
import java.util.concurrent.Callable;
import java.util.*;

public class UIState {
        
    public static IFn transact, delta, debug;
    
    static {
        IFn require = Clojure.var("clojure.core", "require");
        require.invoke(Clojure.read("syncserver.core"));
        transact = Clojure.var("syncserver.core", "transact");
        delta = Clojure.var("syncserver.core", "delta");
        debug = Clojure.var("syncserver.core", "set-debug");
    }
    
    public static Object transact(List tx) {
        return transact.invoke(tx);
    }
    

    public static String delta(Object s) {
        return (String) delta.invoke(s); 
    }

    public static void setDebug(Object x) {
      debug.invoke(x);
    }

}
