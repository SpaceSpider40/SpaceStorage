import com.space.Commands;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.java.Log;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.net.Socket;

import static org.junit.jupiter.api.Assertions.*;

public class TestClients {

    @Data
    @AllArgsConstructor
    private static class ConnectionResult {

        public ConnectionResult() throws
                IOException,
                InterruptedException
        {
            this.socket = new Socket("localhost", 8080);
            this.bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));

            write(bufferedWriter, "1fb36001-44e2-4519-8639-f9c730087b8c");
            String response = read(bufferedReader);
            assertEquals(response, Commands.ESTABLISHED_CONNECTION.toString());
        }

        private Socket socket;
        private BufferedReader bufferedReader;
        private BufferedWriter bufferedWriter;
    }

    private static final int s = 100;

    private static void write(BufferedWriter writer, String msg) throws
            IOException,
            InterruptedException
    {
        writer.write(msg);
        writer.newLine();
        writer.flush();
        Thread.sleep(s);
    }

    private static String read(BufferedReader reader) throws
            IOException
    {
        return reader.readLine()
                     .strip()
                     .replace("\n", "");
    }

    @Test
    void testModat() throws
            IOException,
            InterruptedException
    {
        try (Socket socket = new Socket("localhost", 8080)) {

            BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));

            write(writer, "1fb36001-44e2-4519-8639-f9c730087b8c");

            String r = reader.readLine();

            Thread.sleep(s);
            assertEquals("___ESTABLISHED_CONNECTION___", r.strip());

            write(writer, Commands.MODAT.toString());
            write(writer, "1fb36001-44e2-4519-8639-f9c730087b8c");
            write(writer, "/test.txt");

            String tm = reader.readLine()
                              .strip()
                              .replace("\n", "");

            if (tm.equals(Commands.ERROR.toString())) {
                String err = reader.readLine()
                                   .strip()
                                   .replace("\n", "");
                assertNotEquals(401, Long.parseLong(err), "Vault doesn't exists");

                fail("Unexpected error: " + Long.valueOf(err));
            }

            assertEquals(13, tm.length());
        }
    }

    @Test
    void TestVaultCreation() throws
            IOException,
            InterruptedException
    {
        ConnectionResult connectionResult = new ConnectionResult();

        write(connectionResult.bufferedWriter, Commands.VAULT_CREATE.toString());
        write(connectionResult.bufferedWriter, "creation_test_1");
        String uuid = read(connectionResult.bufferedReader);

        if (uuid.equals(Commands.ERROR.toString())) {
            fail("Unexpected error: " + read(connectionResult.bufferedReader));
        }

        assertEquals(36, uuid.length(), "Malformed UUID");
    }
}
