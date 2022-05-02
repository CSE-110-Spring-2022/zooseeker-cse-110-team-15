//package com.example.cse110.teamproject;
//
//import static androidx.test.espresso.Espresso.onData;
//import static androidx.test.espresso.Espresso.onView;
//import static androidx.test.espresso.action.ViewActions.click;
//import static androidx.test.espresso.action.ViewActions.replaceText;
//import static androidx.test.espresso.action.ViewActions.typeText;
//import static androidx.test.espresso.assertion.ViewAssertions.matches;
//import static androidx.test.espresso.matcher.ViewMatchers.hasDescendant;
//import static androidx.test.espresso.matcher.ViewMatchers.withId;
//import static androidx.test.espresso.matcher.ViewMatchers.withText;
//
//import android.content.Context;
//import android.util.Log;
//import android.view.View;
//import android.widget.AdapterView;
//import android.widget.ArrayAdapter;
//
//import android.widget.AutoCompleteTextView;
//import android.widget.EditText;
//import android.widget.TextView;
//
//import androidx.annotation.NonNull;
//import androidx.annotation.VisibleForTesting;
//import androidx.lifecycle.Lifecycle;
//import androidx.recyclerview.widget.RecyclerView;
//import androidx.room.Room;
//import androidx.test.core.app.ActivityScenario;
//import androidx.test.core.app.ApplicationProvider;
//import androidx.test.espresso.matcher.BoundedMatcher;
//import androidx.test.espresso.matcher.RootMatchers;
//import androidx.test.ext.junit.runners.AndroidJUnit4;
//
//import org.hamcrest.Description;
//import org.hamcrest.Matcher;
//import org.junit.After;
//import org.junit.Before;
//import org.junit.Rule;
//import org.junit.Test;
//import org.junit.runner.RunWith;
//
//import static org.hamcrest.CoreMatchers.equalTo;
//import static org.junit.Assert.*;
//
//import java.util.List;
//
///**
// * Instrumented test, which will execute on an Android device.
// *
// * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
// */
//@RunWith(AndroidJUnit4.class)
//public class SearchAndAddExhibitsTest {
//    ExhibitDatabase testDb;
//    ExhibitListItemDao exhibitListItemDao;
//    UserExhibitListItemDao userExhibitListItemDao;
//
//    @Before
//    public void resetDatabase() {
//        Context context = ApplicationProvider.getApplicationContext();
//        testDb = Room.inMemoryDatabaseBuilder(context, ExhibitDatabase.class)
//                .allowMainThreadQueries().build();
//        ExhibitDatabase.injectTestDatabase(testDb);
//
//        exhibitListItemDao = testDb.exhibitListItemDao();
//        List<ExhibitNodeItem> nodes = ExhibitNodeItem
//                .loadJSON(context, "sample_node_info.json");
//        exhibitListItemDao.insertAll(nodes);
//
//        userExhibitListItemDao = testDb.userExhibitListItemDao();
//        userExhibitListItemDao.deleteUserExhibitItems();
//    }
//
//    public static void forceLayout(RecyclerView recyclerView) {
//        recyclerView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
//        recyclerView.layout(0, 0, 1080, 2280);
//    }
//
////    @After
////    public void tearDown() {
////        testDb.close();
////    }
//
//    @Test
//    public void testAddFromDropdownAddsToUserExhibitListItemDB() {
//        ActivityScenario<MainActivity> scenario
//                = ActivityScenario.launch(MainActivity.class);
//        scenario.moveToState(Lifecycle.State.CREATED);
//        scenario.moveToState(Lifecycle.State.STARTED);
//        scenario.moveToState(Lifecycle.State.RESUMED);
//
//        scenario.onActivity(activity -> {
//            AutoCompleteTextView searchBar = activity.findViewById(R.id.search_bar);
//            searchBar.requestFocus();
//            searchBar.setText("Alligators");
//            searchBar.showDropDown();
//            Log.d("lskjdflkj", searchBar.getAdapter().getItem(0).toString());
//
////            final String[] result = new String[1];
////            searchBar.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
////                @Override
////                public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
////                    result[0] = adapterView.getItemAtPosition(i).toString();
////                    assertEquals("Alligators", result[0]);
////                    view.performClick();
////
////
////                    searchBar.setListSelection(0);
////                    searchBar.performClick();
//
//                    // test adding to list
//
////            assertEquals(1,userExhibitListItemDao.getListSize());
////
////                    RecyclerView userListRecycler = activity.userListRecycler;
////                    String text = ((TextView) userListRecycler.findViewHolderForAdapterPosition(0).itemView.findViewById(R.id.user_list)).getText().toString();
////                    long firstUserListItemID = userListRecycler.getAdapter().getItemId(0);
////                    TextView firstUserListItem = activity.findViewById((int) firstUserListItemID);
////                    assertEquals("Alligators", text);
////                }
////
////                @Override
////                public void onNothingSelected(AdapterView<?> adapterView) {}
////            });
////
////
////            ArrayAdapter<String> dropDownAdapter = activity.arrayAdapter;
////            long firstResultID = dropDownAdapter.getItemId(0);
////            TextView firstResult = activity.findViewById(firstResultID);
////
////            String exhibit = (String) firstResult.getText();
////            assertEquals("Alligators", exhibit);
//                });
//            }
//        }
