package com.mike.projectreactortest.general;

import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import reactor.core.publisher.BaseSubscriber;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import reactor.test.StepVerifierOptions;
import reactor.test.publisher.TestPublisher;

import java.time.Duration;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class SomeReactiveTestsExample {
    private final static String ERROR_MESSAGE = "Dummy_error_message";
    private final static String APPLE = "Apple";
    private final static String BANANA = "Banana";
    private final static String COCONUT = "Coconut";
    private final static String KIWI = "Kiwi";

    @Test
    public void expectNextShowcase() {
        Flux<String> fluxSource = fruitsFlux();

        StepVerifier.create(fluxSource)
                .expectNext(APPLE)
                .expectNextCount(2)
                .expectComplete()
                .log();
    }

    @Test
    public void expectNextMatchesPredicateShowcase() {
        Flux<String> fluxSource = fruitsFlux();

        StepVerifier.create(fluxSource)
                .expectSubscription()
                .expectNext(APPLE)
                .expectNext(BANANA)
                .expectNextMatches(fruit -> fruit.startsWith("C"))
                .verifyComplete();
    }

    @Test
    public void expectNextWithConsumerShowcase() {
        Flux<String> fluxSource = fruitsFlux();

        StepVerifier.create(fluxSource)
                .expectNext(APPLE)
                .expectNext(BANANA)
                .consumeNextWith(fruit -> {
                    if (!BANANA.equals(fruit)) {
                        throw new AssertionError("Test fail");
                    }
                })
                .verifyComplete();
    }

    @Test
    public void thenRequestShowcase() {
        Flux<String> fluxSource = fruitsFlux();

        StepVerifier.create(fluxSource, 1)
                .expectNext(APPLE)
                .thenRequest(1)
                .expectNext(BANANA)
                .thenRequest(1)
                .expectNextCount(1)
                .expectComplete()
                .verify();
    }

    @Test
    public void StepVerifierExceptionThrowShowcase() {
        Flux<String> fluxSource = fruitsFlux();

        Assertions.assertThrows(AssertionError.class, () -> StepVerifier.create(appendError(fluxSource))
                .expectNext(APPLE)
                .expectNext(COCONUT)
                .expectNext(COCONUT)
                .verifyComplete(), "StepVerifier should throw 'AssertionError'");
    }

    @Test
    public void expectErrorMessageShowcase() {
        Flux<String> fluxSource = fruitsFlux();

        StepVerifier.create(appendError(fluxSource))
                .expectNext(APPLE)
                .expectNext(BANANA)
                .expectNext(COCONUT)
                .expectErrorMessage(ERROR_MESSAGE)
                .verify();
    }

    @Test
    public void expectErrorMessageMatchesPredicateShowcase() {
        Flux<String> fluxSource = fruitsFlux();

        StepVerifier.create(appendError(fluxSource))
                .expectNext(APPLE)
                .expectNextSequence(List.of(BANANA, COCONUT))
                .expectErrorMatches(throwable -> throwable instanceof IllegalArgumentException &&
                        throwable.getMessage().equals(ERROR_MESSAGE))
                .verify();
    }

    @Test
    public void stepVerifierOptionsShowcase() {
        final String scenarioName = "Scenario_A";
        StepVerifierOptions options = StepVerifierOptions.create()
                .initialRequest(3)
                .scenarioName(scenarioName);

        StepVerifier stepVerifier = StepVerifier
                .create(fruitsFlux(), options)
                .expectNext(APPLE)
                .as("First fruit")
                .expectNext(BANANA)
                .as("Second fruit")
                .expectNext(BANANA)
                .expectComplete()
                .log();

        AssertionError exception = Assertions.assertThrows(AssertionError.class, stepVerifier::verify);

        Assertions.assertTrue(exception.getMessage().contains(scenarioName));
    }

    @Test
    public void postExecutionAssertionsShowcase() {
        final long sleepDuration = 1000;

        Flux<String> fluxSource = Flux.create(e -> {
            e.next(APPLE);
            e.next(BANANA);
            e.next(COCONUT);
            e.complete();
            try {
                Thread.sleep(sleepDuration);
            } catch (InterruptedException ex) {
                Assertions.fail("Received exception: ", ex);
            }
            e.next(KIWI);
        });

        StepVerifier.create(fluxSource)
                .expectNextCount(2)
                .expectNext(COCONUT)
                .expectComplete()
                .verifyThenAssertThat()
                .hasDropped(KIWI)
                .tookLessThan(Duration.ofMillis(sleepDuration -555));
    }

    @Test
    public void timeBasedPublisherShowcase() {
        StepVerifier.withVirtualTime(() -> Flux.interval(Duration.ofSeconds(1)).take(2))
                .expectSubscription()
                .expectNoEvent(Duration.ofSeconds(1))
                .expectNext(0L)
                .thenAwait(Duration.ofSeconds(1))
                .expectNext(1L)
                .verifyComplete();
    }

    @Test
    public void testPublisherShowcase() {
        final TestPublisher<String> testPublisher = TestPublisher.create();
        UppercaseConverter uppercaseConverter = new UppercaseConverter(testPublisher.flux());

        StepVerifier.create(uppercaseConverter.getUpperCase())
                .then(() -> testPublisher.emit(APPLE, BANANA, COCONUT))
                .expectNext("APPLE")
                .expectNextCount(2)
                .verifyComplete();
    }

    @Test
    public void testBackpressureShowcase() {
        Flux<Integer> limit = Flux.range(1, 25);
        limit.limitRate(10);

        StepVerifier.create(limit)
                .expectSubscription()
                .thenRequest(15)
                .expectNextCount(10)
                .expectNextCount(5)
                .thenRequest(10)
                .expectNextCount(10)
                .verifyComplete();
    }

    @Test
    public void testCancelCallbackShowcase() {
        final Flux<String> fluxSource = fruitsFlux();
        final AtomicBoolean isCanceled = new AtomicBoolean(false);

        fluxSource.doOnCancel(() -> isCanceled.set(true)).doOnComplete(() -> {
        }).subscribe(new BaseSubscriber<>() {
            @Override
            protected void hookOnNext(@NotNull String value) {
                request(1);
                if (value.equals(COCONUT)) {
                    cancel();
                }
            }
        });

        StepVerifier.create(fluxSource)
                .expectNext(APPLE)
                .expectNext(BANANA)
                .expectNext(COCONUT)
                .expectComplete()
                .verify();

        Assertions.assertTrue(isCanceled.get());
    }

    private <T> Flux<T> appendError(Flux<T> source) {
        return source.concatWith(Mono.error(new IllegalArgumentException(ERROR_MESSAGE)));
    }

    private Flux<String> fruitsFlux() {
        return Flux.fromIterable(List.of(APPLE, BANANA, COCONUT));
    }

    private class UppercaseConverter {
        private final Flux<String> source;

        public UppercaseConverter(Flux<String> source) {
            this.source = source;
        }

        Flux<String> getUpperCase() {
            return source.map(String::toUpperCase);
        }
    }
}
