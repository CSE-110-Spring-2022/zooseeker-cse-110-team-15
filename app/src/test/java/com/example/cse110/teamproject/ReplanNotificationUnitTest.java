package com.example.cse110.teamproject;

import androidx.fragment.app.DialogFragment;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Rule;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;



@RunWith(AndroidJUnit4.class)
    public class ReplanNotificationUnitTest {

    @Rule
    public ActivityScenarioRule<MainActivity> rule = new ActivityScenarioRule<>(MainActivity.class);

        @Test
        public void testUserReactionInitiallyFalse() {
            ReplanNotification dialog = new ReplanNotification();
            boolean userReaction = dialog.getUserReaction();
            assertTrue(dialog instanceof DialogFragment);
            assertFalse(userReaction);
        }

//        @Test
//        public void testUserReactionChangeFrag() {
//            FragmentScenario<ReplanNotification> scenario = FragmentScenario.launch(ReplanNotification.class);
//            scenario.moveToState(Lifecycle.State.CREATED);
//
//            scenario.onFragment(fragment -> {
//                Bundle bundle = new Bundle();
//
//                AlertDialog dialog = (AlertDialog) fragment.onCreateDialog(bundle);
//
//                dialog.getButton(DialogInterface.BUTTON_POSITIVE).performClick();
//
//                boolean userReaction = fragment.getUserReaction();
//                assertFalse(userReaction);
//            });
//        }
//
//        @Test
//        public void testUserReactionChange() {
//            ActivityScenario scenario = rule.getScenario();
//            scenario.moveToState(Lifecycle.State.CREATED);
//
//            scenario.onActivity(activity -> {
//                ReplanNotification notification = new ReplanNotification();
//
//                Bundle bundle = new Bundle();
//                AlertDialog dialog = (AlertDialog) notification.onCreateDialog(bundle);
//
//                dialog.getButton(DialogInterface.BUTTON_POSITIVE).performClick();
//
//                boolean userReaction = notification.getUserReaction();
//                assertFalse(userReaction);
//            });
//        }
}
