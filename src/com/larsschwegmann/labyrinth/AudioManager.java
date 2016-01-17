package com.larsschwegmann.labyrinth;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;

public class AudioManager {
    
    public static void playAudio(String name) {
        try {
            Clip clip = AudioSystem.getClip();
            AudioInputStream inputStream = AudioSystem.getAudioInputStream(AudioManager.class.getClassLoader().getResourceAsStream("com/larsschwegmann/labyrinth/resources/" + name + ".wav"));
            clip.open(inputStream);
            clip.start(); 
        } catch (Exception ex) {
            ex.printStackTrace();
        }
}
    
}
