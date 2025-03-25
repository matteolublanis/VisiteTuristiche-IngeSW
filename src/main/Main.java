package main;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

import archivio.Archivio;
import controller.ControllerArchivio;
import controller.Login;


public class Main {
	public static void main (String args[]) throws IllegalAccessException, InvocationTargetException, UnsupportedAudioFileException, IOException, LineUnavailableException {
        String imagePath = "src/gabibbo.png"; // Modifica con il percorso corretto

        if (!checkImageExists(imagePath)) {
            System.err.println("hey besugo dove mi hai messo?");
            File file = new File("src/tispaccolafaccia.wav"); // Sostituisci con il percorso del tuo file WAV
            AudioInputStream audioStream = AudioSystem.getAudioInputStream(file);
            Clip clip = AudioSystem.getClip();
            clip.open(audioStream);
            clip.start();
            //return;
        }
        
		Archivio d = new Archivio();
		System.out.println("Applicazione avviata...");
		ControllerArchivio gdb = new ControllerArchivio(d);
		Login gl = new Login(gdb);
		App app = new App(gl); 
		app.start();
	}
	
    private static boolean checkImageExists(String path) {
        File file = new File(path);
        return file.exists() && file.isFile();
    }
}
