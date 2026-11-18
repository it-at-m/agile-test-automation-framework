package ataf.mobile.utils;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Minimal HTTP double of an Appium server speaking the W3C WebDriver protocol. It answers the
 * commands used by the mobile module (new session, find element, element displayed, screenshot,
 * delete session) and records all requests, so tests can drive a real {@code AndroidDriver}
 * without a device.
 */
final class AppiumStubServer implements AutoCloseable {
    static final String SESSION_ID = "stub-session-1";
    static final String ELEMENT_ID = "stub-element-1";
    static final byte[] SCREENSHOT = Base64.getDecoder()
            .decode("iVBORw0KGgoAAAANSUhEUgAAAAEAAAABCAYAAAAfFcSJAAAADUlEQVR42mNkYPhfDwAChwGA60e6kgAAAABJRU5ErkJggg==");
    private static final String W3C_ELEMENT_KEY = "element-6066-11e4-a52e-4f735466cecf";

    private final HttpServer server;
    private final String basePath;
    private final List<RecordedRequest> requests = new CopyOnWriteArrayList<>();
    private final AtomicInteger missingElementResponses = new AtomicInteger();
    private volatile String newSessionError;

    /**
     * A request received by the stub.
     *
     * @param method the HTTP method.
     * @param path the request path.
     * @param body the request body.
     */
    record RecordedRequest(String method, String path, String body) {
    }

    AppiumStubServer(String basePath) throws IOException {
        this.basePath = basePath;
        this.server = HttpServer.create(new InetSocketAddress(InetAddress.getLoopbackAddress(), 0), 0);
        this.server.createContext("/", this::handle);
        this.server.start();
    }

    URL url() {
        try {
            return URI.create("http://127.0.0.1:" + server.getAddress().getPort() + basePath).toURL();
        } catch (MalformedURLException e) {
            throw new IllegalStateException(e);
        }
    }

    /**
     * Lets the next new-session requests fail like an Appium server that cannot reach the device.
     */
    void failNewSessionWith(String message) {
        this.newSessionError = message;
    }

    /**
     * Answers the next find-element requests with "no such element".
     */
    void answerNoSuchElement(int times) {
        missingElementResponses.set(times);
    }

    List<RecordedRequest> requests() {
        return List.copyOf(requests);
    }

    boolean received(String method, String relativePath) {
        return requests.stream().anyMatch(request -> request.method().equals(method) && request.path().equals(basePath + relativePath));
    }

    /**
     * Returns the capabilities of the last new-session request, merging {@code alwaysMatch} and the
     * first {@code firstMatch} entry.
     */
    Map<String, JsonElement> requestedCapabilities() {
        RecordedRequest newSession = requests.stream()
                .filter(request -> request.method().equals("POST") && request.path().equals(basePath + "/session"))
                .reduce((first, second) -> second)
                .orElseThrow(() -> new AssertionError("No new session request received: " + requests));
        JsonObject capabilities = JsonParser.parseString(newSession.body()).getAsJsonObject().getAsJsonObject("capabilities");
        Map<String, JsonElement> merged = new LinkedHashMap<>();
        if (capabilities.has("alwaysMatch")) {
            capabilities.getAsJsonObject("alwaysMatch").entrySet().forEach(entry -> merged.put(entry.getKey(), entry.getValue()));
        }
        if (capabilities.has("firstMatch") && !capabilities.getAsJsonArray("firstMatch").isEmpty()) {
            capabilities.getAsJsonArray("firstMatch").get(0).getAsJsonObject().entrySet().forEach(entry -> merged.put(entry.getKey(), entry.getValue()));
        }
        return merged;
    }

    @Override
    public void close() {
        server.stop(0);
    }

    private void handle(HttpExchange exchange) throws IOException {
        String method = exchange.getRequestMethod();
        String path = exchange.getRequestURI().getPath();
        String body = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
        requests.add(new RecordedRequest(method, path, body));

        String command = path.startsWith(basePath) ? path.substring(basePath.length()) : path;
        String session = "/session/" + SESSION_ID;
        if (!path.startsWith(basePath)) {
            respond(exchange, 404, error("unknown command", "Unknown base path: " + path));
        } else if (method.equals("POST") && command.equals("/session")) {
            if (newSessionError != null) {
                respond(exchange, 500, error("session not created", newSessionError));
            } else {
                respond(exchange, 200, "{\"value\":{\"sessionId\":\"" + SESSION_ID
                        + "\",\"capabilities\":{\"platformName\":\"Android\",\"appium:automationName\":\"UiAutomator2\"}}}");
            }
        } else if (method.equals("DELETE") && command.equals(session)) {
            respond(exchange, 200, "{\"value\":null}");
        } else if (method.equals("GET") && command.equals(session + "/screenshot")) {
            respond(exchange, 200, "{\"value\":\"" + Base64.getEncoder().encodeToString(SCREENSHOT) + "\"}");
        } else if (method.equals("POST") && command.equals(session + "/element")) {
            if (missingElementResponses.getAndDecrement() > 0) {
                respond(exchange, 404, error("no such element", "An element could not be located on the page using the given search parameters."));
            } else {
                respond(exchange, 200, "{\"value\":{\"" + W3C_ELEMENT_KEY + "\":\"" + ELEMENT_ID + "\"}}");
            }
        } else if (method.equals("GET") && command.equals(session + "/element/" + ELEMENT_ID + "/displayed")) {
            respond(exchange, 200, "{\"value\":true}");
        } else {
            respond(exchange, 404, error("unknown command", "The stub does not support " + method + " " + path));
        }
    }

    private static String error(String error, String message) {
        return "{\"value\":{\"error\":\"" + error + "\",\"message\":\"" + message + "\",\"stacktrace\":\"\"}}";
    }

    private static void respond(HttpExchange exchange, int status, String json) throws IOException {
        byte[] response = json.getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().add("Content-Type", "application/json; charset=utf-8");
        exchange.sendResponseHeaders(status, response.length);
        try (OutputStream output = exchange.getResponseBody()) {
            output.write(response);
        }
    }
}
