package net.wespot.gpim.adapters;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import com.example.TestGlass_v1.R;
import com.google.android.glass.widget.CardBuilder;
import com.google.android.glass.widget.CardScrollAdapter;
import org.celstec.dao.gen.InquiryLocalObject;

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
public class InquiriesAdapter extends CardScrollAdapter {
    private final Context mContext;
    private final long[] mValues;
    public static final String TAG = "InquiriesAdapter";
    private List<InquiryLocalObject> inquiryLocalObjects;

    public InquiriesAdapter(Context mContext, List<InquiryLocalObject> inquiryLocalObjects) {
        this.mContext = mContext;
        mValues = new long[inquiryLocalObjects.size()];
        this.inquiryLocalObjects = inquiryLocalObjects;
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

            view = new CardBuilder(mContext, CardBuilder.Layout.AUTHOR)
                    .setText(inquiryLocalObjects.get(position).getDescription())
                    .setIcon(R.drawable.ic_launcher)
                    .setHeading(inquiryLocalObjects.get(position).getTitle())
                    .setSubheading("Author")
                    .setFootnote("Ownership - Members")
                    .setTimestamp("just now")
                    .getView();
        }

        return view;
    }

    @Override
    public int getPosition(Object o) {
        return 0;
    }

}
