package net.calit2.mooc.iot_db410c.ledblock.gameprocessor;

/**
 * Created by Ara on 1/20/15.
 */
public class Location {
    private int x;
    private int y;

    public Location(int x, int y){
        this.x = x;
        this.y = y;
    }

    public int getx(){
        return x;
    }
    public int gety(){ return y;}

    public void incrementx(){
        x++;
    }
    public void incrementy(){
        y--;
    }
    public void decrementx(){
        x--;
    }
    public void decrementy(){
        y++;
    }

    public void setx(int x){
        this.x = x;
    }
    public void sety(int y){
        this.y = y;
    }

    public boolean intersects(Location location){
        if(this.gety()==location.gety()&&this.getx()==location.getx()){
            return true;
        }
        return false;
    }
}
