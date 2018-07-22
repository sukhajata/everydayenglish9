package com.sukhajata.everydayenglish;


import android.content.Context;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;


/**
 * Created by Administrator on 30/07/2015.
 */
public class IOHelper {

    public static String readFromFile(Context context, int resId) {
        StringBuilder stringBuilder = new StringBuilder();
        try {

            InputStream inputStream = context.getResources().openRawResource(resId);
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            String line;

            while (( line = bufferedReader.readLine()) != null) {
                stringBuilder.append(line);
                stringBuilder.append('\n');
            }

        } catch (IOException ex) {
            Log.e("IOError", ex.getMessage());
            return null;
        }

        return stringBuilder.toString();
    }

}
