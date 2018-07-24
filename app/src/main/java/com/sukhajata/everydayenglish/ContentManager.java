package com.sukhajata.everydayenglish;

import android.content.Context;
import android.util.Log;
import android.widget.ImageView;

import java.lang.reflect.Field;

public class ContentManager {

    public static void fetchImage(Context context, ImageView imgTarget,String imageFileName, String imageUrl) {

        //int id = context.getResources().getIdentifier(imageFileName, "drawable", context.getPackageName());

        imageFileName = imageFileName.substring(0, imageFileName.indexOf('.'));
        Log.d("IMG", imageFileName);
        try {
            Class res = R.drawable.class;
            Field field = res.getField(imageFileName);
            int drawableId = field.getInt(null);
            imgTarget.setImageDrawable(context.getResources().getDrawable(drawableId));
        }
        catch (Exception e) {
            Log.e("IMG", "Failure to get drawable id.", e);
        }
    }
}
