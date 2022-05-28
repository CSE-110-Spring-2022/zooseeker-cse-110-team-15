package com.example.cse110.teamproject.util;

import static androidx.test.espresso.core.internal.deps.guava.base.Preconditions.checkNotNull;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.test.espresso.matcher.BoundedMatcher;

import com.example.cse110.teamproject.ExhibitDatabase;
import com.example.cse110.teamproject.ExhibitListItemDao;
import com.example.cse110.teamproject.ExhibitNodeItem;
import com.google.android.gms.maps.model.LatLng;

import org.hamcrest.Description;
import org.hamcrest.Matcher;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class TestUtil {
    // https://stackoverflow.com/questions/31394569/how-to-assert-inside-a-recyclerview-in-espresso
    // CC BY-SA 4.0.
    public static Matcher<View> atPosition(final int position, @NonNull final Matcher<View> itemMatcher) {
        checkNotNull(itemMatcher);
        return new BoundedMatcher<View, RecyclerView>(RecyclerView.class) {
            @Override
            public void describeTo(Description description) {
                description.appendText("has item at position " + position + ": ");
                itemMatcher.describeTo(description);
            }

            @Override
            protected boolean matchesSafely(final RecyclerView view) {
                RecyclerView.ViewHolder viewHolder = view.findViewHolderForAdapterPosition(position);
                if (viewHolder == null) {
                    // has no item on such position
                    return false;
                }
                return itemMatcher.matches(viewHolder.itemView);
            }
        };
    }
    public static LatLng convertNodeIDToLatLng(ExhibitListItemDao exhibitListItemDao, String exhibitID) {
        ExhibitNodeItem exhibit = exhibitListItemDao.getExhibitByNodeId(exhibitID);
        LatLng latLng = new LatLng(exhibit.lat, exhibit.lng);
        return latLng;
    }
    public static List<LatLng> convertNodeIDToLatLng(ExhibitListItemDao exhibitListItemDao, List<String> exhibitIDs) {
        List<LatLng> latLngs = new ArrayList<>();
        for (String id : exhibitIDs) {
            latLngs.add(convertNodeIDToLatLng(exhibitListItemDao, id));
        }
        return latLngs;
    }

    // https://stackoverflow.com/questions/1839668/what-is-the-best-way-to-combine-two-lists-into-a-map-java
    public static <K, V> Map<K, V> zipToMap(List<K> keys, List<V> values) {
        return IntStream.range(0, keys.size()).boxed()
                .collect(Collectors.toMap(keys::get, values::get));
    }
}
