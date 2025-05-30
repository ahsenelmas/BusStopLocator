package com.example.busstoplocater;

import android.content.Intent;

import androidx.test.core.app.ActivityScenario;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.example.busstoplocater.view.MainActivity;

import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.*;

@RunWith(AndroidJUnit4.class)
public class MainActivityTest {

    @Test
    public void testMapAndLogoVisible() {
        Intent intent = new Intent(ApplicationProvider.getApplicationContext(), MainActivity.class);
        intent.putExtra("lat", 52.23);
        intent.putExtra("lon", 21.01);

        ActivityScenario.launch(intent);

        onView(withId(R.id.map)).check(matches(isDisplayed()));
        onView(withId(R.id.btnHomeLogo)).check(matches(isDisplayed())).check(matches(isClickable()));
    }

    @Test
    public void testSingleStopZoomsIn() {
        Intent intent = new Intent(ApplicationProvider.getApplicationContext(), MainActivity.class);
        intent.putExtra("stop_name", "Kijowska");

        ActivityScenario.launch(intent);

        onView(withId(R.id.map)).check(matches(isDisplayed()));
    }

    @Test
    public void testNearbyStopsUsingGpsCoordinates() {

        Intent intent = new Intent(ApplicationProvider.getApplicationContext(), MainActivity.class);
        intent.putExtra("lat", 52.2297);
        intent.putExtra("lon", 21.0122);

        ActivityScenario.launch(intent);

        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        onView(withId(R.id.map)).check(matches(isDisplayed()));
    }

    @Test
    public void testNoNearbyStopsWithRemoteGps() {
        Intent intent = new Intent(ApplicationProvider.getApplicationContext(), MainActivity.class);
        intent.putExtra("lat", 0.0);
        intent.putExtra("lon", -30.0);

        ActivityScenario.launch(intent);

        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        onView(withId(R.id.map)).check(matches(isDisplayed()));
    }

}