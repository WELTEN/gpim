package net.wespot.gpim.utils.images;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.widget.ImageView;

import java.lang.ref.WeakReference;

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
public class BitmapWorkerTask extends android.os.AsyncTask<byte[], Void, Bitmap> {

    private static final String TAG = "BitmapWorkerTask";
    private final WeakReference<ImageView> imageViewWeakReference;
    private byte[] resId;

    public BitmapWorkerTask(ImageView imageViewWeakReference) {

        this.imageViewWeakReference = new WeakReference<ImageView>(imageViewWeakReference);
    }

    @Override
    protected Bitmap doInBackground(byte[]... params) {
        resId = params[0];
        ImageView thumbnail = imageViewWeakReference.get();

        Bitmap bitmap = decodeSampledBitmapFromByte(resId, 0, resId.length);

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


}
