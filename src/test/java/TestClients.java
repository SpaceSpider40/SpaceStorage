import com.space.Commands;
import lombok.extern.java.Log;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.net.Socket;

import static org.junit.jupiter.api.Assertions.*;

public class TestClients {

    private static int s = 100;

    private static void write(BufferedWriter writer, String msg) throws
            IOException,
            InterruptedException {
        writer.write(msg);
        writer.newLine();
        writer.flush();
        Thread.sleep(s);
    }

    @Test
    void testModat() throws IOException, InterruptedException {
        try (Socket socket = new Socket("localhost", 8080)) {

            System.out.println("Connected to: " + socket.getRemoteSocketAddress());

            BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));

            write(writer, "1fb36001-44e2-4519-8639-f9c730087b8c");

            String r = reader.readLine();

            Thread.sleep(s);
            assertEquals("___ESTABLISHED_CONNECTION___", r.strip());

            write(writer, Commands.MODAT.toString());
            write(writer, String.valueOf(1));
            write(writer, "/test.txt");

            String tm = reader.readLine().strip().replace("\n", "");

            if (tm.equals(Commands.ERROR.toString())) {
                String err = reader.readLine().strip().replace("\n", "");
                assertNotEquals(401, Long.parseLong(err), "Vault doesn't exists");

                fail("Unexpected error: " + Long.valueOf(err));
            }

            assertEquals(13, tm.length());

        }
    }
}
