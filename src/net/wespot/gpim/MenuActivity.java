package net.wespot.gpim;

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
import android.app.Activity;
import android.content.Intent;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import com.example.TestGlass_v1.R;
import com.google.android.glass.view.MenuUtils;

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
public class MenuActivity extends Activity {

    @Override
    public void onResume() {
        super.onResume();
        openOptionsMenu();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        MenuUtils.setDescription(menu.findItem(R.id.resume_inquiry), R.string.menu_option_1_description);
        MenuUtils.setDescription(menu.findItem(R.id.stop_inquiry), R.string.menu_option_2_description);
        MenuUtils.setDescription(menu.findItem(R.id.take_picture), R.string.menu_option_3_description);
        MenuUtils.setDescription(menu.findItem(R.id.record_video), R.string.menu_option_4_description);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection.
        switch (item.getItemId()) {
            case R.id.resume_inquiry:
                Intent setTimerIntent = new Intent(this, MainActivity.class);
                startActivity(setTimerIntent);
                return true;
            case R.id.stop_inquiry:
//                stopService(new Intent(this, MyActivity.class));
//                LiveCardManager.getInstance(getApplicationContext()).unpublish();
                // TODO delete the inquiry
                return true;
            case R.id.take_picture:
                Intent data_collection_intent_picture = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(data_collection_intent_picture, InquiryActivity.RESULT_DATA_COLLECTION_PICTURE);
//                Intent navIntent = new Intent(Intent.ACTION_VIEW,
//                        Uri.parse("google.navigation:ll=37.4219795, - 122.0836669 & title = Googleplex"));
//                startActivity(navIntent);
                return true;
            case R.id.record_video:

                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onOptionsMenuClosed(Menu menu) {
        // Nothing else to do, closing the activity.
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }
}