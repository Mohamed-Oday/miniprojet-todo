package org.openjfx.miniprojet.controller;

import javafx.scene.media.AudioClip;

import java.util.Objects;

/**
 * Controller class for handling notification sounds.
 * This class provides functionality to play a sound when a task is completed.
 */
public class NotificationSoundController {

    /**
     * Plays the sound for a completed task.
     * Loads the audio file from the resources and plays it.
     */
    public void playCompletedTaskSound(){
        String soundFile = Objects.requireNonNull(getClass().getResource("/org/openjfx/miniprojet/assets/audio/complete_sound.wav")).toString();
        AudioClip audioClip = new AudioClip(soundFile);

        audioClip.play();
    }
}