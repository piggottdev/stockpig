package dev.pig.stockpig.gui;

import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;

/**
 * Sound player fo playing a move sound on piece move.
 */
final class MoveSoundPlayer {

    /**
     * Play the move sound.
     */
    static void play() {
        try {
            final Clip clip = AudioSystem.getClip();
            clip.open(AudioSystem.getAudioInputStream(MoveSoundPlayer.class.getClassLoader().getResourceAsStream("move.wav")));
            clip.start();
        } catch (Exception ignored) {
            ignored.printStackTrace();
        }
    }

}
