package org.openjfx.miniprojet.controller;

import javafx.scene.media.AudioClip;

import java.util.Objects;

public class NotificationSoundController {

    public void playCompletedTaskSound(){
        String soundFile = Objects.requireNonNull(getClass().getResource("/org/openjfx/miniprojet/assets/audio/complete_sound.wav")).toString();
        AudioClip audioClip = new AudioClip(soundFile);

        audioClip.play();
    }
}
