package net.calit2.mooc.iot_db410c.ledblock.gameprocessor;

/**
 * Created by Ara on 1/20/15.
 */
public abstract class Actor {


    protected Location location;
    protected boolean running;

    public Actor(int x, int y){
        location = new Location(x,y);
        running=true;
    }

    public Actor(Location location){
        this(location.getx(),location.gety());
    }

    public Location getLocation() {
        return location;
    }

    public int getx(){
        return location.getx();
    }
    public int gety(){
        return location.gety();
    }
    public boolean decrementx(){
        location.decrementx();
        if(getx()<0) { return false;}
        return true;
    }
    public boolean incrementx(){
        location.incrementx();
        if(getx()>=Map.WIDTH) {return false;}
        return true;
    }
    public boolean decrementy(){
        location.decrementy();
        if(gety()>=Map.HEIGHT) {return false;}
        return true;

    }
    public boolean incrementy(){
        location.incrementy();
        if(gety()<0) {return false;}
        return true;

    }

    public boolean intersects(Actor actor){
        return location.intersects(actor.getLocation());
    }
    public abstract boolean move();

    public void setRunning(boolean running){
        this.running = running;
    }



}
