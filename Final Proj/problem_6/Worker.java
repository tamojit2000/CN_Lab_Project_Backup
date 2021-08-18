import java.io.*;
import java.net.*;

public class Worker extends Thread {
    private static final double LOSS_RATE = 0.18;
    DatagramSocket client;
    int num;
    FileInputStream fis;
    InetAddress ip;
    int resPort;
    String file_name;
    public Worker(DatagramSocket client, String file_name, int num, InetAddress ip, int resPort) throws IOException {
        this.client = client;
        this.num = num;
        this.file_name = file_name;
        this.fis = new FileInputStream(file_name);
        this.ip = ip;
        this.resPort = resPort;
    }

    public void run() {
        System.out.println("Thread::Start::" + this.num);
        System.out.println("Transfering: "+this.file_name);
        int read = 0;

        try {
            int count = 0;
            client.setSoTimeout(2000);
            while (read != -1) {
                byte[] data = new byte[512], prefix = new byte[100];
                byte[] sd = new byte[612];
                read = fis.read(data);
                byte[] pref = new String("RTD" + count).getBytes();
                System.arraycopy(pref, 0, prefix, 0, pref.length);
                if (read == -1) {
                    data = new String("END").getBytes();
                }
                System.arraycopy(prefix, 0, sd, 0, prefix.length);
                System.arraycopy(data, 0, sd, 100, data.length);
                DatagramPacket sp = new DatagramPacket(sd, sd.length, ip, resPort);

                ////
                while (true){

        					if (Math.random()>LOSS_RATE){
        						client.send(sp);
        					}

        					try{
                    byte[] rd = new byte[512];
                    DatagramPacket rp = new DatagramPacket(rd, rd.length);
                    client.receive(rp);
        						break;
        					}
        					catch(SocketTimeoutException e){
        						System.out.println(e);
        						System.out.println("No acknowledgement received or packet lost resending");
        					}

        				}
                ////

                //client.send(sp);


                //byte[] rd = new byte[100];
                //DatagramPacket rp = new DatagramPacket(rd, rd.length);

                //client.receive(rp);

                count++;
            }
        } catch (IOException ex) {
			System.out.println(ex.getMessage());

        } finally {
            System.out.println("Thread::End::" + this.num);
			try {
				this.fis.close();
			} catch (IOException ex) {
				System.out.println(ex.getMessage());
			}
		}
    }
}
