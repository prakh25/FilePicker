package com.example.filepicker;

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.CallSuper;

import java.util.IdentityHashMap;
import java.util.regex.Pattern;

/**
 * Created by prakh on 07-01-2018.
 */

public class FilePicker {

    private Activity activity;

    private static final IdentityHashMap<Activity, FilePicker> INSTANCES = new IdentityHashMap<>();

    private FilePicker(Activity activity) {
        this.activity = activity;
    }

    public static FilePicker getInstance(Activity activity) {
        FilePicker filePicker;
        synchronized (INSTANCES) {
            filePicker = INSTANCES.get(activity);
            if (filePicker == null) {
                filePicker = new FilePicker(activity);
                INSTANCES.put(activity, filePicker);
            }
        }
        return filePicker;
    }

    public FilePickerIntentBuilder createFilePickerIntent() {
        return new FilePickerIntentBuilder();
    }

    @SuppressWarnings(value = "unchecked")
    private abstract class PickerIntentBuilder<T extends PickerIntentBuilder> {
        Pattern fileFilter;
        Boolean directoriesFilter = false;
        String rootPath;
        String currentPath;
        Boolean showHidden = false;
        Boolean closeable = true;
        String title;

        private PickerIntentBuilder() {
        }

        public T setFilter(Pattern pattern) {
            fileFilter = pattern;
            return (T) this;
        }

        public T setFilterDirectories(Boolean directoriesFilter) {
            this.directoriesFilter = directoriesFilter;
            return (T) this;
        }

        public T setRootPath(String rootPath) {
            this.rootPath = rootPath;
            return (T) this;
        }

        public T setPath(String path) {
            this.currentPath = path;
            return (T) this;
        }

        public T showHiddenFiles(Boolean showHidden) {
            this.showHidden = showHidden;
            return (T) this;
        }

        public T showCloseMenu(Boolean closeable) {
            this.closeable = closeable;
            return (T) this;
        }

        public T setTitle(String title) {
            this.title = title;
            return (T) this;
        }

        @CallSuper
        public Intent build() {
            return FilePickerActivity.createIntent(activity, getIntentParams());
        }

        protected abstract IntentParameters getIntentParams();
    }

    public final class FilePickerIntentBuilder extends PickerIntentBuilder<FilePickerIntentBuilder> {

        private FilePickerIntentBuilder() {
            super();
        }

        @Override
        protected IntentParameters getIntentParams() {
            return new IntentParameters(
                    title,
                    fileFilter,
                    rootPath,
                    currentPath,
                    directoriesFilter,
                    showHidden,
                    closeable);
        }
    }
}
