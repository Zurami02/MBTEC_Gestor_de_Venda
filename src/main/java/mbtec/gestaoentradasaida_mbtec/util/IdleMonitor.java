package mbtec.gestaoentradasaida_mbtec.util;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.util.Duration;

/**
 * @author Mbtec : Tecnico IT Zulo Mitumba
 * a Classe controla a inatividade dentro do sistema durante alguns menutos
 */
public class IdleMonitor {
    private final Duration idleTime;
    private Runnable onIdle;
    private Timeline timeline;

    private Scene scene;
    private EventHandler<Event> resetHandler;

    public IdleMonitor(Duration idleTime) {
        this.idleTime = idleTime;
    }

    public void setOnIdle(Runnable onIdle) {
        this.onIdle = onIdle;
    }

    public void register(Scene scene) {
        if (scene == null) return;

        timeline = new Timeline(
                new KeyFrame(idleTime, e -> {
                    if (onIdle != null){
                        onIdle.run();
                    }
                })
        );
        timeline.setCycleCount(1);

        EventHandler<Event> resetHandler = e -> reset();

        scene.addEventFilter(MouseEvent.ANY, resetHandler);
        scene.addEventFilter(KeyEvent.ANY, resetHandler);

        reset();
    }

    public void reset() {
        if (timeline == null) return;
        timeline.playFromStart();
    }

    public void stop(){
        if (timeline != null)
        {timeline.stop();}

        if (scene != null && resetHandler != null) {
            scene.removeEventFilter(MouseEvent.ANY, resetHandler);
            scene.removeEventFilter(KeyEvent.ANY, resetHandler);
        }
    }
}
