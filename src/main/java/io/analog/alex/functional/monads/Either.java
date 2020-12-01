package io.analog.alex.functional.monads;

import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * An Either monad (functional programming style)
 * that represents either one value or another. Used as a return type for a Http call,
 * semantically representing either an Error or a successful {@link io.analog.alex.http.model.Response}.
 */
public class Either<L extends Throwable, R> {

    // ---------------
    // attributes
    private final Optional<L> left;
    private final Optional<R> right;

    // ---------------
    // initializers

    /**
     * Create LEFT either - semantically an error state
     *
     * @param value an object representing an error state
     * @return left Either
     */
    public static <L extends Throwable, R> Either<L, R> left(L value) {
        return new Either<>(Optional.of(value), Optional.empty());
    }

    /**
     * Create RIGHT either - semantically a success state
     *
     * @param <R>   the stored value type
     * @param value the stored value
     * @return right Either
     */
    public static <L extends Throwable, R> Either<L, R> right(R value) {
        return new Either<>(Optional.empty(), Optional.of(value));
    }


    // ---------------
    // private constructor
    private Either(Optional<L> l, Optional<R> r) {
        left = l;
        right = r;
    }

    // ----------------
    /* *
     * Methods & interface
     */

    /**
     * Get Left
     *
     * @return the left side of the Either
     */
    public Optional<L> getLeft() {
        return left;
    }

    /**
     * Get Right
     *
     * @return the right side of the Either
     */
    public Optional<R> getRight() {
        return right;
    }

    /**
     * Peek error - use if certain that the Either object is in error (left) state;
     * if not in error state, it returns a generic Error
     *
     * @return the left side
     * @throws NoSuchElementException if Either is not a left Either
     */
    public Throwable peekError() {
        if (left.isPresent()) {
            return left.get();
        }
        throw new NoSuchElementException("Either was not in Left state");
    }

    /**
     * Successful - use if certain that Either object is in success state;
     * if not in success state, it throws NoSuchElementException
     *
     * @return the right side
     */
    public R successful() {
        if (right.isPresent()) {
            return right.get();
        }
        throw new NoSuchElementException("Either was not in Right state");
    }

    /**
     * Test for error state
     *
     * @return Boolean
     */
    public Boolean isError() {
        return left.isPresent();
    }

    /**
     * Test for success state
     *
     * @return Boolean
     */
    public Boolean isRight() {
        return right.isPresent();
    }

    /**
     * Map the inner elements of the Either object by supplying two mapper functions
     * They will be applied on the direction (left|right) present
     *
     * @param <T>   the throwable type
     * @param lFunc the left side transform
     * @param rFunc the right side transform
     * @return the mapped Either
     */
    public <T> T map(Function<? super L, ? extends T> lFunc, Function<? super R, ? extends T> rFunc) {
        return left.<T>map(lFunc).orElseGet(() -> right.map(rFunc).get());
    }

    /**
     * Map the left element of the Either object, returning another Either of the type
     * Throwable the left components was mapped to.
     *
     * @param <T>   the throwable type
     * @param lFunc the left side transform
     * @return the mapped value
     */
    @SuppressWarnings("unchecked")
    public <T extends Throwable> Either<T, R> mapLeft(Function<? super L, ? extends T> lFunc) {
        return this.<Either<T, R>>map(t -> left(lFunc.apply(t)), t -> (Either<T, R>) this);
    }

    /**
     * Map the right element of the Either object, returning another Either of the type
     * the R the right components was mapped to.
     *
     * @param <T>   the throwable type
     * @param rFunc the left side transform
     * @return the mapped value
     */
    @SuppressWarnings("unchecked")
    public <T> Either<L, T> mapRight(Function<? super R, ? extends T> rFunc) {
        return this.<Either<L, T>>map(t -> (Either<L, T>) this, t -> right(rFunc.apply(t)));
    }

    /**
     * Apply a consumer to the left component, if present.
     *
     * @param lFunc the left side transform
     */
    public void applyLeft(Consumer<? super L> lFunc) {
        left.ifPresent(lFunc);
    }

    /**
     * Apply a consumer to the right component, if present.
     *
     * @param rFunc the right side transform
     */
    public void applyRight(Consumer<? super R> rFunc) {
        right.ifPresent(rFunc);
    }

    /**
     * Apply a consumer whatever elements (left|right) is present.
     *
     * @param lFunc the left side transform
     * @param rFunc the right side transform
     */
    public void apply(Consumer<? super L> lFunc, Consumer<? super R> rFunc) {
        left.ifPresent(lFunc);
        right.ifPresent(rFunc);
    }

    /**
     * Attempts to get the right elements throwing left if not present.
     *
     * @return the right side
     * @throws L throwable represented by the left side
     */
    public R attemptRightThrowIfLeft() throws L {
        if (this.left.isPresent())
            throw left.get();

        // SonarLint rules 
        return right.orElseThrow(NoSuchElementException::new);
    }
}
