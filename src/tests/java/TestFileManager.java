import com.space.config.Config;
import com.space.exceptions.VaultAlreadyExistsException;
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
            IOException, VaultAlreadyExistsException {
        Config.readConfig();

        FileManager fileManager = FileManager.getInstance();

        System.out.println(fileManager.CreateVault("Test 2"));
    }
}

