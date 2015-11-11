package net.calit2.mooc.iot_db410c.ultrasonic.UltrasonicSensor;

/**
 * Created by Ara on 8/10/15.
 */
public interface UltraSonicInterface {
    boolean getRunningState();
    void setRunningState(boolean state);

    void execute(double process);
}
