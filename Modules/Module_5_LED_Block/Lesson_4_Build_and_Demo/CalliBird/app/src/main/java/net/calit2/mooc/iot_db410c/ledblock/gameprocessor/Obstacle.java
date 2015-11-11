package net.calit2.mooc.iot_db410c.ledblock.gameprocessor;

/**
 * Created by Ara on 1/20/15.
 */
public class Obstacle extends Actor{
    public Obstacle(int x, int y) {
        super(x, y);
    }

    public Obstacle(Location location){
        super(location);

    }


    @Override
    public boolean move() {

        return decrementx();
    }
}
