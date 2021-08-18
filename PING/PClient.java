import java.net.*;
import java.io.*;
import java.util.*;

public class PClient {

	public static void main(String[] args) throws Exception{
		DatagramSocket cs = new DatagramSocket();
		cs.setSoTimeout(1000);
	    InetAddress ip=InetAddress.getByName(args[0]);
		int port = Integer.parseInt(args[1]);

		byte[] rd, sd;
		String reply;
		DatagramPacket sp, rp;
		int count = 0;
		while (count <= 5) {
			count++;

			Date d = new Date();
			String time = d + "";
			String data = "PING " + count + " " + time + " \r\n";
			rd = new byte[100];
			sd = data.getBytes();
			sp = new DatagramPacket(sd, sd.length, ip, port);
			rp = new DatagramPacket(rd, rd.length);
			System.out.println(ip);
			cs.send(sp);

			try {
				cs.receive(rp);
				reply = new String(rp.getData());
				System.out.println(reply);
			} catch (Exception ex) {
				System.out.println("timeout");
			}

		}
		cs.close();
	}

}
