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

    public static int getEdisonAddress(int[][] edison_table, int row, int col){
        if(row < 0 || col < 0){
            return 511;
        }
        return edison_table[row][col];
    }

    public static int[][] getEdisonAddressTable(){
        int n_rows = 4;
        int n_cols = 512;
        int[][] edisonAddresses  = new int[4][512];
        for(int i = 0; i < n_rows; i++){
            for(int j = 0; j < n_cols; j++){
                edisonAddresses[i][j] = 511;
            }
        }

        int row0_col = 0;
        int row1_col = 0;
        int row2_col = 0;
        int row3_col = 0;
        int address = 0;
        int lights_in_this_section = 0;

        // West side lights
        lights_in_this_section = 9;
        for(int i = 0; i < lights_in_this_section; i++){
            edisonAddresses[0][row0_col++] = address+lights_in_this_section-1-i;
        }
        address = address + lights_in_this_section;

        lights_in_this_section = 8;
        for(int i = 0; i < lights_in_this_section; i++){
            edisonAddresses[1][row1_col++] = address+lights_in_this_section-1-i;
        }
        address = address + lights_in_this_section;

        lights_in_this_section = 8;
        for(int i = 0; i < lights_in_this_section; i++){
            edisonAddresses[2][row2_col++] = address+lights_in_this_section-1-i;
        }
        address = address + lights_in_this_section;

        lights_in_this_section = 7;
        for(int i = 0; i < lights_in_this_section; i++){
            edisonAddresses[3][row3_col++] = address+lights_in_this_section-1-i;
        }
        address = address + lights_in_this_section;

        // Front Light section 1
        lights_in_this_section = 6;
        for(int i = 0; i < lights_in_this_section; i++){
            edisonAddresses[0][row0_col++] = address+i;
        }
        address = address + lights_in_this_section;

        lights_in_this_section = 5;
        for(int i = 0; i < lights_in_this_section; i++){
            edisonAddresses[1][row1_col++] = address+lights_in_this_section-1-i;
        }
        address = address + lights_in_this_section;

        lights_in_this_section = 5;
        for(int i = 0; i < lights_in_this_section; i++){
            edisonAddresses[2][row2_col++] = address+i;
        }
        address = address + lights_in_this_section;

        // Front light section 2
        lights_in_this_section = 6;
        for(int i = 0; i < lights_in_this_section; i++){
            edisonAddresses[0][row0_col++] = address + i;
        }
        address = address + lights_in_this_section;

        lights_in_this_section = 5;
        for(int i = 0; i < lights_in_this_section; i++){
            edisonAddresses[1][row1_col++] = address+lights_in_this_section-1-i;
        }
        address = address + lights_in_this_section;

        lights_in_this_section = 5;
        for(int i = 0; i < lights_in_this_section; i++){
            edisonAddresses[2][row2_col++] = address + i;
        }
        address = address + lights_in_this_section;

        // Front light section 3
        lights_in_this_section = 5;
        for(int i = 0; i < lights_in_this_section; i++){
            edisonAddresses[0][row0_col++] = address + i;
        }
        address = address + lights_in_this_section;

        lights_in_this_section = 6;
        for(int i = 0; i < lights_in_this_section; i++){
            edisonAddresses[1][row1_col++] = address+lights_in_this_section-1-i;
        }
        address = address + lights_in_this_section;

        lights_in_this_section = 5;
        for(int i = 0; i < lights_in_this_section; i++){
            edisonAddresses[2][row2_col++] = address + i;
        }
        address = address + lights_in_this_section;

        // Front light section 4
        lights_in_this_section = 5;
        for(int i = 0; i < lights_in_this_section; i++){
            edisonAddresses[0][row0_col++] = address + i;
        }
        address = address + lights_in_this_section;

        lights_in_this_section = 5;
        for(int i = 0; i < lights_in_this_section; i++){
            edisonAddresses[1][row1_col++] = address+lights_in_this_section-1-i;
        }
        address = address + lights_in_this_section;

        lights_in_this_section = 6;
        for(int i = 0; i < lights_in_this_section; i++){
            edisonAddresses[2][row2_col++] = address + i;
        }
        address = address + lights_in_this_section;

        // Front light section 5
        lights_in_this_section = 4;
        for(int i = 0; i < lights_in_this_section; i++){
            edisonAddresses[0][row0_col++] = address + i;
        }
        address = address + lights_in_this_section;

        lights_in_this_section = 4;
        for(int i = 0; i < lights_in_this_section; i++){
            edisonAddresses[1][row1_col++] = address+lights_in_this_section-1-i;
        }
        address = address + lights_in_this_section;

        lights_in_this_section = 4;
        for(int i = 0; i < lights_in_this_section; i++){
            edisonAddresses[2][row2_col++] = address + i;
        }
        address = address + lights_in_this_section;

        // Front light section 6
        lights_in_this_section = 16+9+4;
        for(int i = 0; i < lights_in_this_section; i++){
            edisonAddresses[0][row0_col++] = address+lights_in_this_section-1-i;
        }
        address = address + lights_in_this_section;

        lights_in_this_section = 7+16+2;
        for(int i = 0; i < lights_in_this_section; i++){
            edisonAddresses[1][row1_col++] = address+lights_in_this_section-1-i;
        }
        address = address + lights_in_this_section;

        lights_in_this_section = 14+11;
        for(int i = 0; i < lights_in_this_section; i++){
            edisonAddresses[2][row2_col++] = address+lights_in_this_section-1-i;
        }
        address = address + lights_in_this_section;

//        return address+5;

        address = address + 5;

        // Front light section 7
        row0_col = row0_col - 4;
        lights_in_this_section = 4;
        for(int i = 0; i < lights_in_this_section; i++){
            edisonAddresses[0][row0_col++] = address+lights_in_this_section-1-i;
//            edisonAddresses[0][row0_col++] = address+i;
        }
        address = address + lights_in_this_section;

        lights_in_this_section = 4;
        for(int i = 0; i < lights_in_this_section; i++){
            edisonAddresses[1][row1_col++] = address+lights_in_this_section-1-i;
        }
        address = address + lights_in_this_section;

        lights_in_this_section = 4;
        for(int i = 0; i < lights_in_this_section; i++){
            edisonAddresses[2][row2_col++] = address+lights_in_this_section-1-i;
        }
        address = address + lights_in_this_section;

        // Front light section 8
        address = address + 4;
        lights_in_this_section = 6;
        for(int i = 0; i < lights_in_this_section; i++){
            edisonAddresses[0][row0_col++] = address+lights_in_this_section-1-i;
        }
        address = address + lights_in_this_section;

        lights_in_this_section = 5;
        for(int i = 0; i < lights_in_this_section; i++){
            edisonAddresses[1][row1_col++] = address+lights_in_this_section-1-i;
        }
        address = address + lights_in_this_section;

        lights_in_this_section = 5;
        for(int i = 0; i < lights_in_this_section; i++){
            edisonAddresses[2][row2_col++] = address+lights_in_this_section-1-i;
        }
        address = address + lights_in_this_section;

        // Front light section 9
        lights_in_this_section = 4;
        for(int i = 0; i < lights_in_this_section; i++){
            edisonAddresses[0][row0_col++] = address+lights_in_this_section-1-i;
        }
        address = address + lights_in_this_section;

        lights_in_this_section = 6;
        for(int i = 0; i < lights_in_this_section; i++){
            edisonAddresses[1][row1_col++] = address+lights_in_this_section-1-i;
        }
        address = address + lights_in_this_section;

        lights_in_this_section = 6;
        for(int i = 0; i < lights_in_this_section; i++){
            edisonAddresses[2][row2_col++] = address+lights_in_this_section-1-i;
        }
        address = address + lights_in_this_section;

        // Right front section that was addressed weird...
        int offset0 = 11;
        edisonAddresses[0][row0_col+offset0-0] = 256;
        edisonAddresses[0][row0_col+offset0-1] = 257;
        edisonAddresses[0][row0_col+offset0-2] = 258;
        edisonAddresses[0][row0_col+offset0-3] = 260;
        edisonAddresses[0][row0_col+offset0-4] = 261;
        edisonAddresses[0][row0_col+offset0-5] = 263;
        edisonAddresses[0][row0_col+offset0-6] = 240;
        edisonAddresses[0][row0_col+offset0-7] = 244;
        edisonAddresses[0][row0_col+offset0-8] = 242;
        edisonAddresses[0][row0_col+offset0-9] = 243;
        edisonAddresses[0][row0_col+offset0-10] = 241;
        edisonAddresses[0][row0_col+offset0-11] = 245;

        int offset1 = 9;
        edisonAddresses[1][row1_col+offset1-0] = 262;
        edisonAddresses[1][row1_col+offset1-1] = 259;
        edisonAddresses[1][row1_col+offset1-2] = 264;
        edisonAddresses[1][row1_col+offset1-3] = 268;
        edisonAddresses[1][row1_col+offset1-4] = 270;
        edisonAddresses[1][row1_col+offset1-5] = 246;
        edisonAddresses[1][row1_col+offset1-6] = 247;
        edisonAddresses[1][row1_col+offset1-7] = 248;
        edisonAddresses[1][row1_col+offset1-8] = 255;
        edisonAddresses[1][row1_col+offset1-9] = 252;

        int offset2 = 9;
        edisonAddresses[2][row2_col+offset2-0] = 267;
        edisonAddresses[2][row2_col+offset2-1] = 269;
        edisonAddresses[2][row2_col+offset2-2] = 271;
        edisonAddresses[2][row2_col+offset2-3] = 265;
        edisonAddresses[2][row2_col+offset2-4] = 266;
        edisonAddresses[2][row2_col+offset2-5] = 250;
        edisonAddresses[2][row2_col+offset2-6] = 251;
        edisonAddresses[2][row2_col+offset2-7] = 249;
        edisonAddresses[2][row2_col+offset2-8] = 254;
        edisonAddresses[2][row2_col+offset2-9] = 253;

        // Right section that was addressed weird...
        edisonAddresses[0][row0_col+offset0+1] = 288;
        edisonAddresses[0][row0_col+offset0+2] = 296;
        edisonAddresses[0][row0_col+offset0+3] = 297;
        edisonAddresses[0][row0_col+offset0+4] = 298;
        edisonAddresses[0][row0_col+offset0+5] = 299;
        edisonAddresses[0][row0_col+offset0+6] = 300;
        edisonAddresses[0][row0_col+offset0+7] = 301;
        edisonAddresses[0][row0_col+offset0+8] = 302;
        edisonAddresses[0][row0_col+offset0+9] = 303;

        edisonAddresses[1][row1_col+offset1+1] = 280;
        edisonAddresses[1][row1_col+offset1+2] = 289;
        edisonAddresses[1][row1_col+offset1+3] = 290;
        edisonAddresses[1][row1_col+offset1+4] = 291;
        edisonAddresses[1][row1_col+offset1+5] = 292;
        edisonAddresses[1][row1_col+offset1+6] = 293;
        edisonAddresses[1][row1_col+offset1+7] = 294;
        edisonAddresses[1][row1_col+offset1+8] = 295;

        edisonAddresses[2][row2_col+offset2+1] = 272;   // ????
        edisonAddresses[2][row2_col+offset2+2] = 281;
        edisonAddresses[2][row2_col+offset2+3] = 282;
        edisonAddresses[2][row2_col+offset2+4] = 283;
        edisonAddresses[2][row2_col+offset2+5] = 284;
        edisonAddresses[2][row2_col+offset2+6] = 285;
        edisonAddresses[2][row2_col+offset2+7] = 287;
        edisonAddresses[2][row2_col+offset2+8] = 286;

        edisonAddresses[3][row3_col+1] = 273;
        edisonAddresses[3][row3_col+2] = 274;
        edisonAddresses[3][row3_col+3] = 279;
        edisonAddresses[3][row3_col+4] = 275;
        edisonAddresses[3][row3_col+5] = 276;
        edisonAddresses[3][row3_col+6] = 277;
        edisonAddresses[3][row3_col+7] = 278;

//        return 190;
        return edisonAddresses;
    }


    public static byte[] centerOutHelper(int[][] edison_addresses, byte[] dmx_univ, int clk_cnt){
        int c_row_0 = 47;
        int c_row_1 = 45;
        int c_row_2 = 45;

//        // Clear universe
//        for (int j = 0; j < 512; j++){
//            dmx_univ[j] = (byte) 0;
//        }

        // Determine which row/col to turn on.
        if(clk_cnt < 38){
            dmx_univ[getEdisonAddress(edison_addresses, 0, c_row_0+clk_cnt)] = (byte) 255;
            dmx_univ[getEdisonAddress(edison_addresses, 0, c_row_0-clk_cnt)] = (byte) 255;
            dmx_univ[getEdisonAddress(edison_addresses, 1, c_row_1+clk_cnt)] = (byte) 255;
            dmx_univ[getEdisonAddress(edison_addresses, 1, c_row_1-clk_cnt)] = (byte) 255;
            dmx_univ[getEdisonAddress(edison_addresses, 2, c_row_2+clk_cnt)] = (byte) 255;
            dmx_univ[getEdisonAddress(edison_addresses, 2, c_row_2-clk_cnt)] = (byte) 255;
        }else if(clk_cnt == 38 || clk_cnt == 39){
            dmx_univ[getEdisonAddress(edison_addresses, 0, c_row_0+clk_cnt)] = (byte) 255;
            dmx_univ[getEdisonAddress(edison_addresses, 0, c_row_0-clk_cnt)] = (byte) 255;

        }else if(clk_cnt < 48){
            dmx_univ[getEdisonAddress(edison_addresses, 0, c_row_0+clk_cnt+2-2)] = (byte) 255;
            dmx_univ[getEdisonAddress(edison_addresses, 0, c_row_0-clk_cnt-2+2)] = (byte) 255;
            dmx_univ[getEdisonAddress(edison_addresses, 1, c_row_1+clk_cnt-2)] = (byte) 255;
            dmx_univ[getEdisonAddress(edison_addresses, 1, c_row_1-clk_cnt+2)] = (byte) 255;
            dmx_univ[getEdisonAddress(edison_addresses, 2, c_row_2+clk_cnt-2)] = (byte) 255;
            dmx_univ[getEdisonAddress(edison_addresses, 2, c_row_2-clk_cnt+2)] = (byte) 255;

            int thirdRowOffset = clk_cnt - 40;
            dmx_univ[getEdisonAddress(edison_addresses, 3, 4-thirdRowOffset+2)] = (byte) 255;
            dmx_univ[getEdisonAddress(edison_addresses, 3, thirdRowOffset+10-2)] = (byte) 255;

        // Blink (on for 3, off for 3 cycles)
        } else if(clk_cnt == 48 || clk_cnt == 49 || clk_cnt == 50 || clk_cnt == 54 | clk_cnt == 55 || clk_cnt == 56 || clk_cnt == 60 || clk_cnt == 61 || clk_cnt == 62){
            // Turn on all but the aux lights
            for (int j = 0; j < 512; j++){
                if(j != 190 && j != 191) {
                    dmx_univ[j] = (byte) 255;
                }
            }
        } else if(clk_cnt == 51 || clk_cnt == 52 || clk_cnt == 53 || clk_cnt == 57 || clk_cnt == 58 || clk_cnt == 59){
            // Turn off all.
            for (int j = 0; j < 512; j++){
                dmx_univ[j] = (byte) 0;
            }
        }

        return dmx_univ;
    }

    public static void main(String[] args) throws SocketException {
//        int network_choice = 5;
        int network_choice = 11;
        String artnet_ip_addr = "192.168.1.3";

        byte[] dmxData_univ_01 = new byte[512];
        byte[] dmxData_univ_02 = new byte[512];
        byte[] dmxData_univ_03 = new byte[512];
        byte[] dmxData_univ_04 = new byte[512];

        Enumeration<NetworkInterface> netInterfaces = NetworkInterface.getNetworkInterfaces();
        ArrayList<NetworkInterface> netInterfaceList = new ArrayList<>();
//        int nInterfaces = 0;
        while (netInterfaces.hasMoreElements()) {
            netInterfaceList.add(netInterfaces.nextElement());
//            System.out.println("(" + nInterfaces + ") = " + netInterfaceList.get(nInterfaces).getDisplayName());
//            nInterfaces++;
        }
//        System.out.println("Selected " + netInterfaceList.get(choice).getDisplayName());

        NetworkInterface ni = netInterfaceList.get(network_choice);
        InetAddress address = ni.getInetAddresses().nextElement();

        ArtNetClient artnet = new ArtNetClient(new ArtNetBuffer(), 6454, 6454);
        artnet.start(address);

        long startTime = System.nanoTime();
        long waitTime = 50000000l;
        int cnt = 0;

        int [][] edison_addresses = getEdisonAddressTable();

        while(true){
            dmxData_univ_01 = centerOutHelper(edison_addresses, dmxData_univ_01, cnt);
            if(startTime + waitTime < System.nanoTime()){
                startTime = System.nanoTime();
                cnt = cnt + 1;
                if(cnt > 61){
                    cnt = 0;
                }
            }
            artnet.unicastDmx(artnet_ip_addr, 0, 0, dmxData_univ_01);
        }
    }
}
