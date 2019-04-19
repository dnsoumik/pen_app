package in.xlayer.f2h.driver.other;

import android.content.Context;
import android.media.MediaPlayer;

import in.xlayer.f2h.driver.R;


public class AlertSoundBuilder {

    private Context context;
    private MediaPlayer notifySound;

    public AlertSoundBuilder(Context context) {
        this.context = context;
        notifySound = MediaPlayer.create(context, R.raw.notify_v3);
    }

    public void startSound() {
        notifySound = MediaPlayer.create(context, R.raw.notify_v3);
        notifySound.setLooping(false);
        notifySound.start();
    }

    public void stopSound() {
        notifySound.stop();
    }

}
