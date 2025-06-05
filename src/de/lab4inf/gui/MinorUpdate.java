// Datei: src/main/java/de/lab4inf/gui/MinorUpdate.java
package de.lab4inf.gui;

import javax.sound.sampled.*;
import javax.swing.*;
import java.awt.*;
import java.io.File;

public class MinorUpdate {
    public static void trigger() {
        Toolkit.getDefaultToolkit().beep();
        JWindow jw = new JWindow();
        jw.setAlwaysOnTop(true);
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        jw.setBounds(0, 0, screenSize.width, screenSize.height);

        ImageIcon icon = null;
        try {
            Image img = new ImageIcon(new File("/Users/nicotillmann/IdeaProjects/GolPrak/src/resources/jumpscare.png")
                    .toURI().toURL()).getImage();
            Image scaled = img.getScaledInstance(screenSize.width, screenSize.height, Image.SCALE_SMOOTH);
            icon = new ImageIcon(scaled);
        } catch (Exception ignored) { }

        JLabel label = new JLabel(icon);
        label.setBounds(0, 0, screenSize.width, screenSize.height);
        jw.getContentPane().setBackground(Color.BLACK);
        if (icon != null) {
            jw.getContentPane().add(label);
        }

        jw.setVisible(true);

        new Thread(() -> {
            long endTime = System.currentTimeMillis() + 2000;
            while (System.currentTimeMillis() < endTime) {
                try {
                    SwingUtilities.invokeLater(() -> label.setVisible(!label.isVisible()));
                    Thread.sleep(100);
                } catch (InterruptedException ignored) { }
            }
            SwingUtilities.invokeLater(jw::dispose);
        }).start();

        try {
            File soundFile = new File("/Users/nicotillmann/IdeaProjects/GolPrak/src/resources/jumpscare.wav");
            if (soundFile.exists()) {
                AudioInputStream ais = AudioSystem.getAudioInputStream(soundFile);
                Clip clip = AudioSystem.getClip();
                clip.open(ais);
                clip.start();
            }
        } catch (Exception ignored) { }
    }
}
