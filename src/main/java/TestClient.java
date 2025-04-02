import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

public class TestClient {
    public static void main(String[] args) throws IOException, InterruptedException {
//        for (int i = 0; i < 3; i++) {

            Socket socket = new Socket("localhost", 8080);

            Thread.sleep(1000);

            new Thread(() -> {

                try {

                    socket.getOutputStream().write("1fb36001-44e2-4519-8639-f9c730087b8c\n".getBytes());
                } catch (IOException e) {
//                        System.out.println(e.getMessage());
                }

                while (true) {
                    try {
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }

                    try {
                        socket.getOutputStream().write("Hello World!\n".getBytes());
                    } catch (IOException e) {
//                        System.out.println(e.getMessage());
                    }
                }
            }).start();
//        }
    }
}
