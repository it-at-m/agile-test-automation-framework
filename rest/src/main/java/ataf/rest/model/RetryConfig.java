package ataf.rest.model;

import java.time.Duration;
import java.util.HashSet;
import java.util.Set;

/**
 * Configuration for HTTP request retry behavior with exponential backoff.
 *
 * <p>
 * This configuration is intended to be used by {@code RequestHelper} to control
 * how often and under which conditions an HTTP request is retried.
 * </p>
 */
public class RetryConfig {

    private final boolean enabled;
    private final int maxAttempts;
    private final long initialDelayMs;
    private final double backoffMultiplier;
    private final long maxDelayMs;
    private final boolean retryOnNetworkErrors;
    private final Set<Integer> retryStatusCodes;
    private final boolean idempotentMethodsOnly;

    private RetryConfig(Builder builder) {
        this.enabled = builder.enabled;
        this.maxAttempts = builder.maxAttempts;
        this.initialDelayMs = builder.initialDelayMs;
        this.backoffMultiplier = builder.backoffMultiplier;
        this.maxDelayMs = builder.maxDelayMs;
        this.retryOnNetworkErrors = builder.retryOnNetworkErrors;
        this.retryStatusCodes = Set.copyOf(builder.retryStatusCodes);
        this.idempotentMethodsOnly = builder.idempotentMethodsOnly;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public int getMaxAttempts() {
        return maxAttempts;
    }

    public long getInitialDelayMs() {
        return initialDelayMs;
    }

    public double getBackoffMultiplier() {
        return backoffMultiplier;
    }

    public long getMaxDelayMs() {
        return maxDelayMs;
    }

    public boolean isRetryOnNetworkErrors() {
        return retryOnNetworkErrors;
    }

    public Set<Integer> getRetryStatusCodes() {
        return retryStatusCodes;
    }

    public boolean isIdempotentMethodsOnly() {
        return idempotentMethodsOnly;
    }

    /**
     * Returns a disabled retry configuration.
     */
    public static RetryConfig disabled() {
        return new Builder()
                .enabled(false)
                .build();
    }

    /**
     * Returns a default retry configuration for idempotent HTTP methods:
     * <ul>
     * <li>enabled: true</li>
     * <li>maxAttempts: 3</li>
     * <li>initialDelayMs: 200ms</li>
     * <li>backoffMultiplier: 2.0</li>
     * <li>maxDelayMs: 2000ms</li>
     * <li>retryOnNetworkErrors: true</li>
     * <li>retryStatusCodes: 502, 503, 504</li>
     * <li>idempotentMethodsOnly: true</li>
     * </ul>
     */
    public static RetryConfig defaultIdempotent() {
        return new Builder()
                .enabled(true)
                .maxAttempts(3)
                .initialDelayMs(Duration.ofMillis(200).toMillis())
                .backoffMultiplier(2.0)
                .maxDelayMs(Duration.ofSeconds(2).toMillis())
                .retryOnNetworkErrors(true)
                .addRetryStatusCode(502)
                .addRetryStatusCode(503)
                .addRetryStatusCode(504)
                .idempotentMethodsOnly(true)
                .build();
    }

    public static class Builder {
        private boolean enabled = false;
        private int maxAttempts = 3;
        private long initialDelayMs = 200;
        private double backoffMultiplier = 2.0;
        private long maxDelayMs = 2000;
        private boolean retryOnNetworkErrors = true;
        private Set<Integer> retryStatusCodes = new HashSet<>();
        private boolean idempotentMethodsOnly = true;

        public Builder enabled(boolean enabled) {
            this.enabled = enabled;
            return this;
        }

        public Builder maxAttempts(int maxAttempts) {
            this.maxAttempts = maxAttempts;
            return this;
        }

        public Builder initialDelayMs(long initialDelayMs) {
            this.initialDelayMs = initialDelayMs;
            return this;
        }

        public Builder backoffMultiplier(double backoffMultiplier) {
            this.backoffMultiplier = backoffMultiplier;
            return this;
        }

        public Builder maxDelayMs(long maxDelayMs) {
            this.maxDelayMs = maxDelayMs;
            return this;
        }

        public Builder retryOnNetworkErrors(boolean retryOnNetworkErrors) {
            this.retryOnNetworkErrors = retryOnNetworkErrors;
            return this;
        }

        public Builder retryStatusCodes(Set<Integer> retryStatusCodes) {
            this.retryStatusCodes = retryStatusCodes != null ? retryStatusCodes : new HashSet<>();
            return this;
        }

        public Builder addRetryStatusCode(int statusCode) {
            this.retryStatusCodes.add(statusCode);
            return this;
        }

        public Builder idempotentMethodsOnly(boolean idempotentMethodsOnly) {
            this.idempotentMethodsOnly = idempotentMethodsOnly;
            return this;
        }

        public RetryConfig build() {
            return new RetryConfig(this);
        }
    }
}
