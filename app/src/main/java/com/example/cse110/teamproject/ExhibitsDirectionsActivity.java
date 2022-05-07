package com.example.cse110.teamproject;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.AbsoluteSizeSpan;
import android.view.View;
import android.widget.Button;

import java.util.List;

public class ExhibitsDirectionsActivity extends AppCompatActivity {
    final String nextButtonLabel = "Next";
    final String prevButtonLabel = "Previous";
    PathItemDao pathItemDao;
    PathItem pathItem;
    Button prevButton;
    Button nextButton;

    // yame
    List<PathItem> pathItemList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exhibits_directions);

        pathItemDao = ExhibitDatabase.getSingleton(this).pathItemDao();
        pathItem = pathItemDao.getByOrder(0);

        prevButton = findViewById(R.id.previous_button);
        nextButton = findViewById(R.id.next_button);

        pathItemList = pathItemDao.getAll();

        SpannableString string = new SpannableString("\n(something, something)");
        string.setSpan(new AbsoluteSizeSpan(9, true), 0, string.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        String str = nextButtonLabel + string;
        nextButton.setText(str);

        // in what order are the path items stored in the list?
        // what exactly is pathitem.node_id?
        //

    }

    public void onPreviousIconClicked(View view) {
        prevButton.setOnClickListener(v -> {
            pathItem = pathItemDao.getByOrder(pathItem.order--);
            // Textview.setText(pathItem.node_id)... and so on

            if (pathItem.order == 0) {
                prevButton.setEnabled(false);
            }
            else {
                prevButton.setEnabled(true);
            }
        });
    }

    public void onNextIconClicked(View view) {
        prevButton.setOnClickListener(v -> {
            pathItem = pathItemDao.getByOrder(pathItem.order++);
            // Textview.setText(pathItem.node_id)... and so on

            if (pathItem.order == pathItemList.size() - 1) {
                nextButton.setEnabled(false);
            }
            else {
                nextButton.setEnabled(true);
            }
        });
    }

}