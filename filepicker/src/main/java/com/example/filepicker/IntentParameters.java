package com.example.filepicker;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RestrictTo;

import com.example.filepicker.util.PreConditions;

import java.util.regex.Pattern;

/**
 * Created by prakh on 07-01-2018.
 */
@RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
public class IntentParameters implements Parcelable {
    @NonNull
    public final String title;

    @Nullable
    public final Pattern fileFilter;

    @Nullable
    public final String rootPath;

    @Nullable
    public final String currentPath;

    public final boolean directoriesFilter;
    public final boolean showHidden;
    public final boolean closeable;


    public IntentParameters(@NonNull String title,
                            @Nullable Pattern fileFilter,
                            @Nullable String rootPath,
                            @Nullable String currentPath,
                            boolean directoriesFilter,
                            boolean showHidden,
                            boolean closeable) {
        this.title = PreConditions.checkNotNull(title, "App Name cannot be null");
        this.fileFilter = fileFilter;
        this.rootPath = rootPath;
        this.currentPath = currentPath;
        this.directoriesFilter = directoriesFilter;
        this.showHidden = showHidden;
        this.closeable = closeable;
    }

    public static IntentParameters fromIntent(Intent intent) {
        return intent.getParcelableExtra(Constants.EXTRA_INTENT_PARAMS);
    }

    public static IntentParameters fromBundle(Bundle bundle) {
        return bundle.getParcelable(Constants.EXTRA_INTENT_PARAMS);
    }

    public Bundle toBundle() {
        Bundle bundle = new Bundle();
        bundle.putParcelable(Constants.EXTRA_INTENT_PARAMS, this);
        return bundle;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.title);
        dest.writeSerializable(this.fileFilter);
        dest.writeString(this.rootPath);
        dest.writeString(this.currentPath);
        dest.writeInt(directoriesFilter ? 1 : 0);
        dest.writeInt(this.showHidden ? 1 : 0);
        dest.writeInt(this.closeable ? 1 : 0);
    }

    public static final Parcelable.Creator<IntentParameters> CREATOR = new Parcelable.Creator<IntentParameters>() {
        @Override
        public IntentParameters createFromParcel(Parcel source) {
            String title = source.readString();
            Pattern fileFilter = (Pattern) source.readSerializable();
            String rootPath = source.readString();
            String currentPath = source.readString();
            boolean directoriesFilter = source.readInt() != 0;
            boolean showHidden = source.readInt() != 0;
            boolean closeable = source.readInt() != 0;

            return new IntentParameters(
                    title,
                    fileFilter,
                    rootPath,
                    currentPath,
                    directoriesFilter,
                    showHidden,
                    closeable);
        }

        @Override
        public IntentParameters[] newArray(int size) {
            return new IntentParameters[size];
        }
    };
}
