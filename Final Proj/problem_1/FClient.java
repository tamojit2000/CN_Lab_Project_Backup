import java.net.*;
import java.io.*;
import java.util.*;

public class FClient {

	public static void main(String[] args) {

    	DatagramSocket cs = null;
   	 	FileOutputStream fos = null;

   	 	try {

   		 	cs = new DatagramSocket();

			byte[] rd, sd;
			String reply;
			DatagramPacket sp,rp;
			int count=0;
			boolean end = false;

			// write received data into demoText1.html
			fos = new FileOutputStream("demoText1.html");
			String initiate = "REQUEST" + args[2] +"\r\n";
			sd=initiate.getBytes();
			sp=new DatagramPacket(
				sd,
				sd.length,
				InetAddress.getByName(args[0]),
				Integer.parseInt(args[1])
			);
			cs.send(sp);
			while(!end)
			{
				// get next consignment
				rd=new byte[1024];
				rp=new DatagramPacket(rd,rd.length);
				cs.receive(rp);

				// concat consignment
				reply=new String(rp.getData());
				String[] split = reply.split(" ");
				// send ACK
				String ack = "ACK "+(split[1]).trim()+" \r\n";
				sd=ack.getBytes();
				sp=new DatagramPacket(
					sd,
					sd.length,
					InetAddress.getByName(args[0]),
					Integer.parseInt(args[1])
				);
				cs.send(sp);
				if (reply.trim().endsWith("END")){
					end = true;
					break;
				}
				System.out.println(reply.substring(5+split[1].length(),reply.length()-2));
   			 	fos.write(reply.substring(5+split[1].length(),reply.length()-2).getBytes());

   			 	count++;
   		 	}
   	 	} catch (IOException ex) {
   		 	System.out.println(ex.getMessage());
   	 	} finally {

			try {
				if (fos != null)
					fos.close();
				if (cs != null)
					cs.close();
			} catch (IOException ex) {
				System.out.println(ex.getMessage());
			}
   	 	}
	}
}
