package com.guillaume.myapplication;

import org.junit.Test;
import org.robolectric.Robolectric;
import org.robolectric.RuntimeEnvironment;

import static org.junit.Assert.*;
import static org.robolectric.Shadows.shadowOf;

import android.content.Intent;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() {
        assertEquals(4, 2 + 2);
    }

    @Test
    public void clickFacebookLogin_shouldStartNavigationActivity()  {
        /*ActivityScenario<MainActivity> scenario = ActivityScenario.launch(MainActivity.class);
        scenario.onActivity(activity ->
                activity.findViewById(R.id.facebook_login_button).performClick());*/
        MainActivity mainActivity = Robolectric.setupActivity(MainActivity.class);
        mainActivity.findViewById(R.id.facebook_login_button).performClick();

        //Intent expectedIntent = ActivityScenario.launch(new Intent(MainActivity.class, NavigationActivity.class));
        Intent expectedIntent = new Intent(mainActivity, NavigationActivity.class);
        Intent actual = shadowOf(RuntimeEnvironment.application).getNextStartedActivity();
        assertEquals(expectedIntent.getComponent(), actual.getComponent());
    }
}