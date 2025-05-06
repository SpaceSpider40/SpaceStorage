import com.space.config.Config;
import com.space.file.FileManager;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.UUID;

public class TestFileManager {

    @Test
    void testFileManagerInit() throws IOException {
        Config.readConfig();

        FileManager fileManager = FileManager.getInstance();
    }

    @Test
    void testCreateVault() throws
            IOException
    {
        Config.readConfig();

        FileManager fileManager = FileManager.getInstance();

        System.out.println(fileManager.CreateVault());
    }
}

