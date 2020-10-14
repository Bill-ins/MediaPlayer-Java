package com.mediaplayer;
import javafx.beans.binding.Bindings;
import javafx.beans.property.*;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.input.MouseEvent;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Duration;
import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;

public class Controller implements Initializable{

    @FXML
    Label play;
    @FXML
    Label pause;
    @FXML
    Slider seeker;
    @FXML
    Slider volumeControl;
    @FXML
    MediaPlayer mediaPlayer;
    @FXML
    MediaView mediaView;
    @FXML
    Media media;
    File file;
    @FXML
    Text timing1;
    @FXML
    Text timing2;

    @FXML
    public void pauseMedia() {
        pause.setVisible(false);
        play.setVisible(true);
        mediaPlayer.pause();
    }

    @FXML
    public void playMedia() {
        pause.setVisible(true);
        play.setVisible(false);
        mediaPlayer.play();
    }

    @FXML
    public void stopMedia() {
        mediaPlayer.stop();
        pause.setVisible(false);
        play.setVisible(true);

        mediaPlayer.setOnReady(() -> {
            seeker.setMin(0);
            seeker.setMax(mediaPlayer.getTotalDuration().toSeconds());
            volumeControl.setValue(50);
        });
    }

    @FXML
    public void fastMedia() {
        if(mediaPlayer.getRate() == 0.5)
            mediaPlayer.setRate(1);
        else
            mediaPlayer.setRate(1.5);
    }

    @FXML
    public void slowMedia() {
        if(mediaPlayer.getRate() == 1.5)
            mediaPlayer.setRate(1);
        else
            mediaPlayer.setRate(0.5);
    }

    @FXML
    public void reload() {
        mediaPlayer.seek(Duration.ZERO);
        playMedia();
    }

    @FXML
    private void seek() {

        mediaPlayer.setOnReady(() -> {
            seeker.setMin(0);
            seeker.setMax(mediaPlayer.getTotalDuration().toSeconds());
        });


        mediaPlayer.currentTimeProperty().addListener(observable -> {
            seeker.setValue(mediaPlayer.getCurrentTime().toSeconds());

            int seconds = (int)mediaPlayer.getCurrentTime().toSeconds();
            int progSec = seconds % 60;
            int progMin = seconds / 60;
            timing1.setText(progMin + ":" + progSec);

            int totalTime = (int)mediaPlayer.getTotalDuration().toSeconds();
            int remain = totalTime - (int)mediaPlayer.getCurrentTime().toSeconds();
            int remSec = remain % 60;
            int remMin = remain / 60;
            timing2.setText("-" + remMin + ":" + remSec);
        });

        seeker.valueProperty().addListener(observable -> {
            if(seeker.isPressed())
                mediaPlayer.seek(Duration.seconds(seeker.getValue()));
        });

    }

    @FXML
    public void addMedia(MouseEvent event) {
        try {
            FileChooser fileChooser = new FileChooser();
            file = fileChooser.showOpenDialog(null);
            String path = file.getAbsolutePath();
            path.replace("\\","/");

            if (file != null) {
                media = new Media(new File(path).toURI().toString());
                mediaPlayer = new MediaPlayer(media);
                mediaView.setMediaPlayer(mediaPlayer);

                Stage stage = (Stage)((Node)event.getSource()).getScene().getWindow();
                stage.setTitle(file.getName());

                playMedia();
            }
        } catch(Exception e) {
            System.out.println("There Is An Error... [404]");
        }

        mediaPlayer.setOnEndOfMedia(() -> {
            mediaPlayer.seek(Duration.ZERO);
            stopMedia();
        });

        volumeControl.setValue(40);
        mediaPlayer.volumeProperty().bind(volumeControl.valueProperty().divide(100));
        seek();
    }

    @FXML
    public void fullScreen() {
        ((Stage)mediaView.getScene().getWindow()).setFullScreen(true);
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        play.setVisible(false);
        pause.setVisible(false);

        DoubleProperty mediaWidth = mediaView.fitWidthProperty();
        DoubleProperty mediaHeight = mediaView.fitHeightProperty();

        mediaWidth.bind(Bindings.selectDouble(mediaView.sceneProperty(),"width"));
        mediaHeight.bind(Bindings.selectDouble(mediaView.sceneProperty(),"height").subtract(75));
    }
}
