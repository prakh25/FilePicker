package com.example.filepicker;

import java.io.File;

/**
 * Created by prakh on 09-01-2018.
 */

public interface OnFileClickListener {

    void onFileClicked(File file);

    void onFileLongClicked(File file);
}
