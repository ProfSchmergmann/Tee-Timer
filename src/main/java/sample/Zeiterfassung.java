package sample;

import com.github.plushaze.traynotification.animations.Animations;
import com.github.plushaze.traynotification.notification.Notification;
import com.github.plushaze.traynotification.notification.Notifications;
import com.github.plushaze.traynotification.notification.TrayNotification;
import javafx.animation.AnimationTimer;
import javafx.application.Platform;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.image.ImageView;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

import java.net.URISyntaxException;
import java.time.Duration;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class Zeiterfassung {

    private static AtomicInteger progressBarMax = new AtomicInteger(300);
    private Timer timer = new Timer();
    private Duration duration;
    private boolean isRunning;
    private int startMin;
    private int startSec;
    private AnimationTimer animationTimer;
    private AtomicBoolean beeped = new AtomicBoolean(false);
    private Media media;
    private MediaPlayer mediaPlayer;

    public Zeiterfassung(Label secondslabel, Label minuteslabel, ProgressBar progressBar, ImageView imageView, ImageView imageViewSteam) {
        startSec = Integer.parseInt(secondslabel.getText().trim());
        startMin = Integer.parseInt(minuteslabel.getText().trim());
        setProgressBarMax(progressBar);
        this.start(this.startMin * 60 + this.startSec);
        isRunning = false;
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                if (isRunning && !duration.isZero()) {
                    duration = duration.minusSeconds(1);
                    int max = progressBarMax.get();
                    Platform.runLater(() -> {
                        progressBar.setProgress(1. - 1. * duration.getSeconds() / max);
                        secondslabel.setText(String.format("%02d", getActualseconds()));
                        minuteslabel.setText(String.format("%02d", getActualminutes()));
                    });
                } else if (duration.isZero()) {
                    animationTimer.start();
                    isRunning = false;
                    duration = Duration.ofSeconds(1);
                }
            }
        }, 0, 1000);
        animationTimer = new AnimationTimer() {
            private long last = 0;

            @Override
            public void handle(long l) {
                if (l - last > 200_000_000) {
                    last = l;
                    if (!beeped.get()) {
                        //FEUER
                        beeped.set(true);
                        String file = "";
                        try {
                            file = MainApplication.BEEP_PATH.toURI().toString();
                        } catch (URISyntaxException e) {
                            e.printStackTrace();
                        }
                        if (media == null) {
                            media = new Media(file);
                        }
                        if (mediaPlayer == null) {
                            mediaPlayer = new MediaPlayer(media);
                        }
                        mediaPlayer.play();

                        String title = "Brudi, dein Tee ist ready";
                        String message = "Klick mich doch jetzt weg, du Kek.";
                        Notification notification = Notifications.INFORMATION;

                        TrayNotification tray = new TrayNotification();
                        tray.setTitle(title);
                        tray.setMessage(message);
                        tray.setNotification(notification);
                        tray.setAnimation(Animations.POPUP);
                        tray.showAndWait();
                    }
                    imageViewSteam.setVisible(false);
                    if (imageView.isVisible()) {
                        imageView.setVisible(false);
                    } else {
                        imageView.setVisible(true);
                    }
                }
            }
        };
    }

    public void stopAnimationTimer() {
        animationTimer.stop();
    }

    public void setStartMin(Label minutes) {
        this.startMin = Integer.parseInt(minutes.getText().trim());
    }

    public void setStartSec(Label seconds) {
        this.startSec = Integer.parseInt(seconds.getText().trim());
    }

    public long getActualminutes() {
        return duration.toMinutes();
    }

    public long getActualseconds() {
        return duration.getSeconds() % 60;
    }

    public void start(long time) {
        duration = Duration.ofSeconds(time);
        isRunning = true;
        beeped.set(false);
    }

    public void stop() {
        isRunning = false;
        if (mediaPlayer != null) {
            mediaPlayer.stop();
        }
        beeped.set(false);
    }

    public void setProgressBarMax(ProgressBar progressBar) {
        progressBarMax.set(startMin * 60 + startSec);
        progressBar.setProgress(0);
    }

}
