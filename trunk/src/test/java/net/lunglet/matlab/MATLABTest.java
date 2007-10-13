package net.lunglet.matlab;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import com.sun.jna.Library;
import java.io.IOException;
import java.io.InputStream;
import org.junit.Test;

public final class MATLABTest {
    @Test
    public void testLibraries() {
        Library libeng = EngineLibrary.INSTANCE;
        Library libmx = MXLibrary.INSTANCE;
    }

    @Test
    public void testEngineOpenClose() {
        Engine engine = new Engine(false);
        assertFalse(engine.isVisible());
        engine.close();
        engine = new Engine(true);
        assertTrue(engine.isVisible());
        engine.setVisible(false);
        assertFalse(engine.isVisible());
        engine.close();
    }

    @Test
    public void testVersion() throws IOException {
        Engine engine = new Engine(false);
        InputStream stream = getClass().getResourceAsStream("version.m");
        assertNotNull(stream);
        engine.eval(stream);
        engine.close();
    }
}