package pikcel.com.pikcelclient;

import android.content.Context;
import android.graphics.Typeface;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class FontManager {

    private Typeface light, bold, regular, italic, medium;
    private Context mContext;

    public FontManager(Context applicationContext){
        mContext = applicationContext;
        light = Typeface.createFromAsset(mContext.getAssets(),"Ubuntu-Light.ttf");
        bold = Typeface.createFromAsset(mContext.getAssets(),"Ubuntu-Bold.ttf");
        regular = Typeface.createFromAsset(mContext.getAssets(),"Ubuntu-Regular.ttf");
        italic = Typeface.createFromAsset(mContext.getAssets(),"Ubuntu-Italic.ttf");
        medium = Typeface.createFromAsset(mContext.getAssets(),"Ubuntu-Medium.ttf");
    }

    public Typeface getBold() {
        return bold;
    }

    public Typeface getItalic() {
        return italic;
    }

    public Typeface getLight() {
        return light;
    }

    public Typeface getMedium() {
        return medium;
    }

    public Typeface getRegular() {
        return regular;
    }

    public void replaceFonts(ViewGroup viewTree, Typeface typeface)
    {
        View child;
        for(int i = 0; i < viewTree.getChildCount(); ++i)
        {
            child = viewTree.getChildAt(i);
            if(child instanceof ViewGroup)
            {
                // recursive call
                replaceFonts((ViewGroup)child, typeface);
            }
            else if(child instanceof TextView)
            {
                // base case
                ((TextView) child).setTypeface(typeface);
            }
        }
    }
}
