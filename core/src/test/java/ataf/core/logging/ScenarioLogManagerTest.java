package ataf.core.logging;

import org.testng.Assert;
import org.testng.annotations.Test;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Tests for {@link ScenarioLogManager}.
 */
public class ScenarioLogManagerTest {

    /**
     * On a thread not named "main", the first {@link ScenarioLogManager#getLogger()} call
     * initializes {@code TestProperties}, whose static initializer logs through
     * {@code getLogger()} again. That nested call must not fail with "Recursive update" from
     * {@code ConcurrentHashMap.compute}. The ATAF classes are loaded in a separate class loader so
     * {@code TestProperties} is not yet initialized, whatever ran before in this JVM.
     */
    @Test(timeOut = 30000)
    public void shouldInitializeTestPropertiesFromFirstLoggerCallOnWorkerThread() throws Exception {
        URL coreClasses = ScenarioLogManager.class.getProtectionDomain().getCodeSource().getLocation();
        try (URLClassLoader isolatedLoader = new AtafFirstClassLoader(new URL[] { coreClasses }, getClass().getClassLoader())) {
            Method getLogger = Class.forName(ScenarioLogManager.class.getName(), true, isolatedLoader).getMethod("getLogger");
            AtomicReference<Throwable> failure = new AtomicReference<>();
            Thread worker = new Thread(() -> {
                try {
                    getLogger.invoke(null);
                } catch (InvocationTargetException e) {
                    failure.set(e.getCause());
                } catch (ReflectiveOperationException e) {
                    failure.set(e);
                }
            }, "scenario-log-manager-test-worker");

            worker.start();
            worker.join();

            Throwable thrown = failure.get();
            Assert.assertNull(thrown,
                    "First getLogger() call on a worker thread failed: " + thrown + (thrown == null ? "" : ", caused by " + thrown.getCause()));
        }
    }

    /**
     * Loads the ATAF classes itself instead of delegating to the parent, so they get initialized
     * from scratch. All other classes, such as Log4j, come from the parent.
     */
    private static final class AtafFirstClassLoader extends URLClassLoader {
        private AtafFirstClassLoader(URL[] urls, ClassLoader parent) {
            super(urls, parent);
        }

        @Override
        protected Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
            if (!name.startsWith("ataf.")) {
                return super.loadClass(name, resolve);
            }
            synchronized (getClassLoadingLock(name)) {
                Class<?> loadedClass = findLoadedClass(name);
                if (loadedClass == null) {
                    loadedClass = findClass(name);
                }
                if (resolve) {
                    resolveClass(loadedClass);
                }
                return loadedClass;
            }
        }
    }
}
