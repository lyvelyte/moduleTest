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

    public ArduinoTxController() {
        // Connect to the com port.
        connect("COM3");

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
            LOGGER.info("Light scheduler closed.");
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
            sendDataToTx(getData());
        }
    }

    private void sendDataToTx(byte[] data) {
        if (isConnected) {
            try {
                updateFrameNumber(data);
                refreshChecksum(data);
                LOGGER.info("Sending data! =)");

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
                for (int a = 0; a < 10; a++) {
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
    }

    private byte[] getData() {
        byte[] data = new byte[DATA_SIZE];
        data[0] = (byte) startMarker;
        data[data.length - 1] = (byte) endMarker;
        return data;
    }
}