package com.example.filepicker;

import android.app.Activity;
import android.content.Intent;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.RestrictTo;

import java.util.List;

/**
 * Created by prakh on 07-01-2018.
 */

public class FilePickerResponse implements Parcelable {
    private final List<String> filePath;
    private final int errorCode;

    private FilePickerResponse(int errorCode) {
        this(null, errorCode);
    }

    private FilePickerResponse(List<String> filePath, int errorCode) {
        this.filePath = filePath;
        this.errorCode = errorCode;
    }

    public static FilePickerResponse fromResulIntent(Intent resultIntent) {
        if (resultIntent != null) {
            return resultIntent.getParcelableExtra(Constants.EXTRA_FILE_PICKER_RESPONSE);
        } else {
            return null;
        }
    }

    @RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
    public static Intent getErrorCodeIntent(int errorCode) {
        return new FilePickerResponse(errorCode).toIntent();
    }

    @RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
    public Intent toIntent() {
        return new Intent().putExtra(Constants.EXTRA_FILE_PICKER_RESPONSE, this);
    }

    public List<String> getFilePath() {
        return filePath;
    }

    public int getErrorCode() {
        return errorCode;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeStringList(this.filePath);
        dest.writeInt(this.errorCode);
    }

    protected FilePickerResponse(Parcel in) {
        this.filePath = in.createStringArrayList();
        this.errorCode = in.readInt();
    }

    public static final Creator<FilePickerResponse> CREATOR = new Creator<FilePickerResponse>() {
        @Override
        public FilePickerResponse createFromParcel(Parcel source) {
            return new FilePickerResponse(source);
        }

        @Override
        public FilePickerResponse[] newArray(int size) {
            return new FilePickerResponse[size];
        }
    };

    @RestrictTo(RestrictTo.Scope.LIBRARY_GROUP)
    public static class Builder {
        private List<String> filePath;

        public Builder(List<String> filePath) {
            this.filePath = filePath;
        }

        public FilePickerResponse build() {
            return new FilePickerResponse(filePath, Activity.RESULT_OK);
        }
    }
}
