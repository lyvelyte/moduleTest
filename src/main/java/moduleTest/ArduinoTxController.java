package moduleTest;

import jssc.SerialPort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Random;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.zip.CRC32;
import java.util.zip.Checksum;

public class ArduinoTxController {
    private static final Logger LOGGER = LoggerFactory.getLogger(ArduinoTxController.class);
    private static final int nRain = 17;
    private static final int bytesPerFrame = 35;
    private static final int DATA_SIZE = bytesPerFrame + 2;
    private static final int startMarker = 255;
    private static final int endMarker = 254;
    private static long frameNumber = 0;
    public boolean isConnected = false;
    private SerialPort serialPort;
    private int frameSkipCnt = 0;
    private boolean isShuttingDown = false;
    private static final Random random = new Random();

    private static final int nFrames = 200000;
    private static boolean swtichFlip = false;
    private static boolean swtichFlip2 = false;

    // Data Format
    // data[0] = startMarker
    // data[1] = Backdrop Brightness
    // data[2] = Brightness
    // data[3] = red
    // data[4] = green
    // data[5] = blue
    // data[6] = lightMode
    // data[7] = direction
    // data[8] = frameskip
    // data[9] = rain0
    // data[10] = rain1
    // data[11] = rain2
    // data[12] = rain3
    // data[13] = rain4
    // data[14] = rain5
    // data[15] = rain6
    // data[16] = rain7
    // data[17] = rain8
    // data[18] = rain9
    // data[19] = rain10
    // data[20] = rain11
    // data[21] = rain12
    // data[22] = rain13
    // data[23] = rain14
    // data[24] = rain15
    // data[25] = rain16
    // data[26] = orientation
    // data[27] = Backdrop light mode
    // data[28] = frameNum0
    // data[29] = frameNum1
    // data[30] = frameNum2
    // data[31] = frameNum3
    // data[32] = chksum0
    // data[33] = chksum1
    // data[34] = chksum2
    // data[35] = chksum3
    // data[36] = endMarker

    private byte getColor(){
        double z = (1000d*frameNumber)/(4d*nFrames);
        double x = Math.sin(2d*Math.PI*z);
        double xAmp = (0.5d*x+0.5d)*128d;
        int a = (int) xAmp;
        byte b = (byte) a;
//        LOGGER.info("z = [{}], x = [{}], xAmp = [{}], a = [{}], b = [{}]", z, x, xAmp, a, b);
        return b;
    }

    private byte[] getBadData(){
        byte[] data = new byte[9];
        data[0] = (byte) startMarker;
        data[data.length-1] = (byte) endMarker;
        return data;
    }

    public static float[] HSVtoRGB(float h, float s, float v)
    {
        // H is given on [0->6] or -1. S and V are given on [0->1].
        // RGB are each returned on [0->1].
        float m, n, f;
        int i;

        float[] hsv = new float[3];
        float[] rgb = new float[3];

        hsv[0] = h;
        hsv[1] = s;
        hsv[2] = v;

        if (hsv[0] == -1)
        {
            rgb[0] = rgb[1] = rgb[2] = hsv[2];
            return rgb;
        }
        i = (int) (Math.floor(hsv[0]));
        f = hsv[0] - i;
        if (i % 2 == 0)
        {
            f = 1 - f; // if i is even
        }
        m = hsv[2] * (1 - hsv[1]);
        n = hsv[2] * (1 - hsv[1] * f);
        switch (i)
        {
            case 6:
            case 0:
                rgb[0] = hsv[2];
                rgb[1] = n;
                rgb[2] = m;
                break;
            case 1:
                rgb[0] = n;
                rgb[1] = hsv[2];
                rgb[2] = m;
                break;
            case 2:
                rgb[0] = m;
                rgb[1] = hsv[2];
                rgb[2] = n;
                break;
            case 3:
                rgb[0] = m;
                rgb[1] = n;
                rgb[2] = hsv[2];
                break;
            case 4:
                rgb[0] = n;
                rgb[1] = m;
                rgb[2] = hsv[2];
                break;
            case 5:
                rgb[0] = hsv[2];
                rgb[1] = m;
                rgb[2] = n;
                break;
        }

        return rgb;

    }

    private byte[] getData() {
        if(frameNumber % 30 == 0){
            if(swtichFlip){
                swtichFlip = false;
            }else{
                swtichFlip = true;
            }
        }else if(frameNumber %1000 == 0){
            double p = ((double) frameNumber) / ((double) nFrames);
            LOGGER.info("frameNumber = {}/{}={}", frameNumber, nFrames, p);
        }else if(frameNumber %200 == 0){
            if(swtichFlip2){
                swtichFlip2= false;
            }else{
                swtichFlip2 = true;
            }
            LOGGER.info("SwitchFlip 2 == {}", swtichFlip2);
        }

        float[] curColor = HSVtoRGB(((float) getColor())/127f, 1f, 1f);
        byte[] data = new byte[DATA_SIZE];
        data[0] = (byte) startMarker;
        data[2] = 64;
        if(swtichFlip){
            data[3] = (byte) (curColor[0]*253);
            data[4] = (byte) (curColor[1]*253);
            data[5] = (byte) (curColor[2]*253);
        }
        data[6] = 1;
        data[36] = (byte) endMarker;
        if(swtichFlip2){
            data = getBadData();
        }
        return data;
    }

    public ArduinoTxController() {
        // Connect to the com port.
        connect("COM6");

        // If connected, start the scheduler to update the lights.
        if (isConnected()) {
            Runtime.getRuntime().addShutdownHook(new Thread(this::cleanUp));
            ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
            scheduler.scheduleAtFixedRate(() -> {
                    try {
                        updateLights();
                    } catch (Exception e) {
                        LOGGER.info("Failed to update lights.", e);
                        System.exit(-1);
                    }
            }, 0, 16670000L, TimeUnit.NANOSECONDS);
        }
    }

    private static void refreshChecksum(byte[] data) {
        Checksum checksum = new CRC32();
        checksum.update(data, 1, data.length - 4 - 2);
        long checksumValue = checksum.getValue();
        byte[] byteChecksum = ByteUtils.longToBytes(checksumValue);
        data[data.length - 5] = byteChecksum[7];
        data[data.length - 4] = byteChecksum[6];
        data[data.length - 3] = byteChecksum[5];
        data[data.length - 2] = byteChecksum[4];
    }

    private static void updateFrameNumber(byte[] data) {
        byte[] byteFrameNumber = ByteUtils.longToBytes(frameNumber++);
        data[data.length - 9] = byteFrameNumber[7];
        data[data.length - 8] = byteFrameNumber[6];
        data[data.length - 7] = byteFrameNumber[5];
        data[data.length - 6] = byteFrameNumber[4];
    }

    private void updateLights() {
        if (isConnected && !isShuttingDown) {
            if(frameNumber > nFrames){
                cleanUp();
            }else{
                sendDataToTx(getData());
            }
        }
    }

    private void sendDataToTx(byte[] data) {
        if (isConnected) {
            try {
                updateFrameNumber(data);
                refreshChecksum(data);
//                LOGGER.info("Sending data! =)");

                if (!serialPort.writeBytes(data)) {
                    LOGGER.info("Failed to write to Tx lights.");
                    cleanUp();
                }
            } catch (Exception e) {
                LOGGER.info("Failed to write to lights.", e);
                cleanUp();
            }
        }
    }

    private boolean isConnected() {
        return isConnected;
    }

    public void connect(String portName) {
        if (!portName.equals("None")) {
            LOGGER.info("Attempting to connect to transmitter.");
            isShuttingDown = false;
            try {
                serialPort.closePort();
            } catch (Exception ignored) {

            }
            try {
                serialPort = new SerialPort(portName);
                serialPort.openPort();
                if (!serialPort.isOpened()) {
                    LOGGER.info("Error. Failed to open serialPort.");
                    return;
                }
                serialPort.setParams(115200, SerialPort.DATABITS_8, SerialPort.STOPBITS_1, SerialPort.PARITY_NONE, false, false);
                isConnected = true;
                resetLights();
                if (!isConnected) {
                    LOGGER.info("Failed to reset tx lights after connecting.");
                }else{
                    LOGGER.info("Tx connected successfully. Enjoy the pretty lights! :)");
                }
            } catch (Exception e) {
                LOGGER.info("Exception caught in connect.", e);
                cleanUp();
            }
        }
    }

    private void resetLights() {
        LOGGER.info("Attempting to reset the lights.");
        byte[] data = new byte[DATA_SIZE];
        data[0] = (byte) startMarker;
        data[data.length - 1] = (byte) endMarker;
        frameSkipCnt = Integer.MAX_VALUE;
        sendDataToTx(data);
    }

    public void cleanUp() {
        LOGGER.info("Tx cleanup called.");
        if (isConnected && !isShuttingDown) {
            isShuttingDown = true;
            try {
                for (int a = 0; a < 3; a++) {
                    resetLights();
                    try {
                        Thread.sleep(25);
                    } catch (InterruptedException e) {
                        LOGGER.info("Failed to sleep?!?!?", e);
                    }
                }
                serialPort.closePort();
            } catch (Exception e) {
                LOGGER.info("Failed to cleanup Tx.", e);
            }
        }
        isConnected = false;
        LOGGER.info("Tx cleanup complete.");
        System.exit(-1);
    }
}