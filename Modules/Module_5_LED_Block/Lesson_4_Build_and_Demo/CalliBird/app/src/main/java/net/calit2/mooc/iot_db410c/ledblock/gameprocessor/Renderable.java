package net.calit2.mooc.iot_db410c.ledblock.gameprocessor;

/**
 * Created by Ara on 8/2/15.
 */
public interface Renderable {
    void begin();
    void render(char [][] data,final int score);
    void clear(final int score);
}
