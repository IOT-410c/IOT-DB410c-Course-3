package net.calit2.mooc.iot_db410c.ledblock;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;

import java.util.ArrayList;

/**
 * Created by JacobTerrado on 8/16/15.
 */
public class GridAdapter extends BaseAdapter {

    private ArrayList<Bitmap> arrayList;
    private Context context;

    public GridAdapter(ArrayList<Bitmap> arrayList, Context context) {
        Log.i("TAG","CREATED");
        this.arrayList = arrayList;
        this.context = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Log.i("TAG", "curr_view");
        LinearLayout coordinate;

        LayoutInflater inflater = ((Activity) context).getLayoutInflater();
        coordinate = (LinearLayout) inflater.inflate(R.layout.coordinate, parent, false);

        ImageView image = (ImageView) coordinate.getChildAt(0);
        image.setImageBitmap(arrayList.get(position));

        return coordinate;
    }

    @Override
    public int getCount() {
        return arrayList.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }
}
