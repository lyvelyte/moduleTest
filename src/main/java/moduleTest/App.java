/*
 * This Java source file was generated by the Gradle 'init' task.
 */
package moduleTest;

import ch.bildspur.artnet.ArtNetBuffer;
import ch.bildspur.artnet.ArtNetClient;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Enumeration;
import java.util.Scanner;

public class App {

    public static void main(String[] args) throws SocketException {
        System.out.println("Sending DMX data...");

        byte[] dmxData_univ_01 = new byte[512];
        byte[] dmxData_univ_02 = new byte[512];
        byte[] dmxData_univ_03 = new byte[512];
        byte[] dmxData_univ_04 = new byte[512];
        byte[] dmxData_univ_05 = new byte[512];
        byte[] dmxData_univ_06 = new byte[512];
        byte[] dmxData_univ_07 = new byte[512];
        byte[] dmxData_univ_08 = new byte[512];

        int offset = 0;

        Enumeration<NetworkInterface> netInterfaces = NetworkInterface.getNetworkInterfaces();
        ArrayList<NetworkInterface> netInterfaceList = new ArrayList<NetworkInterface>();
        int nInterfaces = 0;
        while (netInterfaces.hasMoreElements()) {
            netInterfaceList.add(netInterfaces.nextElement());
            System.out.println("(" + nInterfaces + ") = " + netInterfaceList.get(nInterfaces).getDisplayName());
            nInterfaces++;
        }

//        Scanner interfaceScanner = new Scanner(System.in);
//        System.out.println("Select Network Interface: ");
//        String userInput = interfaceScanner.nextLine();
//        int choice = Integer.valueOf(userInput);
        int choice = 5;
        System.out.println("Selected " + netInterfaceList.get(choice).getDisplayName());

        NetworkInterface ni = netInterfaceList.get(choice);
        InetAddress address = ni.getInetAddresses().nextElement();

        ArtNetClient artnet = new ArtNetClient(new ArtNetBuffer(), 6454, 6454);
        artnet.start(address);

//        long startTime = System.currentTimeMillis();

        // send data to localhost
        boolean onFlag = false;
        int timeToRun = 99999999;
        int r_for_purple =  Math.round(255f*0.25f);

        for(int i = 0; i < timeToRun; i++){
            if(onFlag == false){
                // ========= Universe 01 ===============
                // A - RGBW
                for (int j = 0; j < 63; j++){
                    dmxData_univ_01[4*j+0] = (byte) r_for_purple;
                    dmxData_univ_01[4*j+1] = (byte) 0;
                    dmxData_univ_01[4*j+2] = (byte) 255;
                    dmxData_univ_01[4*j+3] = (byte) 0;
                }

//                // - Center Loop
//                offset = 63*4;
//                for (int j = 0; j < 1; j++){
//                    dmxData_univ_01[offset+3*j+0] = (byte) 255;
//                    dmxData_univ_01[offset+3*j+1] = (byte) 0;
//                    dmxData_univ_01[offset+3*j+2] = (byte) 0;
//                }

                // Top A - RGB
                offset = 63*4;
                for (int j = 0; j < 1; j++){
                    dmxData_univ_01[offset+3*j+0] = (byte) r_for_purple;
                    dmxData_univ_01[offset+3*j+1] = (byte) 0;
                    dmxData_univ_01[offset+3*j+2] = (byte) 255;
                }

                // Side A - RGB
                offset = 64*4;
                for (int j = 0; j < 1; j++){
                    dmxData_univ_01[offset+3*j+0] = (byte) r_for_purple;
                    dmxData_univ_01[offset+3*j+1] = (byte) 0;
                    dmxData_univ_01[offset+3*j+2] = (byte) 255;
                }

                // Back A - RGB
                offset = 65*4;
                for (int j = 0; j < 1; j++){
                    dmxData_univ_01[offset+3*j+0] = (byte) 255;
                    dmxData_univ_01[offset+3*j+1] = (byte) 0;
                    dmxData_univ_01[offset+3*j+2] = (byte) 0;
                }

                // ========= Universe 02 ===============
                // Left A Loop - RGBW
                for (int j = 0; j < 35; j++){
                    dmxData_univ_02[4*j+0] = (byte) r_for_purple;
                    dmxData_univ_02[4*j+1] = (byte) 0;
                    dmxData_univ_02[4*j+2] = (byte) 255;
                    dmxData_univ_02[4*j+3] = (byte) 0;
                }

                // Right A Loop - RGBW
                offset = 35*4;
                for (int j = 0; j < 35; j++){
                    dmxData_univ_02[offset+4*j+0] = (byte) r_for_purple;
                    dmxData_univ_02[offset+4*j+1] = (byte) 0;
                    dmxData_univ_02[offset+4*j+2] = (byte) 255;
                    dmxData_univ_02[offset+4*j+3] = (byte) 0;
                }

                // Left Eye - RGBW
                offset = 70*4;
                for (int j = 0; j < 35; j++){
                    dmxData_univ_02[offset+4*j+0] = (byte) r_for_purple;
                    dmxData_univ_02[offset+4*j+1] = (byte) 0;
                    dmxData_univ_02[offset+4*j+2] = (byte) 255;
                    dmxData_univ_02[offset+4*j+3] = (byte) 0;
                }

                // ========= Universe 03 ===============
                // Right Panel Top - RGBW
                for (int j = 0; j < 72; j++){
                    dmxData_univ_03[4*j+0] = (byte) r_for_purple;
                    dmxData_univ_03[4*j+1] = (byte) 0;
                    dmxData_univ_03[4*j+2] = (byte) 255;
                    dmxData_univ_03[4*j+3] = (byte) 0;
                }

                // Right A Loop - RGBW
                offset = 72*4;
                for (int j = 0; j < 50; j++){
                    dmxData_univ_03[offset+4*j+0] = (byte) r_for_purple;
                    dmxData_univ_03[offset+4*j+1] = (byte) 0;
                    dmxData_univ_03[offset+4*j+2] = (byte) 255;
                    dmxData_univ_03[offset+4*j+3] = (byte) 0;
                }

                // ========= Universe 04 ===============
                // Right Panel Loop - RGBW
                for (int j = 0; j < 98; j++){
                    dmxData_univ_04[4*j+0] = (byte) r_for_purple;
                    dmxData_univ_04[4*j+1] = (byte) 0;
                    dmxData_univ_04[4*j+2] = (byte) 255;
                    dmxData_univ_04[4*j+3] = (byte) 0;
                }

                // ========= Universe 05 ===============
                // Left Panel Loop - RGBW
                for (int j = 0; j < 98; j++){
                    dmxData_univ_05[4*j+0] = (byte) r_for_purple;
                    dmxData_univ_05[4*j+1] = (byte) 0;
                    dmxData_univ_05[4*j+2] = (byte) 255;
                    dmxData_univ_05[4*j+3] = (byte) 0;
                }

                // ========= Universe 06 ===============
                // Left Panel Top - RGBW
                for (int j = 0; j < 72; j++){
                    dmxData_univ_06[4*j+0] = (byte) r_for_purple;
                    dmxData_univ_06[4*j+1] = (byte) 0;
                    dmxData_univ_06[4*j+2] = (byte) 255;
                    dmxData_univ_06[4*j+3] = (byte) 0;
                }

                // ========= Universe 07 ===============
                // Light-O-Rama Controllers
                for (int j = 0; j < 512; j++){
                    dmxData_univ_07[j] = (byte) 255;
                }
            }else{
//                for (int j = 0; j < 512; j++){
//                    dmxData_univ_01[j] = (byte) 255;
//                }
            }

            if(onFlag){
                onFlag = false;
                System.out.println("Turning lights off.");
            }else{
                onFlag = true;
                System.out.println("Turning lights on.");
            }

            artnet.broadcastDmx(0, 1, dmxData_univ_01);
            artnet.broadcastDmx(0, 2, dmxData_univ_02);
            artnet.broadcastDmx(0, 3, dmxData_univ_03);
            artnet.broadcastDmx(0, 4, dmxData_univ_04);
            artnet.broadcastDmx(0, 5, dmxData_univ_05);
            artnet.broadcastDmx(0, 6, dmxData_univ_06);
            artnet.broadcastDmx(0, 7, dmxData_univ_07);
            artnet.broadcastDmx(0, 8, dmxData_univ_08);

            try{
                Thread.sleep(3000);
            }catch(Exception ignored){
                System.out.println("Failed to sleep?!");
                System.exit(-1);
            }
        }

        artnet.stop();
    }
}
