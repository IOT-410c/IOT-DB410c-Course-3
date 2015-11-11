package net.calit2.mooc.iot_db410c.ledblock.gameprocessor;

/**
 * Created by Ara on 1/20/15.
 */
public class CalliBird extends Actor {
    public CalliBird(int x, int y) {
        super(x, y);
    }

    public CalliBird(Location location){
        super(location);

    }


    @Override
    public boolean move() {
       // incrementx();
        return true;
    }
}
