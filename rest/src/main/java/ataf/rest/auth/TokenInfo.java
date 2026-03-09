package ataf.rest.auth;

import java.time.Instant;

/**
 * Represents a single access token with metadata such as type and expiration.
 *
 * @param accessToken The actual OAuth2 access token.
 * @param tokenType The type of token (typically "Bearer").
 * @param expiresAt The expiration timestamp.
 * @param refreshToken Optional refresh token, if available.
 */
public record TokenInfo(
        String accessToken,
        String tokenType,
        Instant expiresAt,
        String refreshToken) {

    /**
     * Determines whether the token is expired or close to expiration (within 30 seconds).
     *
     * @return {@code true} if expired or stale, otherwise {@code false}.
     */
    public boolean isExpiredOrStale() {
        return expiresAt == null || Instant.now().isAfter(expiresAt.minusSeconds(30));
    }
}
