package com.example.cse110.teamproject.persistence;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.example.cse110.teamproject.ExhibitsDirectionsActivity;
import com.example.cse110.teamproject.MainActivity;
import com.example.cse110.teamproject.PlanActivity;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.concurrent.atomic.AtomicBoolean;

public class PersistData {
    private static final String ACTIVITY_FILENAME = "activities.txt";
    private static final String CURR_INDEX_FILENAME = "currIndex.txt";
    private static final String PATH_FILENAME = "path.txt";
    private static AtomicBoolean isRunningTest;

    public enum Activity {
            MAIN,
            PLAN,
            DIRECTIONS
    }

    public enum persistenceKeys {
        PATH,
        INDEX
    }

    public boolean checkIfLastActivityPersisted(Context context) {
        boolean persisted = checkIfFilePersisted(context, ACTIVITY_FILENAME);
        Log.d("persistence", "file exists: " + persisted);
        return persisted;
    }

    public boolean checkIfCurrIndexPersisted(Context context) {
        return checkIfFilePersisted(context, CURR_INDEX_FILENAME);
    }

    public boolean checkIfPathPersisted(Context context) {
        return checkIfFilePersisted(context, PATH_FILENAME);
    }

    public boolean checkIfFilePersisted(Context context, String filename) {
        File file = new File(context.getFilesDir(), filename);
        return file.exists();
    }

    public void writePath(String path, Context context) {
        writeToFile(context, PATH_FILENAME, path);
    }

    public void writeCurrIndex(int currIndex, Context context) {
        writeToFile(context, CURR_INDEX_FILENAME, String.valueOf(currIndex));
    }

    public void writeActivity(Activity activity, Context context) {
        writeToFile(context, ACTIVITY_FILENAME, activity.name());
    }

    public void writeToFile(Context context, String fileName, String data) {
        try (FileOutputStream fos = context.openFileOutput(fileName, Context.MODE_PRIVATE)) {
            fos.write(data.getBytes());
        }
        catch (IOException e) {
            Log.e("Exception", "File write failed: " + e.toString());
        }
    }

    public static void deletePersistedFilesIfExist(Context context) {
        deleteFile(context, CURR_INDEX_FILENAME);
        deleteFile(context, PATH_FILENAME);
        deleteFile(context, ACTIVITY_FILENAME);
    }

    public static void deleteFile(Context context, String fileName) {
        File file = new File(context.getFilesDir(), fileName);
        file.delete();
    }

    public int readCurrIndex(Context context) {
        String ret = readFromFile(context, CURR_INDEX_FILENAME);

        ret = ret.replaceAll("\\s+", "");

        int retVal = Integer.valueOf(ret);

        return retVal;
    }

    public String readPath(Context context) {
        return readFromFile(context, PATH_FILENAME);
    }

    public Activity readActivity(Context context) {
        String ret = readFromFile(context, ACTIVITY_FILENAME);

        ret = ret.replaceAll("\\s+", "");

        Log.d("persistence", "read from file: " + ret);
        Activity retVal = Activity.PLAN;
        switch (ret) {
            case "DIRECTIONS":
                retVal = Activity.DIRECTIONS;
                break;
            case "PLAN":
                retVal = Activity.PLAN;
                break;
            case "MAIN":
                retVal = Activity.MAIN;
                break;
            default:
        }

        return retVal;
    }

    // https://stackoverflow.com/questions/14376807/read-write-string-from-to-a-file-in-android
    public String readFromFile(Context context, String filename) {
        String ret = "";

        try {
            InputStream inputStream = context.openFileInput(filename);

            if ( inputStream != null ) {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String receiveString = "";
                StringBuilder stringBuilder = new StringBuilder();

                while ( (receiveString = bufferedReader.readLine()) != null ) {
                    stringBuilder.append("\n").append(receiveString);
                }

                inputStream.close();
                ret = stringBuilder.toString();

                // remove whitespace
                }
                context.deleteFile(filename);
            }
        catch (FileNotFoundException e) {
            Log.e("login activity", "File not found: " + e.toString());
        } catch (IOException e) {
            Log.e("login activity", "Can not read file: " + e.toString());
        }

        return ret;
    }

    public void resume(Activity currActivity, Context context) {
        Log.d("persistence", "test running " + isRunningTest());
        if (checkIfLastActivityPersisted(context) && !isRunningTest()) {
            PersistData.Activity activity = readActivity(context);
            Log.d("persistence", "switching: " + activity.name());
            Intent intent;
            switch (activity) {
                case DIRECTIONS:
                    if (activity == currActivity) {
                        return;
                    }
                    Log.d("persistence", "go to directions");
                    intent = new Intent(context, ExhibitsDirectionsActivity.class);
                    Log.d("persistence_directions", "curr path, order" + checkIfCurrIndexPersisted(context) + " " + checkIfPathPersisted(context));
                    if (checkIfCurrIndexPersisted(context) && checkIfPathPersisted(context)) {
                        int index = readCurrIndex(context);
                        Log.d("persistence_directions", "added path");
                        String path = readPath(context);
                        intent.putExtra(persistenceKeys.INDEX.name(), index);
                        intent.putExtra(persistenceKeys.PATH.name(), path);
                    }
                    context.startActivity(intent);
                    break;
                case PLAN:
                    if (activity == currActivity) {
                        return;
                    }
                    Log.d("persistence", "go to plan");
                    intent = new Intent(context, PlanActivity.class);
                    context.startActivity(intent);
                    break;
                case MAIN:
                    if (activity == currActivity) {
                        return;
                    }
                    Log.d("persistence", "go to main");
                    intent = new Intent(context, MainActivity.class);
                    context.startActivity(intent);
                    break;
                default:
            }
            Log.d("persistence", "not resuming from file");
        }
    }

    // https://stackoverflow.com/questions/28550370/how-to-detect-whether-android-app-is-running-ui-test-with-espresso

    public synchronized boolean isRunningTest () {
        if (null == isRunningTest) {
            boolean istest;

            try {
                Class.forName ("androidx.test.espresso.Espresso");
                istest = true;
            } catch (ClassNotFoundException e) {
                istest = false;
            }

            isRunningTest = new AtomicBoolean (istest);
        }

        return isRunningTest.get ();
    }
}

