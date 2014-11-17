package net.wespot.gpim;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Bundle;
import android.view.MotionEvent;
import com.google.android.glass.touchpad.Gesture;
import com.google.android.glass.touchpad.GestureDetector;
import com.google.android.glass.widget.CardBuilder;
import com.google.android.glass.widget.CardScrollView;
import net.wespot.gpim.adapters.DataCollectionAdapter;
import org.celstec.arlearn.delegators.INQ;
import org.celstec.dao.gen.GameLocalObject;
import org.celstec.dao.gen.GeneralItemLocalObject;

import java.util.ArrayList;
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
 * Date: 30/10/14
 * ****************************************************************************
 */

public class DataCollectionActivity extends Activity implements GestureDetector.BaseListener {

    private static final String TAG = "DataCollectionActivity";
    private List<GeneralItemLocalObject> generalItemLocalObjectList;
    private DataCollectionAdapter dataCollectionAdapter;
    private CardScrollView mView;
    private GestureDetector mDetector;
    private List<CardBuilder> mCards;
    private AudioManager mAudioManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);

        if (INQ.inquiry.getCurrentInquiry().getRunLocalObject().getGameLocalObject() != null){
            GameLocalObject gameLocalObject = INQ.inquiry.getCurrentInquiry().getRunLocalObject().getGameLocalObject();

            generalItemLocalObjectList = gameLocalObject.getGeneralItems();

            dataCollectionAdapter = new DataCollectionAdapter(this, generalItemLocalObjectList);

            mView = new CardScrollView(this) {
                @Override
                public final boolean dispatchGenericFocusedEvent(MotionEvent event) {
                    if (mDetector.onMotionEvent(event)) {
                        return true;
                    }
                    return super.dispatchGenericFocusedEvent(event);
                }
            };

            mView.setAdapter(dataCollectionAdapter);
        }else{

            createCards();

            mView = new CardScrollView(this) {
                @Override
                public final boolean dispatchGenericFocusedEvent(MotionEvent event) {
                    if (mDetector.onMotionEvent(event)) {
                        return true;
                    }
                    return super.dispatchGenericFocusedEvent(event);
                }
            };
        }

        setContentView(mView);

        mDetector = new GestureDetector(this).setBaseListener(this);
    }

    private void createCards() {
        mCards = new ArrayList<CardBuilder>();

        mCards.add(new CardBuilder(this, CardBuilder.Layout.TEXT)
                .setText("Retrieving inquiry information")
                .setFootnote("Loading..."));
    }

    @Override
    public boolean onGesture(Gesture gesture) {
        if (gesture == Gesture.TAP) {

            int position = mView.getSelectedItemPosition();

            Intent intent = new Intent(this, ResultsActivity.class);

            GeneralItemLocalObject generalItemLocalObject = generalItemLocalObjectList.get(position);

            INQ.responses.syncResponses(INQ.inquiry.getCurrentInquiry().getRunId());
            intent.putExtra("generalItem", generalItemLocalObject.getId());
            startActivity(intent);
            mAudioManager.playSoundEffect(AudioManager.FX_KEY_CLICK);
            return true;
        }
        return false;
    }

    @Override
    public void onResume() {
        super.onResume();
        mView.activate();
    }

    @Override
    public void onPause() {
        super.onPause();
        mView.deactivate();
    }

    @Override
    public void onBackPressed() {
        Intent resultIntent = new Intent();
        setResult(RESULT_OK, resultIntent);
        super.onBackPressed();
    }
}
