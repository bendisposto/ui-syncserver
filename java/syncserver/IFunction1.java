package syncserver;

public interface IFunction1<S,T> {
	T invoke(S v);
}