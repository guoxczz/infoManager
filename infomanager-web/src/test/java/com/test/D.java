package com.test;

import javafx.scene.media.Media;

import java.applet.Applet;
import java.applet.AudioClip;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import javax.swing.JFrame;

import javafx.embed.swing.JFXPanel;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

public class D {

    public static void main(String[] args) {


//        Media _media = new Media(sound1.toURI().toString());
//        //        必须有这一行，并且要在MediaPlayer创建之前
//        MediaPlayer _mediaPlayer = new MediaPlayer(_media);
//        _mediaPlayer.play();
//        System.out.println("a");



        Runtime rt = Runtime.getRuntime();

//        Player audioPlayer = Manager.createRealizedPlayer(new File("").toURL());

        Process p = null;
        try {

            p = rt.exec(" cmd.exe  /c start wmplayer D:/ffmpeg/file/smsup.mp3");
//            p.wait(100l);
//            p.destroy();
            System.out.println("a"+p.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }


    }
}