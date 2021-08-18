import java.io.*;
import java.net.*;


public class Client {
    private static final double LOSS_RATE = 0.18;
    public static String ser_addr;
    public static int ser_port;
    public static DatagramSocket cs;

    public static void send(byte[] sd) throws IOException  {
        DatagramPacket sp = new DatagramPacket(sd, sd.length, InetAddress.getByName(ser_addr), ser_port);
        Client.cs.send(sp);
    }

    public static byte[] get_res() throws IOException  {
        byte[] rd = new byte[612];
        DatagramPacket rp = new DatagramPacket(rd, rd.length);
        Client.cs.receive(rp);
        Client.ser_port = rp.getPort();
        return rp.getData();
    }

    public static void main(String[] args) throws IOException {

        cs = new DatagramSocket();
        FileOutputStream fos = null;
        Client.ser_addr = args[0];
        Client.ser_port = Integer.parseInt(args[1]);
        try {
            int prevPacketNo = -1;

            fos = new FileOutputStream("rec_"+args[2]);
            String req = "REQUEST" + args[2] + "\r\n";
            System.out.println("Requesting Port: " + Client.ser_port);
            send(req.getBytes());

            while(true) {
                byte[] reply = get_res();
                System.out.println("Requesting Port: " + Client.ser_port);
                byte[] reply_prefix = new byte[100], reply_data = new byte[512];
                System.arraycopy(reply, 0, reply_prefix, 0, 100);
                System.arraycopy(reply, 100, reply_data, 0, 512);
                System.out.println("Req No: "+new String(reply_prefix).substring(3));
                int packetNo = Integer.parseInt(new String(reply_prefix).substring(3).trim());
                String ack = "ACK" + packetNo + "\r\n";


                //send(ack.getBytes());

                if (Math.random() > LOSS_RATE){
        						send(ack.getBytes());
        				}else{
        					System.out.println("Client not send acknowledgement");
        				}


                if ("END".equals(new String(reply_data).trim()))
                    break;
                if (packetNo == prevPacketNo)
                    continue;
                prevPacketNo = packetNo;

                int till = 0;
                for (int i = reply_data.length-1; i >= 0; i--) {

                    if ((int) reply_data[i] != 0) {
                        till = i+1;
                        break;
                    }
                }
                byte[] out_data = new byte[till];
                System.arraycopy(reply_data, 0, out_data, 0, till);
                fos.write(out_data);
            }

        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        } finally {
            cs.close();
            if (fos != null) fos.close();
        }
    }
}
