import com.space.config.Config;
import com.space.file.FileManager;
import org.junit.jupiter.api.Test;

import java.io.IOException;

public class TestFileManager {

    @Test
    void testFileManagerInit() throws IOException {
        Config.readConfig();

        FileManager fileManager = FileManager.getInstance();
    }
}

