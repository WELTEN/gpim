package net.wespot.gpim;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.example.TestGlass_v1.R;
import com.google.android.glass.widget.CardScrollAdapter;
import org.celstec.arlearn.delegators.INQ;

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
public class InquiryScrollAdapter extends CardScrollAdapter {
    private final Context mContext;
    private final long[] mValues;
//    private Inquiry inquiry;
    public static final String TAG = "InquiryScrollAdapter";


    public InquiryScrollAdapter(Context mContext) {
        this.mContext = mContext;
        mValues = new long[4];
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
        if (convertView == null) {

            switch (position){
                case 0:
                    // Display wonder moment
                    if (INQ.inquiry != null ){
                        if (INQ.inquiry.getCurrentInquiry().getReflection() != null){
                            convertView = LayoutInflater.from(mContext).inflate(R.layout.display_wonder_moment, parent);

                            final TextView[] views = new TextView[] {
                                    (TextView) convertView.findViewById(R.id.get_wonder_title),
                                    (TextView) convertView.findViewById(R.id.get_wonder_description)};

                            views[0].setText(INQ.inquiry.getCurrentInquiry().getReflection());
                            views[1].setText(R.string.description_wonder_moment2);
                        }else{
                            convertView = LayoutInflater.from(mContext).inflate(R.layout.set_wonder_moment, parent);
                        }
                    }else{
                        convertView = LayoutInflater.from(mContext).inflate(R.layout.set_wonder_moment, parent);
                    }
                    break;
                case 1:
                    // Display hypothesis
                    convertView = LayoutInflater.from(mContext).inflate(R.layout.set_hypothesis, parent);

                    if (INQ.inquiry != null ){
                        if (INQ.inquiry.getCurrentInquiry().getHypothesisTitle() != null){

                            final TextView[] views = new TextView[] {
                                    (TextView) convertView.findViewById(R.id.title_hypothesis),
                                    (TextView) convertView.findViewById(R.id.description_hypothesis)};

                            views[0].setText("My hypothesis was: "+INQ.inquiry.getCurrentInquiry().getHypothesisTitle());
                            views[1].setText(R.string.description_hypothesis2);
                        }
                    }
                    break;
                case 2:
                    // Display data collection

//                    int numberDataCollections = DaoConfiguration.getInstance().getGeneralItemLocalObjectDao().loadAll().size();
                    int numberDataCollections = 4;

                    if (INQ.inquiry != null ){
                        if (numberDataCollections != 0){

                            convertView = LayoutInflater.from(mContext).inflate(R.layout.data_collection, parent);

                            final TextView[] views = new TextView[] {
                                    (TextView) convertView.findViewById(R.id.title_number_data_collected),
                                    (TextView) convertView.findViewById(R.id.description_data_collected)};

                            String number_data_collected = mContext.getResources().getQuantityString(R.plurals.number_data_collected, numberDataCollections, numberDataCollections);

                            views[0].setText(number_data_collected);
                            views[1].setText(R.string.description_collect_data);
                        }
                        else{
                            convertView = LayoutInflater.from(mContext).inflate(R.layout.set_collect_data, parent);
                        }

                    }else{
                        convertView = LayoutInflater.from(mContext).inflate(R.layout.set_collect_data, parent);
                    }
                    break;
//                case 3:
//                    convertView = LayoutInflater.from(mContext).inflate(R.layout.map, parent);

                default:
                    convertView = LayoutInflater.from(mContext).inflate(R.layout.save_inquiry, parent);
                    break;
            }
        }
//        final TextView[] views = new TextView[] {
//                (TextView) convertView.findViewById(R.id.hours),
//                (TextView) convertView.findViewById(R.id.minutes),
//                (TextView) convertView.findViewById(R.id.seconds)
//        };
//        final TextView tipView = (TextView) convertView.findViewById(R.id.tip);
//        final String tipLabel = mContext.getResources().getString(((TimeComponents) getItem(position)).getLabelResourceId());
//
//        tipView.setText(tipLabel);
//
//        for (int i = 0; i < 3; ++i) {
//            views[i].setText(String.format("%02d", mValues[i]));
//            views[i].setTextColor(mContext.getResources().getColor(R.color.gray));
//        }
//        views[position].setTextColor(mContext.getResources().getColor(R.color.white));

        return convertView;
    }

    @Override
    public int getPosition(Object o) {
        return 0;
    }
}
