package net.wespot.gpim;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Bundle;
import android.view.*;
import com.google.android.glass.touchpad.Gesture;
import com.google.android.glass.touchpad.GestureDetector;
import com.google.android.glass.widget.CardScrollView;
import daoBase.DaoConfiguration;
import net.wespot.gpim.adapters.InquiriesAdapter;
import org.celstec.arlearn.delegators.INQ;
import org.celstec.arlearn2.android.events.MyAccount;
import org.celstec.dao.gen.InquiryLocalObject;

import java.util.List;

public class MainActivity extends Activity implements GestureDetector.BaseListener {

    public static final String TAG = "MyActivity";

    private InquiriesAdapter inquiriesAdapter;
    private CardScrollView mView;
    private GestureDetector mDetector;
    private AudioManager mAudioManager;

    private List<InquiryLocalObject> inquiryLocalObjectList;

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        INQ.init(this);
        INQ.eventBus.register(this);

        INQ.accounts.disAuthenticate();
        INQ.properties.setAccount(1l);
        INQ.properties.setFullId("2:117769871710404943583");

        // Authenticate the user
        INQ.properties.setAuthToken("ya29.lgBVhf29NWlBqgWx91sxfZNrumLJeR0IVI8kDZABaE8pON2Vqs-Ps-ZM");
        INQ.properties.setIsAuthenticated();
        INQ.accounts.syncMyAccountDetails();

        if (INQ.accounts.getLoggedInAccount() != null){
            INQ.inquiry.syncInquiries();
        }

        // Keep the screen on
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        // Set the audio configuration
        mAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);

        inquiryLocalObjectList = DaoConfiguration.getInstance().getInquiryLocalObjectDao().loadAll();

        // Set the adapter
        inquiriesAdapter = new InquiriesAdapter(this, inquiryLocalObjectList);

        mView = new CardScrollView(this) {
            @Override
            public final boolean dispatchGenericFocusedEvent(MotionEvent event) {
                if (mDetector.onMotionEvent(event)) {
                    return true;
                }
                return super.dispatchGenericFocusedEvent(event);
            }
        };

        mView.setAdapter(inquiriesAdapter);

        setContentView(mView);

        mDetector = new GestureDetector(this).setBaseListener(this);
    }

    public void onEventBackgroundThread(MyAccount myAccount){
        INQ.inquiry.syncInquiries();
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
    public boolean onGesture(Gesture gesture) {
        if (gesture == Gesture.TAP) {

            int position = mView.getSelectedItemPosition();

            Intent intent = new Intent(this, InquiryActivity.class);
            INQ.inquiry.setCurrentInquiry(inquiryLocalObjectList.get(position));
            INQ.inquiry.syncDataCollectionTasks(INQ.inquiry.getCurrentInquiry());

            startActivity(intent);
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
//                case RESULT_WONDER_MOMENT:
//                    List<String> results_wonder_moment = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
//                    String txt_wonder_moment = results_wonder_moment.get(0);
//                    INQ.inquiry.getCurrentInquiry().setReflection(txt_wonder_moment);
//                    DaoConfiguration.getInstance().getInquiryLocalObjectDao().insertOrReplace(INQ.inquiry.getCurrentInquiry());
//                    inquiriesAdapter.notifyDataSetChanged();
//                    break;
            }
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onDestroy() {
        INQ.eventBus.unregister(this);
        super.onDestroy();

    }
}
