import com.space.Commands;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.net.Socket;

import org.junit.jupiter.api.Assertions.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ServerTests {

    @Test
    void testModat() throws IOException, InterruptedException {
        try (Socket socket = new Socket("localhost", 8080)) {

            System.out.println("Connected to: " + socket.getRemoteSocketAddress());

            DataOutputStream dataOutputStream = new DataOutputStream(new BufferedOutputStream(socket.getOutputStream()));
            DataInputStream reader = new DataInputStream(new BufferedInputStream(socket.getInputStream()));

            dataOutputStream.writeUTF("1fb36001-44e2-4519-8639-f9c730087b8c");
            dataOutputStream.flush();
            String r = reader.readUTF();

            System.out.println(r);

            assertEquals("___ESTABLISHED_CONNECTION___", r.strip());
            dataOutputStream.writeUTF(Commands.EMPTY.toString());
            dataOutputStream.flush();
            dataOutputStream.writeUTF(Commands.EMPTY.toString());
            dataOutputStream.flush();
            dataOutputStream.writeUTF(Commands.MODAT.toString());
            dataOutputStream.flush();
            dataOutputStream.writeUTF("/file/test.txt");
            dataOutputStream.flush();

            while (true){
                System.out.println(reader.readUTF());
                System.out.flush();
                Thread.sleep(500);
            }
//            String tm = reader.readUTF();
//
//            System.out.println(tm);
//
//            assertEquals(13, tm.length());
        }
    }
}
