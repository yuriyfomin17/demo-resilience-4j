import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import lombok.SneakyThrows;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.function.Consumer;

public class DemoApp {
    public static void main(String[] args) throws InterruptedException {
        CircuitBreakerRegistry circuitBreakerRegistry = CircuitBreakerRegistry.ofDefaults();
        CircuitBreakerConfig circuitBreakerConfig = CircuitBreakerConfig.custom()
                .minimumNumberOfCalls(10)
                .failureRateThreshold(50)
                .slowCallDurationThreshold(Duration.of(5, ChronoUnit.SECONDS))
                .waitDurationInOpenState(Duration.of(5, ChronoUnit.SECONDS))
                .build();
        CircuitBreaker demoCircuitBreaker = circuitBreakerRegistry.circuitBreaker("demoCircuitBreaker", circuitBreakerConfig);
        Consumer<Integer> decorateRunnable = demoCircuitBreaker.decorateConsumer(DemoApp::badMethod);
        for (int i = 0; i < 100; i++) {
            try {
                System.out.print(i + ":");
                System.out.print("State:" + demoCircuitBreaker.getState());
                decorateRunnable.accept(i);
                Thread.sleep(500);
            } catch (Exception e) {
                Thread.sleep(500);
                System.out.println(e.getMessage());
            }
            System.out.println();
        }
    }

    @SneakyThrows
    private static void badMethod(int i) {
        System.out.println("Hello from bad method");
        if (i <= 5) {
            Thread.sleep(500);
            throw new RuntimeException("Exception");
        }
    }
}
