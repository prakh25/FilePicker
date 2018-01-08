package com.example.filepicker;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.annotation.RestrictTo;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.example.filepicker.filters.CompositeFilter;
import com.example.filepicker.filters.HiddenFilter;
import com.example.filepicker.filters.PatternFilter;
import com.example.filepicker.util.FileUtils;

import java.io.File;
import java.io.FileFilter;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.regex.Pattern;

/**
 * Created by prakh on 07-01-2018.
 */
@RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
public class FilePickerActivity extends HelperBaseActivity
        implements FilePickerFragment.FileClickListener {

    public static final String SAVE_STATE_START_PATH = "state_start_path";
    private static final String SAVE_STATE_CURRENT_PATH = "state_current_path";

    private String startPath = Environment.getExternalStorageDirectory().getAbsolutePath();
    private String currentPath = startPath;

    private String title;

    private Boolean closeable;

    private CompositeFilter compositeFilter;

    private Toolbar toolbar;

    public static Intent createIntent(Context context, IntentParameters intentParameters) {
        return HelperBaseActivity.createBaseIntent(context, FilePickerActivity.class,
                intentParameters);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fp_activity_file_picker);

        init(savedInstanceState);
        initView();
        initToolbar();
        initBackStackState();
        initFragment();
    }

    private void init(Bundle saveInstanceState) {
        Serializable filter = getFilter(getIntentParameters().showHidden,
                getIntentParameters().fileFilter, getIntentParameters().directoriesFilter);
        compositeFilter = (CompositeFilter) filter;

        if (saveInstanceState != null) {
            startPath = saveInstanceState.getString(SAVE_STATE_START_PATH);
            currentPath = saveInstanceState.getString(SAVE_STATE_CURRENT_PATH);
            updateTitle();
        } else {
            if (getIntentParameters().rootPath != null &&
                    !getIntentParameters().rootPath.isEmpty()) {
                startPath = getIntentParameters().rootPath;
                currentPath = startPath;
            }
            if (getIntentParameters().currentPath != null &&
                    !getIntentParameters().currentPath.isEmpty()) {
                String currentPath = getIntentParameters().currentPath;

                if (currentPath.startsWith(startPath)) {
                    this.currentPath = currentPath;
                }
            }
        }
        title = getIntentParameters().title;

        closeable = getIntentParameters().closeable;
    }

    private CompositeFilter getFilter(boolean showHidden,
                                      Pattern fileFilter,
                                      boolean directoryFilter) {

        ArrayList<FileFilter> filters = new ArrayList<>();

        if (!showHidden) {
            filters.add(new HiddenFilter());
        }

        if (fileFilter != null) {
            filters.add(new PatternFilter(fileFilter, directoryFilter));
        }

        return new CompositeFilter(filters);
    }

    private void initView() {
        toolbar = findViewById(R.id.fp_activity_toolbar);
    }

    private void initToolbar() {
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        try {
            Field f;
            if (TextUtils.isEmpty(title)) {
                f = toolbar.getClass().getDeclaredField("mTitleTextView");
            } else {
                f = toolbar.getClass().getDeclaredField("mSubtitleTextView");
            }

            f.setAccessible(true);
            TextView textView = (TextView) f.get(toolbar);
            textView.setEllipsize(TextUtils.TruncateAt.START);
        } catch (Exception ignored) {
        }

        if (!TextUtils.isEmpty(title)) {
            setTitle(title);
        }
        updateTitle();
    }

    private void initFragment() {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fp_fragment_container, FilePickerFragment.getInstance(
                        currentPath, compositeFilter))
                .addToBackStack(null)
                .commit();
    }

    private void initBackStackState() {
        String pathToAdd = currentPath;
        ArrayList<String> separatedPaths = new ArrayList<>();

        while (!pathToAdd.equals(startPath)) {
            pathToAdd = FileUtils.cutLastSegmentOfPath(pathToAdd);
            separatedPaths.add(pathToAdd);
        }

        Collections.reverse(separatedPaths);

        for (String path : separatedPaths) {
            addFragmentToBackStack(path);
        }
    }

    private void addFragmentToBackStack(String path) {
        getSupportFragmentManager().beginTransaction()
                .setCustomAnimations(R.anim.fp_enter_from_right, R.anim.fp_exit_to_left,
                        R.anim.fp_enter_from_left, R.anim.fp_exit_to_right)
                .replace(R.id.fp_fragment_container, FilePickerFragment.getInstance(
                        path, compositeFilter))
                .addToBackStack(null)
                .commit();
    }

    private void updateTitle() {
        if (getSupportActionBar() != null) {
            String titlePath = currentPath.isEmpty() ? "/" : currentPath;
            if (TextUtils.isEmpty(title)) {
                getSupportActionBar().setTitle(titlePath);
            } else {
                getSupportActionBar().setSubtitle(titlePath);
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.fp_menu_main, menu);
        menu.findItem(R.id.fp_action_close).setVisible(closeable);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == android.R.id.home) {
            onBackPressed();
        } else if(item.getItemId() == R.id.fp_action_close){
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {

        if (!currentPath.equals(startPath)) {
            getSupportFragmentManager().popBackStack();
            currentPath = FileUtils.cutLastSegmentOfPath(currentPath);
            updateTitle();
        } else {
            setResult(RESULT_CANCELED);
            finish();
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(SAVE_STATE_CURRENT_PATH, currentPath);
        outState.putString(SAVE_STATE_START_PATH, startPath);
    }

    @Override
    public void onFileClicked(final File clickedFile) {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                handleFileClicked(clickedFile);
            }
        }, 150);
    }

    private void handleFileClicked(final File clickedFile) {
        if (clickedFile.isDirectory()) {
            currentPath = clickedFile.getPath();
            // If the user wanna go to the emulated directory, he will be taken to the
            // corresponding user emulated folder.
            if (currentPath.equals("/storage/emulated"))
                currentPath = Environment.getExternalStorageDirectory().getAbsolutePath();
            addFragmentToBackStack(currentPath);
            updateTitle();
        } else {
            FilePickerResponse response =
                    new FilePickerResponse.Builder(clickedFile.getPath())
                    .build();
            setResultAndFinish(response);
        }
    }
}
