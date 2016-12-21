package ch.usi.inf.mc.yapt.parc;

import android.app.FragmentManager;
import android.content.Intent;
import android.support.v4.view.GestureDetectorCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;


import ch.usi.inf.mc.yapt.parc.activities.PreferencesActivity;
import ch.usi.inf.mc.yapt.parc.fragments.DialogFragment;
import ch.usi.inf.mc.yapt.parc.util.Gesture;
import ch.usi.inf.mc.yapt.parc.service.GestureService;
import ch.usi.inf.mc.yapt.parc.service.SendService;
import ch.usi.inf.mc.yapt.parc.util.GestureHandler;

/**
 * Main Activity of the app
 *
 * This also act as the Gesture handler to coordinate the gesture and send service and decide
 * what to send when a gesture is detected.
 */
public class MainActivity extends AppCompatActivity implements GestureHandler {
    private SendService mSendService;
    private GestureService mGestureService;
    private GestureDetectorCompat mDetector;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mSendService = new SendService(this);
        mGestureService = new GestureService(this, this);
        mDetector = new GestureDetectorCompat(this, mGestureService);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            Intent prefintent = new Intent(this, PreferencesActivity.class);
            startActivity(prefintent);
            return true;
        }
        else if (id == R.id.action_about) {
            FragmentManager fm = getFragmentManager();
            DialogFragment dialogFragment = new DialogFragment();
            Bundle args = new Bundle();
            args.putInt("dialog", R.layout.about_dialog);
            dialogFragment.setArguments(args);
            dialogFragment.show(fm, "Dialog");
            return true;
        }
        else if (id == R.id.action_help) {
            FragmentManager fm = getFragmentManager();
            DialogFragment dialogFragment = new DialogFragment();
            Bundle args = new Bundle();
            args.putInt("dialog", R.layout.help_dialog);
            dialogFragment.setArguments(args);
            dialogFragment.show(fm, "Dialog");
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mGestureService.resume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mGestureService.pause();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event){
        this.mDetector.onTouchEvent(event);
        return super.onTouchEvent(event);
    }

    @Override
    public void notifyGesture(Gesture gesture) {
        mSendService.send(gesture);
    }
}
