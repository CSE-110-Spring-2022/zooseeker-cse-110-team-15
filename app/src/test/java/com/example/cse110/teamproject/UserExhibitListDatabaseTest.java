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

import java.util.ArrayList;
import java.util.List;

@RunWith(AndroidJUnit4.class)
public class UserExhibitListDatabaseTest {
    private UserExhibitListItemDao userExhibitListItemDao;
    private ExhibitListItemDao exhibitListItemDao;
    private ExhibitDatabase testDb;

    @Before
    public void setUp()  {
        Context context = ApplicationProvider.getApplicationContext();
        testDb = Room.inMemoryDatabaseBuilder(context, ExhibitDatabase.class)
                .allowMainThreadQueries().build();

        List<ExhibitNodeItem> nodes = ExhibitNodeItem
                .loadJSON(context, "zoo_node_info.json");
        System.out.println("nodes from json: " + nodes.toString());
        exhibitListItemDao = testDb.exhibitListItemDao();
        exhibitListItemDao.insertAll(nodes);
        userExhibitListItemDao = testDb.userExhibitListItemDao();

        ExhibitDatabase.injectTestDatabase(testDb);
    }

    @After
    public void tearDown() {
        ExhibitDatabase.resetSingleton();
        testDb.close();
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

        ExhibitNodeItem flamingosNode = exhibitListItemDao.getExhibitByName("Flamingos");
        ExhibitNodeItem monkeysMode = exhibitListItemDao.getExhibitByName("Capuchin Monkeys");

        UserExhibitListItem item1 = new UserExhibitListItem(flamingosNode.node_id);
        UserExhibitListItem item2 = new UserExhibitListItem(monkeysMode.node_id);

        // test first item
        userExhibitListItemDao.insert(item1);
        expectedItems.add(flamingosNode);
        actualItems = userExhibitListItemDao.getAllUserExhibits();

        assertEquals(expectedItems, actualItems);

        // test second item
        userExhibitListItemDao.insert(item2);
        actualItems = userExhibitListItemDao.getAllUserExhibits();
        expectedItems.add(monkeysMode);

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

            ExhibitNodeItem flamingosNode = exhibitListItemDao.getExhibitByName("Flamingos");
            ExhibitNodeItem monkeysMode = exhibitListItemDao.getExhibitByName("Capuchin Monkeys");

            UserExhibitListItem item1 = new UserExhibitListItem(flamingosNode.node_id);
            UserExhibitListItem item2 = new UserExhibitListItem(monkeysMode.node_id);

            // test first item
            userExhibitListItemDao.insert(item1);
            expectedItems.add(flamingosNode);
            actualItems = userExhibitListItemDao.getAllLive();

            assertEquals(expectedItems, TestUtil.getOrAwaitValue(actualItems));

            // test second item
            userExhibitListItemDao.insert(item2);
            actualItems = userExhibitListItemDao.getAllLive();
            expectedItems.add(monkeysMode);

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

        ExhibitNodeItem flamingosNode = exhibitListItemDao.getExhibitByName("Flamingos");
        ExhibitNodeItem monkeysMode = exhibitListItemDao.getExhibitByName("Capuchin Monkeys");

        UserExhibitListItem item1 = new UserExhibitListItem(flamingosNode.node_id);
        UserExhibitListItem item2 = new UserExhibitListItem(monkeysMode.node_id);

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

            ExhibitNodeItem flamingosNode = exhibitListItemDao.getExhibitByName("Flamingos");
            ExhibitNodeItem monkeysMode = exhibitListItemDao.getExhibitByName("Capuchin Monkeys");

            UserExhibitListItem item1 = new UserExhibitListItem(flamingosNode.node_id);
            UserExhibitListItem item2 = new UserExhibitListItem(monkeysMode.node_id);

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
