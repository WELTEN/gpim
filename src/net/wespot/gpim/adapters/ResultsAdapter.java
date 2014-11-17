package net.wespot.gpim.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.VideoView;
import com.example.TestGlass_v1.R;
import com.google.android.glass.widget.CardScrollAdapter;
import net.wespot.gpim.utils.images.ImageFetcher;
import org.celstec.dao.gen.ResponseLocalObject;

import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.net.URL;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
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
public class ResultsAdapter extends CardScrollAdapter {
    private final Context mContext;
    private ArrayList   mValues;
    public static final String TAG = "ViewResultsScrollAdapter";
    private List<ResponseLocalObject> responseLocalObjectList;
    private ImageFetcher mImageFetcher;
    private int mImageThumbSize;
    private int mImageThumbSpacing;

    public ResultsAdapter(Context mContext, List<ResponseLocalObject> responses) {
        this.mContext = mContext;
        Log.i(TAG, "Number data collected: "+ responses.size());
        responseLocalObjectList = responses;

        mImageThumbSize = mContext.getResources().getDimensionPixelSize(R.dimen.data_collect_thumbnail_image_size);
        mImageThumbSpacing = mContext.getResources().getDimensionPixelSize(R.dimen.data_collect_thumbnail_image_spacing);

        mImageFetcher = new ImageFetcher(mContext, mImageThumbSize);
        mImageFetcher.setLoadingImage(R.drawable.ic_taks_photo);
    }

    @Override
    public int getCount() {
        return responseLocalObjectList.size();
    }

    @Override
    public Object getItem(int position) {
        if (position >= 0 && position < responseLocalObjectList.size()) {
            return responseLocalObjectList.get(position);
        }
        return null;
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ResponseLocalObject responseLocalObject = responseLocalObjectList.get(position);


        if (convertView == null){
            convertView = LayoutInflater.from(mContext).inflate(R.layout.card_1_image, parent);

            final TextView[] views = new TextView[] {
                    (TextView) convertView.findViewById(R.id.caption),
                    (TextView) convertView.findViewById(R.id.filter_name)};

            final ImageView[] images = new ImageView[] {
                    (ImageView) convertView.findViewById(R.id.filtered_image)};

            final VideoView[] videos = new VideoView[] {
                    (VideoView) convertView.findViewById(R.id.video)};


            Date date = new Date(responseLocalObject.getTimeStamp());
            Format format = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");


            if (responseLocalObject.isAudio()){
                images[0].setImageDrawable(mContext.getResources().getDrawable(R.drawable.ic_task_record));
            }else if(responseLocalObject.isPicture()){

                //TODO workaround
                String aux = responseLocalObject.getUriAsString();

                if (aux.contains("/0/")){
                    String s = aux.split("/0/")[1];
                    Drawable d = Drawable.createFromPath("storage/emulated/legacy/"+s);
                    images[0].setImageDrawable(d);
                }
                else {
                    try {
                        InputStream is = (InputStream) new URL(aux).getContent();
                        Drawable d = Drawable.createFromStream(is, "src name");
                        images[0].setImageDrawable(d);
                    } catch (Exception e) {
                        return null;
                    }
                }
            }else if (responseLocalObject.isVideo()){
                images[0].setImageDrawable(mContext.getResources().getDrawable(R.drawable.ic_task_video));

//                String aux = responseLocalObject.getUriAsString();
//                String s = aux.split("/0/")[1];
//                videos[0].setVideoPath("storage/emulated/legacy/"+s+".mp4");
//                videos[0].setVisibility(View.VISIBLE);
            }else if (!responseLocalObject.getValue().equals(null)){
                views[0].setText(responseLocalObject.getValue());
                if (responseLocalObject.getAccountLocalObject() != null) {
                    views[1].setText(format.format(date) + " - " + responseLocalObject.getAccountLocalObject().getName());
                }
                else{
                    views[1].setText(format.format(date));
                }
                views[0].setVisibility(View.VISIBLE);
                views[1].setVisibility(View.VISIBLE);
            }else{
//                imageView.setImageDrawable(mContext.getResources().getDrawable(R.drawable.ic_taks_photo));
            }
        }

        return convertView;
    }

    class BitmapWorkerTask extends AsyncTask<byte[], Void, Bitmap> {

        private final WeakReference<ImageView> imageViewWeakReference;
        private byte[] resId;

        public BitmapWorkerTask(ImageView imageViewWeakReference) {

            this.imageViewWeakReference = new WeakReference<ImageView>(imageViewWeakReference);
        }
//

        @Override
        protected Bitmap doInBackground(byte[]... params) {
            resId = params[0];
            ImageView thumbnail = imageViewWeakReference.get();

            Bitmap bitmap = decodeSampledBitmapFromByte(resId,0, resId.length);

            return bitmap;
        }


        @Override
        protected void onPostExecute(Bitmap bitmap) {
            if (isCancelled()){
                bitmap = null;
            }

            if (imageViewWeakReference != null && bitmap != null){
                final ImageView imageView = imageViewWeakReference.get();
                if (imageView != null){
                    imageView.setImageBitmap(bitmap);
                }
            }

            super.onPostExecute(bitmap);    //To change body of overridden methods use File | Settings | File Templates.
        }
    }

    public static Bitmap decodeSampledBitmapFromByte(byte[] res,int option, int len ) {

        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeByteArray(res, option, len);

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, 30, 30);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeByteArray(res, option, len);
    }

    public static int calculateInSampleSize(BitmapFactory.Options options,
                                            int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            // Calculate ratios of height and width to requested height and width
            final int heightRatio = Math.round((float) height / (float) reqHeight);
            final int widthRatio = Math.round((float) width / (float) reqWidth);

            // Choose the smallest ratio as inSampleSize value, this will guarantee a final image
            // with both dimensions larger than or equal to the requested height and width.
            inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;

            // This offers some additional logic in case the image has a strange
            // aspect ratio. For example, a panorama may have a much larger
            // width than height. In these cases the total pixels might still
            // end up being too large to fit comfortably in memory, so we should
            // be more aggressive with sample down the image (=larger inSampleSize).

            final float totalPixels = width * height;

            // Anything more than 2x the requested pixels we'll sample down further
            final float totalReqPixelsCap = reqWidth * reqHeight * 2;

            while (totalPixels / (inSampleSize * inSampleSize) > totalReqPixelsCap) {
                inSampleSize++;
            }
        }
        return inSampleSize;
    }

    @Override
    public int getPosition(Object o) {
        return 0;
    }
}
