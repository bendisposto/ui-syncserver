import clojure.lang.*;
import clojure.java.api.Clojure;
import java.io.StringReader;

public class Sync {

	private static IFn jmodify, jdelete, jchange, jcommit;

	static {
	  IFn require = Clojure.var("clojure.core", "require");
      require.invoke(Clojure.read("syncserver.core"));
      jmodify = Clojure.var("syncserver.core", "jmodify");
      jdelete = Clojure.var("syncserver.core", "jdelete");
      jchange = Clojure.var("syncserver.core", "jchange");
      jcommit = Clojure.var("syncserver.core", "commit");  	
	}

	public static void change(Object value, String... path) {
		jchange.invoke(value, path);
	}

    public static void modify(Callable<Object> fn, String... path) {
		jmodify.invoke(fn, path);
	}

    public static void delete(String... path) {
		jdelete.invoke(path);
	}

	public static void commit() {
		jcommit.invoke();
	}

}