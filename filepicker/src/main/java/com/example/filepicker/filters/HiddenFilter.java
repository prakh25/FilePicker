package com.example.filepicker.filters;

import java.io.File;
import java.io.FileFilter;
import java.io.Serializable;

/**
 * Created by prakh on 07-01-2018.
 */

public class HiddenFilter implements FileFilter, Serializable {

    @Override
    public boolean accept(File f) {
        return !f.isHidden();
    }
}
