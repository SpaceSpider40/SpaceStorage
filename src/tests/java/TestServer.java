import com.space.Commands;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TestServer {
    @Test
    void createModatCommand() {
        assertEquals(Commands.MODAT, Commands.fromString("___MODAT___"));
    }
}
