package ch.usi.inf.mc.yapt.parc.service;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Handler;
import android.os.Message;
import android.view.GestureDetector;
import android.view.MotionEvent;

import ch.usi.inf.mc.yapt.parc.util.GestureHandler;
import ch.usi.inf.mc.yapt.parc.util.Gesture;

public class GestureService extends GestureDetector.SimpleOnGestureListener
        implements SensorEventListener {
    /** Min distance to consider motion as an actual swipe */
    private static final int SWIPE_THRESHOLD = 100;
    /** Min velocity to consider motion as an actual swipe */
    private static final int SWIPE_VELOCITY_THRESHOLD = 100;
    /** Min duration to conisder hover as holding otherwise swipe */
    private static final int HOLD_TIME = 500;

    /** Message type for Hover Handler */
    private static final int HOVER_HOLD = 0;
    /** Message type for Hover Handler */
    private static final int HOVER_SWIPE = 1;

    private final GestureHandler mGestureHandler;
    private SensorManager mSensorManager;
    private Sensor mProximity;
    private Handler mHoverHandler;

    /**
     * Service to detect various gestures.
     *
     * So far the following gestures are supported:
     *  - swipe (four directions)
     *  - long press
     *  - hover hold (proximity sensor)
     *  - hover swipe (proximity sensor)
     *
     *  When a gesture is detected, the `GesutureHandler` is called with the proper Gesture.
     *
     *  @param context The context of the application.
     *  @param gestureHandler The handler to notify of gestures.
     *
     */
    public GestureService(Context context, GestureHandler gestureHandler) {
        mSensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        mProximity = mSensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);
        mSensorManager.registerListener(this, mProximity, SensorManager.SENSOR_DELAY_FASTEST);

        mGestureHandler = gestureHandler;
        mHoverHandler = new HoverHandler();
    }

    /** Must be called when the activity holding the service is resumed. */
    public void resume() {
        mSensorManager.registerListener(this, mProximity, SensorManager.SENSOR_DELAY_FASTEST);
    }

    /** Must be called when the activity holding the service is paused. */
    public void pause() {
        mSensorManager.unregisterListener(this);
    }

    /** Required to detect other events. */
    @Override
    public boolean onDown(MotionEvent event) {
        return true;
    }

    /** Handle long press events */
    @Override
    public void onLongPress(MotionEvent e) {
        mGestureHandler.notifyGesture(Gesture.SINGLE_TAP);
    }

    /**
     * Handle fling events (for swipe motions)
     *
     * Based on https://goo.gl/53k1WV
     */
    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        boolean result = false;
        try {
            float diffY = e2.getY() - e1.getY();
            float diffX = e2.getX() - e1.getX();
            if (Math.abs(diffX) > Math.abs(diffY)) {
                if (Math.abs(diffX) > SWIPE_THRESHOLD
                        && Math.abs(velocityX) > SWIPE_VELOCITY_THRESHOLD) {
                    if (diffX > 0) {
                        mGestureHandler.notifyGesture(Gesture.SWIPE_RIGHT);
                    } else {
                        mGestureHandler.notifyGesture(Gesture.SWIPE_LEFT);
                    }
                }
                result = true;
            }
            else if (Math.abs(diffY) > SWIPE_THRESHOLD
                    && Math.abs(velocityY) > SWIPE_VELOCITY_THRESHOLD) {
                if (diffY > 0) {
                    mGestureHandler.notifyGesture(Gesture.SWIPE_DOWN);
                } else {
                    mGestureHandler.notifyGesture(Gesture.SWIPE_UP);
                }
            }
            result = true;

        } catch (Exception exception) {
            exception.printStackTrace();
        }

        return result;
    }

    /**
     * Handle proximity events to detect hover motions.
     *
     * Based on android.view.GestureDetector.onTouchEvent(MotionEvent ev) and https://goo.gl/dlBzSO
     */
    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_PROXIMITY) {
            boolean hasMessages = mHoverHandler.hasMessages(HOVER_HOLD);

            if (event.values[0] >= -0.01 && event.values[0]<= 0.01) {
                // hover near
                if (!hasMessages) {
                    mHoverHandler.sendEmptyMessageDelayed(HOVER_HOLD, HOLD_TIME);
                }
            } else if (hasMessages) {
                mHoverHandler.removeMessages(HOVER_HOLD);
                mHoverHandler.sendEmptyMessage(HOVER_SWIPE);
            }
        }
    }

    /** Handle sensor accuracy change (not needed) */
    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {}

    private class HoverHandler extends Handler {
        HoverHandler() {
            super();
        }


        /**
         * Handle hover realted events
         *
         * Based on Based on android.view.GestureDetector.GestureHandler
         */
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case HOVER_HOLD:
                    mGestureHandler.notifyGesture(Gesture.HOVER_HOLD);
                    break;

                case HOVER_SWIPE:
                    mGestureHandler.notifyGesture(Gesture.HOVER_SWIPE);
                    break;
                default:
                    throw new RuntimeException("Unknown message " + msg); //never
            }
        }
    }
}
