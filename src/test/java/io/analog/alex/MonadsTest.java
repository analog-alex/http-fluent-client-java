package io.analog.alex;


import io.analog.alex.functional.monads.Either;
import org.junit.jupiter.api.Test;

import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;


public class MonadsTest {
    @Test
    public void eitherTest() {
        final Exception exp = new Exception();

        // either with error
        Either<Exception, Object> testObjError = Either.left(exp);

        assertTrue(testObjError.isError());
        assertFalse(testObjError.isRight());
        assertEquals(exp, testObjError.getLeft().get());

        testObjError.applyLeft(e -> System.out.println(e.getClass().toGenericString()));

        // map left
        Either<Exception, Object> otherEitherError =
                testObjError.mapLeft(e -> new IllegalArgumentException());

        assertTrue(IllegalArgumentException.class.equals(otherEitherError.getLeft().get().getClass()));

        // -----

        final String response = "SUCCESS";

        Either<Exception, String> testObjSuccess = Either.right(response);

        assertFalse(testObjSuccess.isError());
        assertTrue(testObjSuccess.isRight());
        assertEquals(response, testObjSuccess.getRight().get());

        // map right
        Either<Exception, Integer> otherEitherSuccess =
                testObjSuccess.mapRight(str -> str.length());

        assertTrue(Integer.class.equals(otherEitherSuccess.getRight().get().getClass()));
    }

    @Test
    public void eitherPeekErrorTest() {
        Either<IllegalArgumentException, Integer> either = Either.left(new IllegalArgumentException());

        assertTrue(either.isError());
        assertEquals(IllegalArgumentException.class, either.peekError().getClass());
    }

    @Test
    public void eitherPeekErrorFailureTest() {
        Either<IllegalArgumentException, Integer> either = Either.right(1);

        assertFalse(either.isError());
        assertThrows(NoSuchElementException.class, () -> either.peekError());
    }

    @Test
    public void eitherSuccessfulTest() {
        Either<IllegalArgumentException, Integer> either = Either.right(1);

        assertTrue(either.isRight());
        assertEquals(1, either.successful());
    }

    @Test
    public void eitherSuccessfulFailureTest() {
        Either<IllegalArgumentException, Integer> either = Either.left(new IllegalArgumentException());

        assertFalse(either.isRight());
        assertThrows(NoSuchElementException.class, () -> either.successful());
    }

    @Test
    public void eitherApplyTest() {

        // if right

        Either<IllegalArgumentException, Integer> either = Either.right(1);

        either.applyRight(x -> assertEquals(1, x));
        either.applyLeft(System.err::println);

        either.apply(System.err::println, System.out::println);

        assertTrue(either.isRight());

        // if left

        either = Either.left(new IllegalArgumentException());

        either.applyRight(System.out::println);
        either.applyLeft(e -> assertEquals(IllegalArgumentException.class, e.getClass()));

        either.apply(System.err::println, System.out::println);

        assertTrue(either.isError());
    }

    @Test
    public void eitherExceptionTest() throws Exception {
        Either<Exception, Integer> left = Either.left(new IllegalArgumentException());

        assertFalse(left.isRight());
        assertTrue(left.isError());
        assertThrows(IllegalArgumentException.class, () -> left.attemptRightThrowIfLeft());
    }
}
