package net.calit2.mooc.iot_db410c.ledblock.gameprocessor;

import android.util.Log;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

/**
 * Created by Ara on 1/20/15.
 */
public class Map {
    public static final int HEIGHT=8;
    public static final int WIDTH = 8;
    private static final String TAG = "Map";
    private List<Actor> actors;
    private char map[][];

    public Map(CalliBird calliBird){
        actors = new ArrayList<Actor>();
        actors.add(calliBird);
        //actors.get(0).start();
        map = new char[HEIGHT][WIDTH];

    }

    public char[][] updateMap(){
        for(int i = 0;i<WIDTH;i++){
            for(int j = 0; j<HEIGHT;j++){
                map[i][j]=0;
                // Clear the space in the grid
            }
        }
        Actor actor;

        for(Iterator <Actor> iter = actors.iterator(); iter.hasNext();){
            actor = iter.next();
            map[actor.gety()][actor.getx()]=1;
            // put in a block of some sort
        }

        map[getCalliBird().gety()][getCalliBird().getx()]=2;
        // Attach bird
        return map;
    }

    public boolean collisions(){
        Actor calliBird = getCalliBird();
        //Location location = getCalliBird().getLocation();
        Iterator<Actor> iter = actors.iterator();
        iter.next();
        for(;iter.hasNext();){
            Actor obstacle = iter.next();
            if(obstacle.intersects(calliBird)){
                removeCalliBird();
                return true;
            }
        }
        return false;
    }
    public void randomize(){
        int position = HEIGHT-1-4; // 7 possible combinations of 2, need two piepes at least, so 4 gone
        Random random = new Random();
        int numOfPipes = random.nextInt(position)+2;
       // Log.i("TAG", "" + numOfPipes);
        for(int j =0;  j<numOfPipes;j++){
            actors.add(new Obstacle(WIDTH-1,j));
        }
        for(int k = numOfPipes+2; k<HEIGHT;k++){
            actors.add(new Obstacle(WIDTH-1,k));
        }

    }

    public void move(){
        Iterator<Actor> iter = actors.iterator();
        //iter.next();'
        Log.i("TAG",""+actors.size());
        for(;iter.hasNext();){
           // Log.i("TAG","HI");
            if(!((iter.next()).move())){

                iter.remove();
            };

        }
    }
    public CalliBird getCalliBird(){
        return (CalliBird)actors.get(0);
    }
    public void removeCalliBird(){
        actors.remove(0);
    }

    public char[] getByteData(){
        //
        String string= "";
        char[] data = new char[Map.HEIGHT];
        for(int i = 0;i<map.length;i++){
            for(int j=0;j<map[i].length;j++){
                //Log.i("BINARY", i+ " " + j + " " + Integer.toBinaryString((int)data[i]));
            //    string+=Integer.toString((int)map[i][j]);
                data[i] = (char) (data[i]|(((int)map[i][j])<<(WIDTH-j-1)));

              //  Log.i("BINARY", i+ " " + j + " " + Integer.toBinaryString((int)data[i]));

            }
            Log.i(TAG, "" +String.format("%8s", Integer.toBinaryString(data[i])).replace(' ', '0'));
          //  string+="\n";
        }
        Log.i(TAG, string);
        return data;
    }

    public boolean checkCalliBirdScore(){
        Actor calliBird = getCalliBird();
        Iterator<Actor> iter = actors.iterator();
        iter.next();
        for(;iter.hasNext();){
            Actor obstacle = iter.next();
            if(obstacle.getx()==(calliBird.getx()-1)){

                return true;
            }
        }
        return false;


    }
}
