package dev.pig.stockpig.gui.sound;

import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.IOException;
import java.util.Objects;

/**
 * Sound player for playing piece move sounds.
 */
public final class MoveSoundPlayer {

    /**
     * Play the move sound.
     */
    public static void play() {
        try {
            final Clip clip = AudioSystem.getClip();
            clip.open(AudioSystem.getAudioInputStream(Objects.requireNonNull(MoveSoundPlayer.class.getClassLoader().getResourceAsStream("move.wav"))));
            clip.start();
        } catch (final UnsupportedAudioFileException | IOException | LineUnavailableException cause) {
            throw new RuntimeException(cause);
        }
    }


    private MoveSoundPlayer() {};
}
