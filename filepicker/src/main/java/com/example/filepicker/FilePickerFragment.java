package com.example.filepicker;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.filepicker.filters.CompositeFilter;
import com.example.filepicker.util.FileUtils;

import java.io.File;

/**
 * Created by prakh on 07-01-2018.
 */

public class FilePickerFragment extends Fragment {

    private static final String ARG_FILE_PATH = "arg_file_path";
    private static final String ARG_FILTER = "arg_filter";

    interface FileClickListener {
        void onFileClicked(File clickedFile);
    }

    private TextView emptyView;
    private String path;
    private CompositeFilter filter;
    private CustomRecyclerView recyclerView;
    private FileClickListener listener;

    private FileAdapter adapter;

    public static FilePickerFragment getInstance(String path,
                                                 CompositeFilter filter) {
        FilePickerFragment instance = new FilePickerFragment();

        Bundle args = new Bundle();
        args.putString(ARG_FILE_PATH, path);
        args.putSerializable(ARG_FILTER, filter);
        instance.setArguments(args);

        return instance;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        listener = (FileClickListener) getActivity();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fp_fragment_file_picker,
                container, false);

        recyclerView = view.findViewById(R.id.fp_recycler_view);
        emptyView = view.findViewById(R.id.fp_empty_text_view);

        emptyView.setCompoundDrawablesWithIntrinsicBounds(null,
                getResources().getDrawable(R.drawable.fp_ic_empty_dir),
                null, null);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initArgs();
        initFilesList();
    }

    @SuppressWarnings("unchecked")
    private void initArgs() {
        if(getArguments() != null) {
            if (getArguments().getString(ARG_FILE_PATH) != null) {
                path = getArguments().getString(ARG_FILE_PATH);
            }
        }
        filter = (CompositeFilter) getArguments().getSerializable(ARG_FILTER);
    }

    private void initFilesList() {
        adapter = new FileAdapter(FileUtils.getFileListByDirPath(path, filter));

       adapter.setListener(new FileAdapter.FileInteractionListener() {
           @Override
           public void onItemClicked(View view, int position) {
               if (listener != null) {
                   listener.onFileClicked(adapter.getModel(position));
               }
           }
       });

        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(adapter);
        recyclerView.setEmptyView(emptyView);
    }
}
