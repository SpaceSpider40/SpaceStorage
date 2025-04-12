import com.space.Commands;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.net.Socket;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class TestClients {

    private static int s = 100;

    @Test
    void testModat() throws IOException, InterruptedException {
        try (Socket socket = new Socket("localhost", 8080)) {

            System.out.println("Connected to: " + socket.getRemoteSocketAddress());

            BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));

            writer.write("1fb36001-44e2-4519-8639-f9c730087b8c");
            writer.newLine();
            writer.flush();

            String r = reader.readLine();

            System.out.println(r);

            Thread.sleep(s);
            assertEquals("___ESTABLISHED_CONNECTION___", r.strip());
            writer.write(Commands.MODAT.toString());
            writer.newLine();
            writer.flush();
            Thread.sleep(s);
            writer.write("/file/test.txt");
            writer.newLine();
            writer.flush();
            Thread.sleep(s);

            String tm = reader.readLine();

            System.out.println(tm);

            assertEquals(13, tm.length());
        }
    }
}
