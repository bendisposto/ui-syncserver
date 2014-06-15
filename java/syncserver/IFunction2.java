package syncserver;
import java.util.Map;

public interface IFunction2<T> {
	T invoke(T v, Map state);
}