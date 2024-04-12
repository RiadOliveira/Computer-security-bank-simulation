package interfaces;

@FunctionalInterface
public interface ThrowingConsumer<R, T, E extends Exception> {
  R run(T t) throws E;
}
