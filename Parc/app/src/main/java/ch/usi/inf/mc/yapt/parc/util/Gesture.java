package ch.usi.inf.mc.yapt.parc.util;

import ch.usi.inf.mc.yapt.parc.R;


public enum Gesture {
    /** Swipe up on the screen. */
    SWIPE_UP(R.string.gesture_swipe_up),
    /** Swipe right on the screen. */
    SWIPE_RIGHT(R.string.gesture_swipe_right),
    /** Swipe down on the screen. */
    SWIPE_DOWN(R.string.gesture_swipe_down),
    /** Swipe left on the screen. */
    SWIPE_LEFT(R.string.gesture_swipe_left),
    /** Long press on the screen. */
    SINGLE_TAP(R.string.gesture_long_press),
    /** Hover (hold) over the phone */
    HOVER_HOLD(R.string.gesture_hover_hold),
    /** Hover (swipe) over the phone */
    HOVER_SWIPE(R.string.gesture_hover_swipe);

    public final int id;

    /**
     * All the available gestures
     *
     * @param id The preferences id to retrieve the action associated with the gesture.
     */
    Gesture(int id) {
        this.id = id;
    }
}
