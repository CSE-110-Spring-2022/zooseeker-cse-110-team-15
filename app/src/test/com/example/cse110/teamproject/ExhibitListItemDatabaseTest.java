package com.example.cse110.teamproject;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import android.content.Context;

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
public class ExhibitListItemDatabaseTest {
    private ExhibitListItemDao dao;
    private ExhibitDatabase db;

    @Before
    public void createDb() {
        Context context = ApplicationProvider.getApplicationContext();
        db = Room.inMemoryDatabaseBuilder(context, ExhibitDatabase.class)
                .allowMainThreadQueries()
                .build();
        dao = db.exhibitListItemDao();
    }

    @After
    public void closeDb() throws IOException {
        db.close();
    }

    @Test
    public void testInsert() {
        ExhibitNodeItem item1 = new ExhibitNodeItem("exhibit", "Elephant", "0", 0f, 0f);
        ExhibitNodeItem item2 = new ExhibitNodeItem("exhibit", "Alligator", "1", 1f, 1f);

        long id1 = dao.insert(item1);
        long id2 = dao.insert(item2);

        assertNotEquals(id1, id2);
    }

    @Test
    public void testInsertAll() {
        ExhibitNodeItem item1 = new ExhibitNodeItem("exhibit", "Elephant", "0", 0f, 0f);
        ExhibitNodeItem item2 = new ExhibitNodeItem("exhibit", "Alligator", "1", 1f, 1f);

        List<ExhibitNodeItem> list = new ArrayList<>();

        list.add(item1);
        list.add(item2);

        List<Long> idList = dao.insertAll(list);

        assertNotEquals(idList.get(0), idList.get(1));
    }

    @Test
    public void testGetExhibit() {
        ExhibitNodeItem insertedItem =  new ExhibitNodeItem("exhibit", "Elephant", "0", 0f, 0f);
        long id = dao.insert(insertedItem);

        ExhibitNodeItem item = dao.getExhibit(id);
        assertEquals(id, item.id);
        assertEquals(insertedItem.kind, item.kind);
        assertEquals(insertedItem.name, item.name);
        assertEquals(insertedItem.node_id, item.node_id);
    }

    @Test
    public void testGetAllExhibits() {
        ExhibitNodeItem item1 = new ExhibitNodeItem("exhibit", "Elephant", "0", 0f, 0f);
        ExhibitNodeItem item2 = new ExhibitNodeItem("exhibit", "Alligator", "1", 1f, 1f);

        long id1 = dao.insert(item1);
        long id2 = dao.insert(item2);

        List<ExhibitNodeItem> list = dao.getAllExhibits();

        assertEquals(id1, list.get(0).id);
        assertEquals(item1.kind, list.get(0).kind);
        assertEquals(item1.name, list.get(0).name);
        assertEquals(item1.node_id, list.get(0).node_id);

        assertEquals(id2, list.get(1).id);
        assertEquals(item2.kind, list.get(1).kind);
        assertEquals(item2.name, list.get(1).name);
        assertEquals(item2.node_id, list.get(1).node_id);
    }

    @Test
    public void testGetExhibits() {
        ExhibitNodeItem item1 = new ExhibitNodeItem("exhibit", "Elephant", "0", 0f, 0f);
        ExhibitNodeItem item2 = new ExhibitNodeItem("exhibit", "Alligator", "1", 1f, 1f);
        ExhibitNodeItem item3 = new ExhibitNodeItem("exhibit", "Monkey", "2", 2f, 2f);

        long id1 = dao.insert(item1);
        long id2 = dao.insert(item2);
        long id3 = dao.insert(item3);

        List<ExhibitNodeItem> list = dao.getExhibits("Alli");

        assertEquals(1, list.size());
        assertEquals(id2, list.get(0).id);
        assertEquals(item2.kind, list.get(0).kind);
        assertEquals(item2.name, list.get(0).name);
        assertEquals(item2.node_id, list.get(0).node_id);

    }

    @Test
    public void testGetExhibitByName() {
        ExhibitNodeItem item1 = new ExhibitNodeItem("exhibit", "Elephant", "0", 0f, 0f);
        ExhibitNodeItem item2 = new ExhibitNodeItem("reptile", "Alligator", "1", 1f, 1f);
        ExhibitNodeItem item3 = new ExhibitNodeItem("exhibit", "Monkey", "2", 2f, 2f);

        long id1 = dao.insert(item1);
        long id2 = dao.insert(item2);
        long id3 = dao.insert(item3);

        ExhibitNodeItem item = dao.getExhibitByName("Elephant");

        assertEquals(id1, item.id);
        assertEquals(item1.kind,item.kind);
        assertEquals(item1.name, item.name);
        assertEquals(item1.node_id, item.node_id);
    }
}
