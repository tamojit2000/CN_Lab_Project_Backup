import java.io.*;
import java.net.*;

public class Server {

    public static void main(String[] args) throws IOException {
        int port = Integer.parseInt(args[0]);
        DatagramSocket ss = new DatagramSocket(port);
        System.out.println("Server is up....");
        int count = 0;
        while(true)
        {
            byte[] rd = new byte[100];
            DatagramPacket rp = new DatagramPacket(rd, rd.length);
            ss.receive(rp);
            String reply = new String(rp.getData()).trim();
            InetAddress ip = rp.getAddress();
            int resPort = rp.getPort();
            Worker worker = new Worker(new DatagramSocket(port + count + 1), reply.substring(7), count, ip, resPort);
            worker.start();
            count++;
        }
    }
}
