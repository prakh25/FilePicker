package com.example.filepicker;

import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.ActionMode;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.filepicker.filters.CompositeFilter;
import com.example.filepicker.util.FileUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by prakh on 07-01-2018.
 */

public class FilePickerFragment extends Fragment implements FileAdapter.AdapterInteractionListener {

    private static final String ARG_FILE_PATH = "arg_file_path";
    private static final String ARG_FILTER = "arg_filter";

    private TextView emptyView;
    private String path;
    private CompositeFilter filter;
    private CustomRecyclerView recyclerView;

    private FileAdapter adapter;

    private FragmentListener listener;
    private ActionMode actionMode;

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
        listener = (FragmentListener) getActivity();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fp_fragment_file_picker,
                container, false);

        recyclerView = view.findViewById(R.id.fp_recycler_view);
        recyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), null));

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
        if (getArguments() != null) {
            if (getArguments().getString(ARG_FILE_PATH) != null) {
                path = getArguments().getString(ARG_FILE_PATH);
            }
        }
        filter = (CompositeFilter) getArguments().getSerializable(ARG_FILTER);
    }

    private void initFilesList() {
        adapter = new FileAdapter(FileUtils.getFileListByDirPath(path, filter));

        adapter.setListener(this);

        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(adapter);
        recyclerView.setEmptyView(emptyView);
    }

    @Override
    public void onItemClicked(File file, int position) {
        handleFileClicked(file, position);
    }

    @Override
    public void onItemLongClicked(File file, int position) {
        if(!file.isDirectory()) {
            if (actionMode == null) {
                actionMode = ((AppCompatActivity) getActivity()).startSupportActionMode(actionModeCallback);
                toggleSelection(position);
            }
        } else {
            Toast.makeText(getActivity(), ""+file.getName(), Toast.LENGTH_SHORT).show();
        }
    }

    private void handleFileClicked(final File clickedFile, int position) {
        if (clickedFile.isDirectory()) {
            path = clickedFile.getPath();
            // If the user wanna go to the emulated directory, he will be taken to the
            // corresponding user emulated folder.
            if (path.equals("/storage/emulated"))
                path = Environment.getExternalStorageDirectory().getAbsolutePath();
            listener.directoryClicked(path);
        } else {
            if(actionMode != null) {
                toggleSelection(position);
                return;
            }

            List<String> strings = new ArrayList<>();
            strings.add(clickedFile.getPath());

            FilePickerResponse response =
                    new FilePickerResponse.Builder(strings)
                            .build();
            listener.fileClicked(response);
        }
    }

    private void toggleSelection(int pos) {
        adapter.toggleSelection(pos);
        String title = getString(R.string.fp_footer_select, adapter.getSelectedItemCount());
        actionMode.setTitle(title);
    }

    public interface FragmentListener {
        void directoryClicked(String path);

        void fileClicked(FilePickerResponse response);
    }

    private ActionMode.Callback actionModeCallback = new ActionMode.Callback() {
        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            MenuInflater menuInflater = mode.getMenuInflater();
            menuInflater.inflate(R.menu.fp_menu_action_mode, menu);
            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return false;
        }

        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            int id = item.getItemId();
            if (id == R.id.fp_action_select) {
                List<Integer> selectedItemPosition = adapter.getSelectedItems();
                List<String> selectedFilePaths = new ArrayList<>();

                for(int pos : selectedItemPosition) {
                    selectedFilePaths.add(adapter.getSelectedFilePath(pos));
                }

                FilePickerResponse response = new FilePickerResponse.Builder(selectedFilePaths)
                        .build();
                listener.fileClicked(response);

                actionMode.finish();
                return true;
            }
            return false;
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {
            actionMode = null;
            adapter.clearSelection();
        }
    };
}
