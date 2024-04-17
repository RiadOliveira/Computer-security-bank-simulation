package interfaces;

@FunctionalInterface
public interface ThrowingConsumerTwoParameters<R, T, U, E extends Exception> {
    R run(T t, U u) throws E;
}
