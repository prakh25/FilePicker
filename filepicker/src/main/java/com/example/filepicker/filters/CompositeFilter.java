package com.example.filepicker.filters;

import java.io.File;
import java.io.FileFilter;
import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by prakh on 07-01-2018.
 */

public class CompositeFilter implements FileFilter, Serializable {

    private ArrayList<FileFilter> fileFilters;

    public CompositeFilter(ArrayList<FileFilter> fileFilters) {
        this.fileFilters = fileFilters;
    }

    @Override
    public boolean accept(File f) {
        for (FileFilter filter : fileFilters) {
            if (!filter.accept(f)) {
                return false;
            }
        }
        return true;
    }
}
