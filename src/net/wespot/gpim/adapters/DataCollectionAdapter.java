package net.wespot.gpim.adapters;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import com.example.TestGlass_v1.R;
import com.google.android.glass.widget.CardBuilder;
import com.google.android.glass.widget.CardScrollAdapter;
import org.celstec.dao.gen.GeneralItemLocalObject;

import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * ****************************************************************************
 * Copyright (C) 2014 Open Universiteit Nederland
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
 * Date: 04/07/14
 * ****************************************************************************
 */
public class DataCollectionAdapter extends CardScrollAdapter {
    private final Context mContext;
    private final long[] mValues;
    public static final String TAG = "InquiriesAdapter";
    private List<GeneralItemLocalObject> generalItemLocalObjects;

    public DataCollectionAdapter(Context mContext, List<GeneralItemLocalObject> generalItemLocalObjects) {
        this.mContext = mContext;
        mValues = new long[generalItemLocalObjects.size()];
        this.generalItemLocalObjects = generalItemLocalObjects;
    }

    @Override
    public int getCount() {
        return mValues.length;
    }

    @Override
    public Object getItem(int position) {
        if (position >= 0 && position < mValues.length) {
            return mValues[position];
        }
        return null;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View view = null;
        if (convertView == null ) {

            Date date = new Date(generalItemLocalObjects.get(position).getLastModificationDate());
            Format format = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");

            view = new CardBuilder(mContext, CardBuilder.Layout.AUTHOR)
                    .setText(generalItemLocalObjects.get(position).getDescription())
                    .setIcon(R.drawable.ic_launcher)
                    .setHeading(generalItemLocalObjects.get(position).getTitle())
                    .setSubheading(generalItemLocalObjects.get(position).getType())
                    .setFootnote("")
                    .setTimestamp(format.format(date))
                    .getView();
        }

        return view;
    }

    @Override
    public int getPosition(Object o) {
        return 0;
    }

}
