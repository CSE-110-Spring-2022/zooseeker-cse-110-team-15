package com.example.cse110.teamproject;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.room.Room;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@RunWith(AndroidJUnit4.class)
public class UserExhibitListDatabaseTest {
    private UserExhibitListItemDao userExhibitListItemDao;
    private ExhibitListItemDao exhibitListItemDao;
    private ExhibitDatabase db;


    @Before
    public void resetDatabase() {
        Context context = ApplicationProvider.getApplicationContext();
        db = Room.inMemoryDatabaseBuilder(context, ExhibitDatabase.class)
                .allowMainThreadQueries().build();
        ExhibitDatabase.injectTestDatabase(db);

        exhibitListItemDao = db.exhibitListItemDao();
        List<ExhibitNodeItem> nodes = ExhibitNodeItem
                .loadJSON(context, "sample_node_info.json");
        exhibitListItemDao.insertAll(nodes);

        userExhibitListItemDao = db.userExhibitListItemDao();
        userExhibitListItemDao.deleteUserExhibitItems();
    }

    @After
    public void closeDb() throws IOException {
        db.close();
    }

    @Test
    public void testInsert() {
        UserExhibitListItem item1 = new UserExhibitListItem("Pizza time");
        UserExhibitListItem item2 = new UserExhibitListItem("Photos Spider-Man");

        long id1 = userExhibitListItemDao.insert(item1);
        long id2 = userExhibitListItemDao.insert(item2);

        assertNotEquals(id1, id2);
    }

    @Test
    public void testGetAllUserExhibits() {
        List<ExhibitNodeItem> actualItems = userExhibitListItemDao.getAllUserExhibits();
        List<ExhibitNodeItem> expectedItems = new ArrayList();

        assertEquals(expectedItems, actualItems);

        ExhibitNodeItem alligatorsNode = exhibitListItemDao.getExhibitByName("Alligators");
        ExhibitNodeItem lionsNode = exhibitListItemDao.getExhibitByName("Lions");

        UserExhibitListItem item1 = new UserExhibitListItem(alligatorsNode.node_id);
        UserExhibitListItem item2 = new UserExhibitListItem(lionsNode.node_id);

        // test first item
        userExhibitListItemDao.insert(item1);
        expectedItems.add(alligatorsNode);
        actualItems = userExhibitListItemDao.getAllUserExhibits();

        assertEquals(expectedItems, actualItems);

        // test second item
        userExhibitListItemDao.insert(item2);
        actualItems = userExhibitListItemDao.getAllUserExhibits();
        expectedItems.add(lionsNode);

        // relying on alligators always being first in returned list and lion second which seems to be
        // the case but idk why. would be good to add an ordering in the db for a guarantee
        assertEquals(expectedItems, actualItems);
    }

    @Test
    public void testGetLive() {
        try {
            LiveData<List<ExhibitNodeItem>> actualItems = userExhibitListItemDao.getAllLive();
            List<ExhibitNodeItem> expectedItems = new ArrayList();

            assertEquals(expectedItems, TestUtil.getOrAwaitValue(actualItems));

            ExhibitNodeItem alligatorsNode = exhibitListItemDao.getExhibitByName("Alligators");
            ExhibitNodeItem lionsNode = exhibitListItemDao.getExhibitByName("Lions");

            UserExhibitListItem item1 = new UserExhibitListItem(alligatorsNode.node_id);
            UserExhibitListItem item2 = new UserExhibitListItem(lionsNode.node_id);

            // test first item
            userExhibitListItemDao.insert(item1);
            expectedItems.add(alligatorsNode);
            actualItems = userExhibitListItemDao.getAllLive();

            assertEquals(expectedItems, TestUtil.getOrAwaitValue(actualItems));

            // test second item
            userExhibitListItemDao.insert(item2);
            actualItems = userExhibitListItemDao.getAllLive();
            expectedItems.add(lionsNode);

            // relying on alligators always being first in returned list and lion second which seems to be
            // the case but idk why. would be good to add an ordering in the db for a guarantee
            assertEquals(expectedItems, TestUtil.getOrAwaitValue(actualItems));
        } catch (Exception e) {}
    }

    @Test
    public void testDeleteUserExhibitItems() {
        List<ExhibitNodeItem> actualItems = userExhibitListItemDao.getAllUserExhibits();
        List<ExhibitNodeItem> expectedItems = new ArrayList();

        assertEquals(expectedItems, actualItems);

        ExhibitNodeItem alligatorsNode = exhibitListItemDao.getExhibitByName("Alligators");
        ExhibitNodeItem lionsNode = exhibitListItemDao.getExhibitByName("Lions");

        UserExhibitListItem item1 = new UserExhibitListItem(alligatorsNode.node_id);
        UserExhibitListItem item2 = new UserExhibitListItem(lionsNode.node_id);

        userExhibitListItemDao.insert(item1);
        userExhibitListItemDao.insert(item2);

        actualItems = userExhibitListItemDao.getAllUserExhibits();
        assertEquals(2, actualItems.size());

        userExhibitListItemDao.deleteUserExhibitItems();
        actualItems = userExhibitListItemDao.getAllUserExhibits();
        assertEquals(expectedItems, actualItems);
    }


    @Test
    public void getListSize() {
        try {
            LiveData<Integer> actualListSize = userExhibitListItemDao.getListSize();

            assertEquals(new Integer(0), TestUtil.getOrAwaitValue(actualListSize));

            ExhibitNodeItem alligatorsNode = exhibitListItemDao.getExhibitByName("Alligators");
            ExhibitNodeItem lionsNode = exhibitListItemDao.getExhibitByName("Lions");

            UserExhibitListItem item1 = new UserExhibitListItem(alligatorsNode.node_id);
            UserExhibitListItem item2 = new UserExhibitListItem(lionsNode.node_id);

            // test first item
            userExhibitListItemDao.insert(item1);
            actualListSize = userExhibitListItemDao.getListSize();
            assertEquals(new Integer(1), TestUtil.getOrAwaitValue(actualListSize));

            // test second item
            userExhibitListItemDao.insert(item2);
            actualListSize = userExhibitListItemDao.getListSize();
            assertEquals(new Integer(2), TestUtil.getOrAwaitValue(actualListSize));
        } catch (Exception e) {};
    }
}
