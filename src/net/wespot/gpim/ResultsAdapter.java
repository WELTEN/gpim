package net.wespot.gpim;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.example.TestGlass_v1.R;
import com.google.android.glass.widget.CardScrollAdapter;

import java.util.ArrayList;

/**
 * ****************************************************************************
 * Copyright (C) 2013 Open Universiteit Nederland
 * <p/>
 * This library is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * <p/>
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * <p/>
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library.  If not, see <http://www.gnu.org/licenses/>.
 * <p/>
 * Contributors: Angel Suarez
 * ****************************************************************************
 */
public class ResultsAdapter extends CardScrollAdapter {
    private final Context mContext;
//    private final long[] mValues;
    private ArrayList   mValues;
    public static final String TAG = "ViewResultsScrollAdapter";


    public ResultsAdapter(Context mContext) {
        this.mContext = mContext;
        Log.e(TAG, "Number data collected"+MainActivity.data_collection.size());
        mValues = MainActivity.data_collection;
    }


    @Override
    public int getCount() {
        return MainActivity.data_collection.size();
    }

    @Override
    public Object getItem(int position) {
        if (position >= 0 && position < MainActivity.data_collection.size()) {
            return mValues.get(position);
        }
        return null;
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (convertView == null){
            convertView = LayoutInflater.from(mContext).inflate(R.layout.card_1_image, parent);

            final TextView[] views = new TextView[] {
                    (TextView) convertView.findViewById(R.id.caption),
                    (TextView) convertView.findViewById(R.id.filter_name)};
//
            final ImageView[] images = new ImageView[] {
                    (ImageView) convertView.findViewById(R.id.filtered_image)};


            images[0].setImageBitmap((Bitmap) MainActivity.data_collection.get(position));

//
//            for (Object s : INQ.inquiry.getData_collection()){
//                images[0].setImageBitmap((Bitmap)s);
//                Log.e(TAG, " " + images[0]);
//            }


//        views[0].setText(R.string.title_collect_data2);
//            Iterator it = INQ.inquiry.getData_collection().iterator();
//            while (it.hasNext()) {
////                Map.Entry e = (Map.Entry)it.next();
////                images[0].setImageBitmap((Bitmap) e.getKey());
////                Location d = (Location) e.getValue();
////                views[1].setText(d.getLongitude()+" ssss");
//
//                images[0].setImageBitmap((Bitmap) it.next());
//                Log.e(TAG, " " + images[0]);
//            }

        }


        return convertView;

    }

    @Override
    public int getPosition(Object o) {
        return 0;
    }


}
