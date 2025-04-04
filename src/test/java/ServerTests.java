import com.space.Commands;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.net.Socket;

import org.junit.jupiter.api.Assertions.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ServerTests {

    @Test
    void testModat() throws IOException {
        try (Socket socket = new Socket("localhost", 8080)) {

            System.out.println("Connected to: " + socket.getRemoteSocketAddress());

            OutputStream outputStream = socket.getOutputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            outputStream.write("1fb36001-44e2-4519-8639-f9c730087b8c\n".getBytes());

            assertEquals("___ESTABLISHED_CONNECTION___", reader.readLine());

            outputStream.write(Commands.MODAT.toString().getBytes());
            outputStream.write("/file/test.txt\n".getBytes());

            String tm =reader.readLine();

            System.out.println(tm);

            assertEquals(13, tm.length());
        }
    }
}
