package interfaces;

@FunctionalInterface
public interface ThrowingRunnable<R, E extends Exception> {
  R run() throws E;
}