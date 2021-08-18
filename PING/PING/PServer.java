import java.net.*;
import java.io.*;
import java.util.*;
 
public class PServer {
	private static final double LOSS_RATE = 0.3;
	private static final double AVERAGE_DELAY = 100;
	public static void main(String[] args) throws Exception{
		
		int port = Integer.parseInt(args[0]);
		System.out.println("\n **** Ping Server is working on Port = " + port + " ****");
		DatagramSocket socket = new DatagramSocket(port);
		Random random = new Random();
		while (true) {
			DatagramPacket request = new DatagramPacket(new byte[1024], 1024);
			socket.receive(request);

			System.out.print(new String(request.getData()));

			if (random.nextDouble() < LOSS_RATE) {
				System.out.println("   Reply not sent.");
				continue;
			}

			Thread.sleep((int) ( random.nextDouble() * AVERAGE_DELAY));
			
			InetAddress clientHost = request.getAddress();
			int clientPort = request.getPort();
			byte[] buf = request.getData();
			DatagramPacket reply = new DatagramPacket(buf, buf.length, clientHost, clientPort);
			socket.send(reply);
			System.out.println("   Reply sent.");

		}
	}
}

