import java.net.*;
import java.io.*;
import java.util.*;

public class FServer {

	public static void main(String[] args) {

		DatagramSocket ss = null;
		FileInputStream fis = null;
		DatagramPacket rp, sp;
		byte[] rd, sd;
		String reply;
		InetAddress ip;
		int port;

		try {
			ss = new DatagramSocket(Integer.parseInt(args[0]));

			System.out.println("Server is up....");
			// read file into buffer
			rd=new byte[100];
			sd=new byte[512];
			rp = new DatagramPacket(rd,rd.length);
			ss.receive(rp);
			ip = rp.getAddress();
			port =rp.getPort();
			reply=new String(rp.getData());
			String file_name = reply.substring(7, reply.length()).trim();
			fis = new FileInputStream(file_name);

			int consignment;
			String strConsignment;
			int result = 0; // number of bytes read
			int count = 0;
			ss.setSoTimeout(3000);
			while(true & result != -1){

				rd=new byte[100];
				sd=new byte[512];


				// prepare data
				result = fis.read(sd);
				if (result == -1) {
					sd = new String("RTD "+ count + " END \r\n").getBytes();
				} else {
					sd = new String("RTD "+ count+ " " + new String(sd) +" \r\n").getBytes();
				}
				System.out.println();


				while (true){
					sp=new DatagramPacket(sd,sd.length,ip,port);

					ss.send(sp);




					try{
							rp = new DatagramPacket(rd,rd.length);
							ss.receive(rp);
							break;
					}catch(Exception e){
						System.out.println(e.toString());
						System.out.println("Timeout as ackowledgemnt not received and resending.");
					}

				}



				// get client's consignment request from DatagramPacket
				ip = rp.getAddress();
				port =rp.getPort();
				System.out.println("Client IP Address = " + ip);
				System.out.println("Client port = " + port);

				String ack = new String(rp.getData());
				System.out.println(ack);
				//consignment = Integer.parseInt((ack.split(" ")[1]));
				//System.out.println("Client ACK = " + consignment);


				rp=null;
				sp = null;
				count++;
			}

		} catch (IOException ex) {
			System.out.println(ex.getMessage());

		} finally {
			try {
				if (fis != null)
					fis.close();
			} catch (IOException ex) {
				System.out.println(ex.getMessage());
			}
		}
	}
}
