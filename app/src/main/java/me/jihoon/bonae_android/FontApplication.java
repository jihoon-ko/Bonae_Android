package me.jihoon.bonae_android;

import android.app.Application;
import android.content.Context;
import android.graphics.Typeface;

import java.lang.reflect.Field;

/**
 * Created by q on 2017-07-19.
 */

public class FontApplication extends Application {
    @Override
    public void onCreate() {
        setDefaultFont(this, "DEFAULT", "Binggrae.ttf");
        setDefaultFont(this, "SANS_SERIF", "font.ttf");
        setDefaultFont(this, "SERIF", "tvnfont.ttf");
    }

    public static void setDefaultFont(Context context, String staticTypefaceFieldName, String fontAssetName) {
        final Typeface regular = Typeface.createFromAsset(context.getAssets(), fontAssetName);
        replaceFont(staticTypefaceFieldName, regular);
    }

    protected static void replaceFont(String staticTypefaceFieldName, final Typeface newTypeface) {
        try {
            final Field StaticField = Typeface.class.getDeclaredField(staticTypefaceFieldName);
            StaticField.setAccessible(true);
            StaticField.set(null, newTypeface);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

}
