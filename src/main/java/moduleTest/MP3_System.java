package moduleTest;

import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class MP3_System {

    private final double updates_per_second = 60d;
    private SourceDataLine outputDeviceLine;
    private AudioInputStream decodedMP3Stream;
    private AudioInputStream encodedMP3Stream;
    private byte[] binDataBuffer;
    private int samplesPerFrame;
    private static final float c = (float) (1d / Math.pow(2, 15));
    private String mp3Filename = "test_tone_440Hz.mp3";
    public boolean isRunning = false;
    private AudioFormat decodedFormat;

    public void init() {
        // Attempt to open the mp3 file for decoding
        File file = new File(mp3Filename);
        try {
            System.out.println("Working Directory = " + System.getProperty("user.dir"));
            System.out.println("File = " + file);
            encodedMP3Stream = AudioSystem.getAudioInputStream(file);
        } catch (UnsupportedAudioFileException e) {
            System.out.println("Error: Invalid MP3 file. UnsupportedAudioFileException. This is usually because of a missing mp3spi dependency.");
            e.printStackTrace();
            cleanUp();
            System.exit(-1);
        } catch (IOException e) {
            System.out.println("Error: Invalid MP3 file. IOException.");
            e.printStackTrace();
            cleanUp();
            System.exit(-1);
        }

        // Print original MP3s format.
        AudioFormat encodedFormat = encodedMP3Stream.getFormat();
        decodedFormat = new AudioFormat(AudioFormat.Encoding.PCM_SIGNED, encodedFormat.getSampleRate(), 16,2, 4, encodedFormat.getSampleRate(), false);
        decodedMP3Stream = AudioSystem.getAudioInputStream(decodedFormat, encodedMP3Stream);
        System.out.println(mp3Filename + " decoding with the following audio format: ");
        System.out.println("Sample Rate: " + decodedFormat.getSampleRate() + "Hz");
        System.out.println("Bits per sample: " + decodedFormat.getSampleSizeInBits());
        System.out.println("Number of channels: " + decodedFormat.getChannels());
        System.out.println("Encoding type: " + decodedFormat.getEncoding());

        // Set buffers and constants.
        samplesPerFrame = (int) ((decodedFormat.getSampleRate()) / updates_per_second);
        binDataBuffer = new byte[(int) (Math.ceil(samplesPerFrame * decodedFormat.getFrameSize()))];

        startPlayback();

        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
        scheduler.scheduleAtFixedRate(() -> {
            if (isRunning) {
                updateAudio();
            } else {
                scheduler.shutdownNow();
            }
        }, 0, 16670000L, TimeUnit.NANOSECONDS);
    }

    // Starts Playback for JavaSound (non ASIO)
    private void startPlayback() {
            // Open a device for playback.
            try {
                outputDeviceLine = getLine();
            } catch (LineUnavailableException e1) {
                System.out.println("Error: Failed to getLine for starPlayback.");
                e1.printStackTrace();
                cleanUp();
                System.exit(-1);
            }

            // Start the playback line.
            outputDeviceLine.start();
            isRunning = true;
    }

    private void updateAudio() {
        int nBytesRead = readFrame();
        decodeFrame(nBytesRead);
    }

    // Attempt to open a "line" or device for playback.
    private SourceDataLine getLine() throws LineUnavailableException {
        SourceDataLine res = null;
        DataLine.Info info = new DataLine.Info(SourceDataLine.class, decodedFormat);

        if (info.isFormatSupported(decodedFormat)) {
            res = (SourceDataLine) AudioSystem.getLine(info);
            res.open(decodedFormat);
        } else {
            System.out.println("Error. Line not supported.");
            cleanUp();
            System.exit(-1);
        }
        return res;
    }

    public void cleanUp() {
        isRunning = false;
        outputDeviceLine.drain();
        outputDeviceLine.stop();
        outputDeviceLine.close();
        try {
            decodedMP3Stream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            encodedMP3Stream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("MP3 Audio System closed correctly.");
    }

    private int readFrame() {
        int nBytesRead = -1;
        if (isRunning) {
            try {
                if (decodedMP3Stream.available() != -1) {
                    nBytesRead = decodedMP3Stream.read(binDataBuffer, 0, binDataBuffer.length);
                } else {
                    System.out.println("Error: No data available in the MP3 stream.");
                    cleanUp();
                    System.exit(-1);
                }
            } catch (IOException e) {
                e.printStackTrace();
                System.out.println("Error: Failed to read from MP3 File.");
                cleanUp();
                System.exit(-1);
            }

            // Copy data to output buffer. (Playback)
            if (nBytesRead != -1) {
                outputDeviceLine.write(binDataBuffer, 0, nBytesRead);
            } else {
                System.out.println("End of MP3 file reached. Shutting down.");
                cleanUp();
                System.exit(-1);
            }
        }
        return nBytesRead;
    }

    private void decodeFrame(int nBytesRead) {
        if (isRunning) {
            int framesRead = nBytesRead / decodedFormat.getFrameSize();
            float[] monoBuffer = new float[framesRead];

            // Copy current frame amplitudes to the video buffer.
            for (int i = 0; i < framesRead; i++) {
                float Xf;
                if (decodedFormat.getChannels() == 1) {
                    short int16chL = (short) (((binDataBuffer[2 * decodedFormat.getChannels() * i + 1]
                            & 0xFF) << 8) | (binDataBuffer[2 * decodedFormat.getChannels() * i] & 0xFF));
                    Xf = int16chL;
                } else {
                    short int16chL = (short) (((binDataBuffer[2 * decodedFormat.getChannels() * i + 1]
                            & 0xFF) << 8) | (binDataBuffer[2 * decodedFormat.getChannels() * i] & 0xFF));
                    short int16chR = (short) (((binDataBuffer[2 * decodedFormat.getChannels() * i + 3]
                            & 0xFF) << 8)
                            | (binDataBuffer[2 * decodedFormat.getChannels() * i + 2] & 0xFF));
                    Xf = int16chL + int16chR;
                }
                monoBuffer[i] = c*Xf;
            }
//            timeDomainFreqBuffer.add(monoBuffer);
        }
    }
}
