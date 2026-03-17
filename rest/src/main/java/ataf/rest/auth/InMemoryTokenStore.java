package ataf.rest.auth;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Simple thread-safe in-memory implementation of {@link TokenStore}.
 * <p>
 * This implementation stores tokens in a concurrent hash map for the duration of
 * the JVM runtime. It is mainly intended for short-lived test executions or
 * single-threaded scenarios such as REST API integration tests.
 * <p>
 * For distributed or long-running environments, consider implementing a
 * persistent {@link TokenStore}, e.g., backed by a file, database, or cache service.
 */
public class InMemoryTokenStore implements TokenStore {

    /** Internal token cache mapping a unique key to {@link TokenInfo}. */
    private final Map<String, TokenInfo> CACHE = new ConcurrentHashMap<>();

    /**
     * Retrieves a stored token for the given key if present.
     *
     * @param key the unique token key (e.g. composed of grant type and client ID)
     * @return an {@link Optional} containing the corresponding {@link TokenInfo}
     *         if found; otherwise, an empty {@link Optional}
     */
    @Override
    public Optional<TokenInfo> get(String key) {
        return Optional.ofNullable(CACHE.get(key));
    }

    /**
     * Stores or replaces a {@link TokenInfo} for the given key.
     * <p>
     * If a token already exists for this key, it will be overwritten.
     *
     * @param key the unique token identifier
     * @param token the {@link TokenInfo} instance to store (must not be {@code null})
     */
    @Override
    public void put(String key, TokenInfo token) {
        CACHE.put(key, token);
    }

    /**
     * Removes the stored token for the specified key, if present.
     * <p>
     * This method is idempotent: calling it with a key that does not exist
     * has no effect.
     *
     * @param key the unique token key to remove
     */
    @Override
    public void clear(String key) {
        CACHE.remove(key);
    }
}
