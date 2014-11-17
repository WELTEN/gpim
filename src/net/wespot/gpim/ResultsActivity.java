package net.wespot.gpim;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.media.AudioManager;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import com.example.TestGlass_v1.R;
import com.google.android.glass.media.Sounds;
import com.google.android.glass.touchpad.Gesture;
import com.google.android.glass.touchpad.GestureDetector;
import com.google.android.glass.view.MenuUtils;
import com.google.android.glass.widget.CardScrollView;
import daoBase.DaoConfiguration;
import net.wespot.gpim.adapters.ResultsAdapter;
import org.celstec.arlearn.delegators.INQ;
import org.celstec.arlearn2.android.dataCollection.AudioInputManager;
import org.celstec.arlearn2.android.dataCollection.DataCollectionManager;
import org.celstec.arlearn2.android.dataCollection.PictureManager;
import org.celstec.arlearn2.android.dataCollection.VideoManager;
import org.celstec.arlearn2.android.delegators.ARL;
import org.celstec.dao.gen.GeneralItemLocalObject;
import org.celstec.dao.gen.ResponseLocalObject;

import java.io.File;
import java.util.List;

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
public class ResultsActivity extends Activity{

    public static final int PICTURE_RESULT = 1;
    public static final int AUDIO_RESULT = 2;
    public static final int VIDEO_RESULT = 3;
    public static final int TEXT_RESULT = 4;
    public static final int VALUE_RESULT = 5;

    public static final String EXTRA_PICTURE_FILE_PATH = "picture_file_path";
    public static final String EXTRA_THUMBNAIL_FILE_PATH = "thumbnail_file_path";
    public static final String EXTRA_VIDEO_FILE_PATH = "video_file_path";

    private CardScrollView mView;
    private GestureDetector mGestureDetector;

    private AudioManager maManager;
    private ResultsAdapter mAdapter;
    private GeneralItemLocalObject generalItemLocalObject;

    private PictureManager man_pic = new PictureManager(this);
    private VideoManager man_vid = new VideoManager(this);
    private AudioInputManager man_aud = new AudioInputManager(this);
    private File bitmapFile;

    public static final String TAG = "ViewResults";

    @Override
    protected void onResume() {
        super.onResume();
        mView.activate();
    }

    @Override
    public void onPause() {
        super.onPause();
        mView.deactivate();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle extras = getIntent().getExtras();

        assert extras != null;
        long generalItemId = extras.getLong("generalItem");

        generalItemLocalObject = DaoConfiguration.getInstance().getGeneralItemLocalObjectDao().load(generalItemId);

        mAdapter = new ResultsAdapter(this, generalItemLocalObject.getResponses());
        mGestureDetector = createGestureDetector(this);

        mView = new CardScrollView(this) {
            @Override
            public final boolean dispatchGenericFocusedEvent(MotionEvent event) {
                if (mGestureDetector.onMotionEvent(event)) {
                    return true;
                }
                return super.dispatchGenericFocusedEvent(event);
            }
        };


        maManager = (AudioManager) getSystemService(this.AUDIO_SERVICE);


        mView.setAdapter(mAdapter);

        setContentView(mView);
    }

    private GestureDetector createGestureDetector(Context context)
    {
        GestureDetector gdDetector = new GestureDetector(context);

        gdDetector.setBaseListener( new GestureDetector.BaseListener()
        {
            @Override
            public boolean onGesture(Gesture gesture)
            {
                if (gesture == Gesture.TAP)
                {
                    //play the tap sound
                    maManager.playSoundEffect(Sounds.TAP);
                    //open the menu
                    openOptionsMenu();
                    return true;
                }

                return false;
            }
        });

        return gdDetector;
    }

    @Override
    public boolean onGenericMotionEvent(MotionEvent event)
    {
        if (mGestureDetector != null)
            return mGestureDetector.onMotionEvent(event);

        return false;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_inquriy, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {

        boolean data_collection = InquiryActivity.data_collection_images.size() != 0;

        // TODO visualization of the menu of data collection doesn't work
        menu.setGroupVisible(R.id.data_collection, data_collection);

        MenuUtils.setDescription(menu.findItem(R.id.data_collection_take_picture), R.string.menu_data_collection_take_picture_description);
        MenuUtils.setDescription(menu.findItem(R.id.data_collection_record_video), R.string.menu_data_collection_record_video_description);
        MenuUtils.setDescription(menu.findItem(R.id.data_collection_collect_text), R.string.menu_data_collection_collect_text_description);
        MenuUtils.setDescription(menu.findItem(R.id.data_collection_record_audio), R.string.menu_data_collection_record_audio_description);
        MenuUtils.setDescription(menu.findItem(R.id.data_collection_view_results), R.string.menu_data_collection_view_results_description);

        return super.onPrepareOptionsMenu(menu);    //To change body of overridden methods use File | Settings | File Templates.
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection.
        switch (item.getItemId()) {
            case R.id.data_collection_take_picture:
                Log.e(TAG, "Take a picture");
                man_pic.setRunId(INQ.inquiry.getCurrentInquiry().getRunId());
                man_pic.setGeneralItem(generalItemLocalObject);
                man_pic.takeDataSample(null);
//                Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
//                bitmapFile = MediaFolders.createOutgoingJpgFile();
//                cameraIntent.putExtra(android.provider.MediaStore.EXTRA_OUTPUT, Uri.fromFile(bitmapFile));
//                startActivityForResult(cameraIntent, PICTURE_RESULT);
                break;
            case R.id.data_collection_record_video:
                man_vid.setRunId(INQ.inquiry.getCurrentInquiry().getRunId());
                man_vid.setGeneralItem(generalItemLocalObject);
                man_vid.takeDataSample(null);
                break;
            case R.id.data_collection_record_audio:
                Intent wonder_moment_intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                startActivityForResult(wonder_moment_intent, TEXT_RESULT);
                break;
//            case R.id.data_collection_record_audio:
//                man_aud.setRunId(INQ.inquiry.getCurrentInquiry().getRunId());
//                man_aud.setGeneralItem(generalItemLocalObject);
//                man_aud.takeDataSample(AudioCollectionActivityImpl.class);
//                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {

            // Different responses from the intent result.
            switch (requestCode){
                case DataCollectionManager.PICTURE_RESULT:
                    man_pic.onActivityResult(requestCode, resultCode, data);
//                    data = null;
//                    if (resultCode == Activity.RESULT_OK) {
//                        Uri uri = null;
//                        String filePath = null;
//                        if (data != null) {
//                            uri = data.getData();
//                            filePath = data.getData().getPath();
//                        } else {
//                            uri = Uri.fromFile(bitmapFile);
//                            filePath = bitmapFile.getAbsolutePath();
//                        }
//
//                        ResponseLocalObject response = new ResponseLocalObject();
//
//                        response.setUriAsString(uri.toString());
//
//                        BitmapFactory.Options options = new BitmapFactory.Options();
//                        options.inJustDecodeBounds = true;
//
//                        BitmapFactory.decodeFile(filePath, options);
//                        response.setContentType("image/jpeg");
//                        response.setWidth(options.outWidth);
//                        response.setHeight(options.outHeight);
//
//                        saveResponseForSyncing(response);
//                    } else if (resultCode == Activity.RESULT_CANCELED) {
//                        // User cancelled the image capture§
//                    } else {
//                        // Image capture failed, advise user
//                    }
                    break;
                case DataCollectionManager.VIDEO_RESULT:
                    man_vid.onActivityResult(requestCode, resultCode, data);
                    break;
                case DataCollectionManager.TEXT_RESULT:
                    List<String> results_wonder_moment = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    String txt_wonder_moment = results_wonder_moment.get(0);

                    ResponseLocalObject response = new ResponseLocalObject();
                    response.setValue(txt_wonder_moment);
                    response.setTextType();
                    saveResponseForSyncing(response);
                    break;
            }
            INQ.responses.syncResponses(INQ.inquiry.getCurrentInquiry().getRunId());
        }
    }

    protected void saveResponseForSyncing(ResponseLocalObject res) {
        ResponseLocalObject response = new ResponseLocalObject();
        response.setRevoked(false);

        response.setContentType(res.getContentType());
        response.setUriAsString(res.getUriAsString());
        response.setHeight(res.getHeight());
        response.setWidth(res.getWidth());


        response.setRunId(INQ.inquiry.getCurrentInquiry().getRunId());
        response.setGeneralItemLocalObject(generalItemLocalObject);
        response.setTimeStamp(ARL.time.getServerTime());
        response.setAccountLocalObject(ARL.accounts.getLoggedInAccount());
        response.setIsSynchronized(false);
        response.setNextSynchronisationTime(0l);
        response = setLocationDetails(response);
        DaoConfiguration.getInstance().getResponseLocalObjectDao().insertOrReplace(response);
        DaoConfiguration.getInstance().getRunLocalObjectDao().load(response.getRunId()).resetResponses();
        INQ.responses.syncResponses(INQ.inquiry.getCurrentInquiry().getRunId());
    }

    private ResponseLocalObject setLocationDetails(ResponseLocalObject response) {
        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        String locationProviderNetwork = LocationManager.NETWORK_PROVIDER;
        String locationProviderGPS = LocationManager.GPS_PROVIDER;
        Location lastKnownLocation = locationManager.getLastKnownLocation(locationProviderGPS);
        if (lastKnownLocation == null) {
            lastKnownLocation =locationManager.getLastKnownLocation(locationProviderNetwork);
        }
        if (lastKnownLocation != null) {
            response.setLat(lastKnownLocation.getLatitude());
            response.setLng(lastKnownLocation.getLongitude());
        }
        return response;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

    }
}
