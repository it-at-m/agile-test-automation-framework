package ataf.core.clients;

/**
 * Factory interface used to create concrete {@link HttpClient} subclasses while reusing
 * the centralized proxy/PAC resolution logic in
 * {@link HttpClient#createForTarget(String, ClientFactory)}.
 *
 * @param <T> the concrete client type
 */
@FunctionalInterface
public interface ClientFactory<T extends HttpClient> {

    /**
     * Creates a client without any proxy configuration (direct connection).
     *
     * @return a direct client instance
     */
    T direct();

    /**
     * Creates a client configured via PAC (Proxy Auto-Config) for the given target URL.
     * <p>
     * Default implementation throws {@link UnsupportedOperationException}. Override if supported.
     * </p>
     *
     * @param pacUrl the PAC file URL
     * @param targetUrl the target URL used for PAC evaluation
     * @return a PAC-configured client instance
     */
    default T withPac(String pacUrl, String targetUrl) {
        throw new UnsupportedOperationException("PAC proxy is not supported by this client factory.");
    }

    /**
     * Creates a client configured with a direct proxy host and port.
     * <p>
     * Default implementation throws {@link UnsupportedOperationException}. Override if supported.
     * </p>
     *
     * @param proxyHostname the proxy hostname
     * @param proxyPort the proxy port
     * @return a proxy-configured client instance
     */
    default T withProxy(String proxyHostname, int proxyPort) {
        throw new UnsupportedOperationException("Direct proxy is not supported by this client factory.");
    }
}
