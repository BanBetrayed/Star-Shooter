// Courtsy of Chat-GPT

import java.io.*;
import javax.sound.sampled.*;

public class SoundHandler {
    public static void playSoundInBackground(String soundFilePath) {
        Thread soundThread = new Thread(() -> {
            playSound(soundFilePath);
        });

        soundThread.start();
    }


    // while (grabbing) {
    //     // set grab pos to 0.5
    // }


    public static void playSound(String soundFilePath) {
        try {
            // Load the sound file
            File soundFile = new File(soundFilePath);
            AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(soundFile);

            // Get the audio format
            AudioFormat audioFormat = audioInputStream.getFormat();

            // Open a data line for audio output
            DataLine.Info info = new DataLine.Info(SourceDataLine.class, audioFormat);
            SourceDataLine line = (SourceDataLine) AudioSystem.getLine(info);
            line.open(audioFormat);

            // Start playing the sound
            line.start();
            byte[] buffer = new byte[4096]; // Adjust buffer size as needed
            int bytesRead;
            while ((bytesRead = audioInputStream.read(buffer)) != -1) {
                line.write(buffer, 0, bytesRead);
            }

            // Close the resources
            line.drain();
            line.close();
            audioInputStream.close();

        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            // Handle exceptions gracefully (e.g., log the error or display a user-friendly
            // message)
            e.printStackTrace();
        }
    }
}
