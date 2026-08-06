package ataf.core.xray;

import ataf.core.assertions.CustomAssertions;
import ataf.core.clients.JiraClient;
import ataf.core.helpers.AuthenticationHelper;
import ataf.core.logging.ScenarioLogManager;
import org.jetbrains.annotations.NotNull;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Represents a test case with associated attributes and methods for managing its status and
 * assignee within a Jira system. Natural ordering follows the stable Xray test-run ID and is
 * therefore consistent with {@link #equals(Object)}. Execution order is represented separately by
 * {@link #RANK}.
 */
public class Test implements Comparable<Test> {
    /**
     * Unique identifier for the test
     */
    public final int ID;

    /**
     * Jira issue key associated with the test
     */
    public final String ISSUE_KEY;

    /**
     * Rank of the test for sorting purposes
     */
    public final int RANK;

    private TestStatus status; // Current status of the test
    private final AtomicBoolean assignedOnce = new AtomicBoolean(false); // Indicates if the test is assigned

    /**
     * Constructs a Test instance with the specified attributes.
     *
     * @param id Unique identifier for the test.
     * @param issueKey Jira issue key associated with the test.
     * @param rank Rank of the test for sorting purposes.
     * @param status Initial status of the test.
     */
    public Test(int id, String issueKey, int rank, TestStatus status) {
        ID = id;
        ISSUE_KEY = issueKey;
        RANK = rank;
        this.status = status;
    }

    /**
     * Retrieves the current status of the test.
     *
     * @return the current status of the test.
     */
    public TestStatus getStatus() {
        return status;
    }

    /**
     * Updates the status of the test and communicates the change to the Jira system.
     *
     * <p>
     * If the update to the status fails, an error is logged.
     * </p>
     *
     * @param status the new status to be set for the test.
     */
    public void setStatus(TestStatus status) {
        try (JiraClient jiraClient = new JiraClient()) {
            // Execute HTTP request to update test status in Jira
            String response = jiraClient.executeHttpPutRequest(JiraClient.jiraXrayRestApiUrl() + "api/testrun/" + ID + "/status?status=" + status.NAME, "",
                    AuthenticationHelper.getAuthenticationMethod());
            CustomAssertions.assertEquals(jiraClient.getLastRequestStatusCode(), 200, response);
        } catch (Exception e) {
            ScenarioLogManager.getLogger().error("Setting of test status has failed!", e);
        }
        this.status = status; // Update the local status
    }

    /**
     * Assigns the current user as the assignee for the test if it is not already assigned.
     *
     * <p>
     * If the assignment fails, an error is logged.
     * </p>
     */
    public void assign() {
        if (assignedOnce.compareAndSet(false, true)) {
            try (JiraClient jiraClient = new JiraClient()) {
                final StringBuilder clearUserName = new StringBuilder();
                AuthenticationHelper.getUserName().access(clearUserName::append);
                String jiraName = clearUserName.toString();
                clearUserName.setLength(0); // Clear the StringBuilder

                // Execute HTTP request to assign the current user to the test
                String response = jiraClient.executeHttpPutRequest(
                        JiraClient.jiraXrayRestApiUrl() + "api/testrun/" + ID + "/assignee?user=" + URLEncoder.encode(jiraName,
                                StandardCharsets.UTF_8),
                        "", AuthenticationHelper.getAuthenticationMethod());

                CustomAssertions.assertEquals(jiraClient.getLastRequestStatusCode(), 200, response);
            } catch (Exception e) {
                ScenarioLogManager.getLogger().error("Setting of test assignee has failed!", e);
            }
        } else {
            ScenarioLogManager.getLogger().warn("Test ({}) has already been assigned!", ISSUE_KEY);
        }
    }

    /**
     * Compares this test to another test by its stable {@link #ID}. Rank is deliberately excluded
     * because two representations of the same Xray test run remain equal even if their rank or
     * other mutable execution data differs.
     *
     * @param test the other test to compare to.
     * @return a negative integer, zero, or a positive integer as this test should be ordered before,
     *         equal to, or after the specified test.
     */
    @Override
    public int compareTo(@NotNull Test test) {
        return Integer.compare(ID, test.ID);
    }

    /**
     * Indicates whether another object is equal to this test. Equality is based on the stable
     * Xray test-run {@link #ID}, not on {@link #RANK} (which is only unique within a single
     * execution).
     *
     * @param obj the object to compare with.
     * @return {@code true} if the other object is a {@code Test} with the same ID.
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof Test other)) {
            return false;
        }
        return this.ID == other.ID;
    }

    /**
     * Returns a hash code consistent with {@link #equals(Object)}.
     *
     * @return a hash code derived from {@link #ID}.
     */
    @Override
    public int hashCode() {
        return Objects.hash(ID);
    }
}
