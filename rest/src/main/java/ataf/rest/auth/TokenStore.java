package ataf.rest.auth;

import java.util.Optional;

/**
 * Generic interface for storing and retrieving OAuth2 tokens.
 */
public interface TokenStore {
    /**
     * Retrieves a token for a given key.
     *
     * @param key Token identifier.
     * @return Optional containing {@link TokenInfo} if available.
     */
    Optional<TokenInfo> get(String key);

    /**
     * Stores or updates a token for a given key.
     *
     * @param key Identifier for the token.
     * @param token Token information to store.
     */
    void put(String key, TokenInfo token);

    /**
     * Removes a token from storage.
     *
     * @param key Token key to remove.
     */
    void clear(String key);
}
