package net.wespot.gpim;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.location.Location;
import android.location.LocationListener;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.speech.RecognizerIntent;
import android.util.Log;
import android.view.*;
import com.example.TestGlass_v1.R;
import com.google.android.glass.timeline.LiveCard;
import com.google.android.glass.touchpad.Gesture;
import com.google.android.glass.touchpad.GestureDetector;
import com.google.android.glass.view.MenuUtils;
import com.google.android.glass.widget.CardScrollView;
import daoBase.DaoConfiguration;
import net.wespot.gpim.adapters.InquiryAdapter;
import net.wespot.gpim.utils.BitmapUtils;
import org.celstec.arlearn.delegators.INQ;
import org.celstec.arlearn2.android.dataCollection.*;
import org.celstec.arlearn2.android.db.PropertiesAdapter;
import org.celstec.arlearn2.android.delegators.ARL;
import org.celstec.arlearn2.android.events.MyAccount;
import org.celstec.arlearn2.client.InquiryClient;
import org.celstec.dao.gen.InquiryLocalObject;

import java.util.ArrayList;
import java.util.List;

public class InquiryActivity extends Activity implements GestureDetector.BaseListener, LocationListener {

    public static final String TAG = "MyActivity";
    public static final int TAP_WONDER_MOMENT = 0;
    public static final int TAP_HYPOTHESIS = 1;
    public static final int TAP_DATA_COLLECTION = 2;
    public static final int TAP_MAP = 3;
    public static final int TAP_SAVE_INQUIRY = 4;

    public static final String EXTRA_PICTURE_FILE_PATH = "picture_file_path";
    public static final String EXTRA_THUMBNAIL_FILE_PATH = "thumbnail_file_path";
    public static final String EXTRA_VIDEO_FILE_PATH = "video_file_path";

    public static final String VIEW_RESULTS = "view_results";

    private static final String LIVE_CARD_TAG = "inquiry";

    private static final int RESULT_WONDER_MOMENT = 0;
    private static final int RESULT_HYPOTHESIS = 1;
    public static final int RESULT_DATA_COLLECTION_PICTURE = 2;
    public static final int RESULT_DATA_COLLECTION_VIDEO = 3;
    public static final int RESULT_DATA_COLLECTION_TEXT = 4;

    // The minimum distance to change Updates in meters
    private static final long MIN_DISTANCE_CHANGE_FOR_UPDATES = 1; // 10 meters
    // The minimum time between updates in milliseconds
    private static final long MIN_TIME_BW_UPDATES = 1000 * 60 * 1;

    private InquiryAdapter mAdapter;
    private CardScrollView mView;
    private GestureDetector mDetector;
    private AudioManager mAudioManager;

    public static int TEMP_MEMBERSHIP = InquiryClient.OPEN_MEMBERSHIP;
    public static int TEMP_VISIBILITY = InquiryClient.VIS_PUBLIC;

    private Bitmap downsampledBitmap;

    private LiveCard mLiveCard;


    private PictureManager man_pic = new PictureManager(this);
    private VideoManager man_vid = new VideoManager(this);
    private AudioInputManager man_aud = new AudioInputManager(this);
    private ValueInputManager man_val = new ValueInputManager(this);
    private TextInputManager man_tex = new TextInputManager(this);

    private static final int SELECT_VALUE = 100;


    final String AUTH_TOKEN_TYPE = "oauth2:https://www.example.com/auth/login";
    public static ArrayList data_collection_images = new ArrayList();
    public static ArrayList data_collection_videos = new ArrayList();

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // New inquiry or existing one
        if (INQ.inquiry.getCurrentInquiry() == null){
            InquiryLocalObject inquiry = new InquiryLocalObject();
            inquiry.setTitle("Demo Inquiry from Google Glass");
            inquiry.setDescription("Demo description from Google Glass");
            CreateInquiryObject createInquiryObject = new CreateInquiryObject();
            createInquiryObject.inquiry = inquiry;

            // To invoke onEventBackgroundThread this line is needed
            INQ.inquiry.setCurrentInquiry(inquiry);
        }

        // Keep the screen on
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        // Set the audio configuration
        mAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);

        // Set the adapter
        mAdapter = new InquiryAdapter(this);

        mView = new CardScrollView(this) {
            @Override
            public final boolean dispatchGenericFocusedEvent(MotionEvent event) {
                if (mDetector.onMotionEvent(event)) {
                    return true;
                }
                return super.dispatchGenericFocusedEvent(event);
            }
        };

        mView.setAdapter(mAdapter);

        setContentView(mView);

        mDetector = new GestureDetector(this).setBaseListener(this);

        // TODO manage authentication
        // manageAuthentication();

    }

    public void onEventBackgroundThread(CreateInquiryObject inquiryObject){
        // * int $vis (Visibility: 0 -> Inquiry members only, 1 -> logged in users, 2 -> Public)
        // int $membership (Membership: 0 -> Closed, 2 -> Open)
        // public void createInquiry(InquiryLocalObject inquiry, AccountLocalObject account, int visibility, int membership)
        PropertiesAdapter pa = PropertiesAdapter.getInstance();
        if (pa != null) {
            String token = pa.getAuthToken();
            if (token != null && ARL.isOnline()) {
                InquiryClient.getInquiryClient().createInquiry(token, inquiryObject.inquiry, INQ.accounts.getLoggedInAccount(), TEMP_VISIBILITY, TEMP_MEMBERSHIP, true);
                INQ.inquiry.syncInquiries();
            }
        }
    }

    private void onEventBackgroundThread(MyAccount myAccount){
        INQ.inquiry.syncInquiries();
    }

    private class CreateInquiryObject {
        public InquiryLocalObject inquiry;
    }

    @Override
    public void onResume() {
        super.onResume();
//        BusFactory.getInstance().register(this);

        mView.activate();
    }

    @Override
    public void onPause() {
        super.onPause();
//        BusFactory.getInstance().unregister(this);
        mView.deactivate();
    }

    @Override
    public boolean onGenericMotionEvent(MotionEvent event) {
        return mDetector.onMotionEvent(event);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_inquriy, menu);

        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {

        boolean data_collection = data_collection_images.size() != 0;

        // TODO visualization of the menu of data collection doesn't work
//        menu.setGroupVisible(R.id.no_data_collection, !data_collection_images);
        menu.setGroupVisible(R.id.data_collection, data_collection);

        MenuUtils.setDescription(menu.findItem(R.id.data_collection_take_picture), R.string.menu_data_collection_take_picture_description);
        MenuUtils.setDescription(menu.findItem(R.id.data_collection_record_video), R.string.menu_data_collection_record_video_description);
        MenuUtils.setDescription(menu.findItem(R.id.data_collection_collect_text), R.string.menu_data_collection_collect_text_description);
        MenuUtils.setDescription(menu.findItem(R.id.data_collection_view_results), R.string.menu_data_collection_view_results_description);

        return super.onPrepareOptionsMenu(menu);    //To change body of overridden methods use File | Settings | File Templates.
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection.
        switch (item.getItemId()) {
            case R.id.data_collection_view_results:
                Log.e(TAG, "View results");

                Intent data_collection_view_results = new Intent(this, ResultsActivity.class);
                startActivity(data_collection_view_results);

                return true;

            case R.id.data_collection_take_picture:
                Log.e(TAG, "Take a picture");
                Intent data_collection_intent_picture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(data_collection_intent_picture, RESULT_DATA_COLLECTION_PICTURE);
                return true;

            case R.id.data_collection_record_video:
                Log.e(TAG, "Record a video");
                Intent data_collection_intent_video = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
                startActivityForResult(data_collection_intent_video, RESULT_DATA_COLLECTION_VIDEO);
                return true;

            case R.id.data_collection_collect_text:
                Log.e(TAG, "Create a note");
                Intent wonder_moment_intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                startActivityForResult(wonder_moment_intent, RESULT_DATA_COLLECTION_TEXT);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onGesture(Gesture gesture) {
        if (gesture == Gesture.TAP) {

            int position = mView.getSelectedItemPosition();
            // here is where you add the tap functionality of each card
            switch (position){
                case TAP_WONDER_MOMENT:
                    Intent wonder_moment_intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                    startActivityForResult(wonder_moment_intent, RESULT_WONDER_MOMENT);
                    break;
                case TAP_HYPOTHESIS:
                    Intent hypothesis_intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
                    startActivityForResult(hypothesis_intent, RESULT_HYPOTHESIS);
                    break;
                case TAP_DATA_COLLECTION:
                    // If we want to collect data we need a data collection

//                    INQ.dataCollection.createDataCollectionTask(
//                            INQ.inquiry.getCurrentInquiry().getRunLocalObject().getGameLocalObject().getId(),
//                            "Data collection title",
//                            "Data collection description"
//                    );

//                    openOptionsMenu();

                    Intent intent = new Intent(this, DataCollectionActivity.class);
                    startActivity(intent);
//                    mAudioManager.playSoundEffect(AudioManager.FX_KEY_CLICK);

                    break;
                case TAP_MAP:
                    break;
                case TAP_SAVE_INQUIRY:
                    finish();
                    break;
            }

            mAudioManager.playSoundEffect(AudioManager.FX_KEY_CLICK);
            return true;
        }
        return false;
    }

    @Override
    public void onBackPressed() {
        Intent resultIntent = new Intent();
        setResult(RESULT_OK, resultIntent);
        super.onBackPressed();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (resultCode == RESULT_OK) {

            // Different responses from the intent result.
            switch (requestCode){
                case RESULT_WONDER_MOMENT:
                    List<String> results_wonder_moment = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    String txt_wonder_moment = results_wonder_moment.get(0);
                    INQ.inquiry.getCurrentInquiry().setReflection(txt_wonder_moment);
                    DaoConfiguration.getInstance().getInquiryLocalObjectDao().insertOrReplace(INQ.inquiry.getCurrentInquiry());
                    mAdapter.notifyDataSetChanged();
                    break;

                case RESULT_HYPOTHESIS:
                    List<String> results_hypothesis = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    String txt_hypothesis = results_hypothesis.get(0);
                    INQ.inquiry.getCurrentInquiry().setHypothesisTitle(txt_hypothesis);
                    DaoConfiguration.getInstance().getInquiryLocalObjectDao().insertOrReplace(INQ.inquiry.getCurrentInquiry());
                    mAdapter.notifyDataSetChanged();
                    break;

                case RESULT_DATA_COLLECTION_PICTURE:
                    final String path_image = data.getStringExtra(EXTRA_PICTURE_FILE_PATH);
                    final Handler mHandler = new Handler();

                    Runnable checkImageTask = new Runnable() {
                        private int counter = 0;
                        public void run() {
                            downsampledBitmap = BitmapUtils.decodeSampledBitmapFromPath(path_image, 200, 200);
                            if (downsampledBitmap == null) {
                                if (counter++ < 1000)
                                    mHandler.postDelayed(this, 500);
                            }else{
                                data_collection_images.add(0, downsampledBitmap);
                                mAdapter.notifyDataSetChanged();
                            }
                        }
                    };
                    mHandler.postDelayed(checkImageTask, 1);

                    break;

                case RESULT_DATA_COLLECTION_VIDEO:
                    final String path_thumbnail_video = data.getStringExtra(EXTRA_THUMBNAIL_FILE_PATH);
                    Log.e(TAG, "On activity result the thumbnail video captured is: "+path_thumbnail_video);

                    final String path_video = data.getStringExtra(EXTRA_VIDEO_FILE_PATH);
                    Log.e(TAG, "On activity result the video captured is: "+path_video);

                    final Handler mHandlerVideo = new Handler();

                    Runnable checkVideoTask = new Runnable() {

                        private int counter = 0;
                        public void run() {
                            downsampledBitmap = BitmapUtils.decodeSampledBitmapFromPath(path_thumbnail_video, 200,
                                    200);
                            if (downsampledBitmap == null) {
                                if (counter++ < 1000)
                                    mHandlerVideo.postDelayed(this, 500);
                            }else{
                                data_collection_images.add(0, downsampledBitmap);
                                data_collection_videos.add(0, path_video);
                            }
                        }
                    };
                    mHandlerVideo.postDelayed(checkVideoTask, 1);

                    break;

                case RESULT_DATA_COLLECTION_TEXT:
                    List<String> results_data_collection_text = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    String txt_data_collection = results_data_collection_text.get(0);

//
//                    // Location
//                    LocationManager locationManager = (LocationManager)  this.getSystemService(LOCATION_SERVICE);
//
//                    // getting GPS status
//                    boolean isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
//
//                    // getting network status
//                    boolean isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
//
//                    List<String> providers = locationManager.getAllProviders();
//                    for (String provider : providers) {
//                        if (locationManager.isProviderEnabled(provider)) {
//                            locationManager.requestLocationUpdates(provider, MIN_DISTANCE_CHANGE_FOR_UPDATES, MIN_TIME_BW_UPDATES, this);
//
//                        }
//                    }

//                    Location location;
//
//                    if (locationManager != null) {
//                        location = locationManager
//                                .getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
//                        if (location != null) {
//                            INQ.inquiry.addData(txt_data_collection, location);
//                            Log.e(TAG, "Location:"+ location.getLatitude()+","+location.getLongitude());
//                        }else{
//                            INQ.inquiry.addData(txt_data_collection, null);
//                        }
//
//                    }else{
//                        INQ.inquiry.addData(txt_data_collection, null);
//                    }


//                    mAdapter.setInquiry(INQ.inquiry);
//                    mView.updateViews(true);
                    break;
            }
        }

        super.onActivityResult(requestCode, resultCode, data);
    }
//
//    private void setLiveCard() {
//        // Launch live card
//        TimelineManager tm = TimelineManager.from(getApplicationContext());
//        mLiveCard = tm.getLiveCard("test");
//
//        //TODO implement a scroll behaviour with the movement of the head over the textview
//        mLiveCard.setViews(new RemoteViews(getApplicationContext().getPackageName(), R.layout.live_card));
//        Intent intent = new Intent(getApplicationContext(), MenuActivity.class);
//        mLiveCard.setAction(PendingIntent.getActivity(getApplicationContext(), 0, intent, 0));
//        mLiveCard.publish();
//    }

    @Override
    public void onLocationChanged(Location location) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void onProviderEnabled(String s) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void onProviderDisabled(String s) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    protected void onDestroy() {

//        if (mLiveCard == null){
//
//            Card a = new Card(getApplicationContext());
//
//            a.setText("asdaksjdhaskljdh");
//
//            mTimelineManager.insert(a);
//
////            RemoteViews views = new RemoteViews(getApplicationContext().getPackageName(), R.layout.live_card);
//
//        }

        super.onDestroy();
    }
}
