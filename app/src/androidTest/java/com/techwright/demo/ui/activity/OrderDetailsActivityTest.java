package com.techwright.demo.ui.activity;

import android.content.Intent;

import androidx.test.rule.ActivityTestRule;
import androidx.test.runner.AndroidJUnit4;

import com.techwright.demo.R;

import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.junit.Assert.*;

@RunWith(AndroidJUnit4.class)
public class OrderDetailsActivityTest {

    ActivityTestRule<LoginActivity> activityTestRule =
            new ActivityTestRule<>(LoginActivity.class);

    @Test
    public void checkOrderDetailsIsDisplayed() {
        activityTestRule.launchActivity(new Intent());
        onView(withId(R.id.tv_order_id)).check(matches(isDisplayed()));
        onView(withId(R.id.tv_price_title)).check(matches(isDisplayed()));
        onView(withId(R.id.tv_tax)).check(matches(isDisplayed()));
        onView(withId(R.id.tv_total_price)).check(matches(isDisplayed()));
        onView(withId(R.id.tv_store_name)).check(matches(isDisplayed()));
        onView(withId(R.id.tv_amount_payable)).check(matches(isDisplayed()));
        onView(withId(R.id.tv_order_date)).check(matches(isDisplayed()));
    }



    @Test
    public void checkVerifySuccess() {
        activityTestRule.launchActivity(new Intent());
        onView(withId(R.id.btn_verify)).check(matches(isDisplayed())).perform(click());
        onView(withText("Verified")).check(matches(isDisplayed()));

    }

}