package com.example.filepicker;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.filepicker.util.FileTypeUtils;
import com.example.filepicker.util.FileUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by prakh on 07-01-2018.
 */

public class FileAdapter extends RecyclerView.Adapter<FileAdapter.FileViewHolder> {

    private List<File> fileList;
    private FileInteractionListener listener;

    public FileAdapter(List<File> files) {
        fileList = new ArrayList<>();
        fileList.addAll(files);
    }

    @Override
    public FileViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new FileViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fp_file_list_item, parent, false), listener);
    }

    @Override
    public void onBindViewHolder(FileViewHolder holder, int position) {
        File file = fileList.get(position);

        FileTypeUtils.FileType fileType = FileTypeUtils.getFileType(file);
        holder.fileImage.setImageResource(fileType.getIcon());
        holder.fileTitle.setText(file.getName());

        holder.fileSubtitle.setText(holder.itemView.getContext()
                .getString(R.string.fp_file_last_modified,
                        FileUtils.getDate(file.lastModified()),
                        FileUtils.getTime(file.lastModified())));
    }

    @Override
    public int getItemCount() {
        return fileList.size();
    }

    public File getModel(int index) {
        return fileList.get(index);
    }

    public void setListener(FileInteractionListener listener) {
        this.listener = listener;
    }

    public class FileViewHolder extends RecyclerView.ViewHolder {

        private ImageView fileImage;
        private TextView fileTitle;
        private TextView fileSubtitle;

        public FileViewHolder(View itemView, final FileInteractionListener listener) {
            super(itemView);

            fileImage = itemView.findViewById(R.id.fp_item_image);
            fileTitle = itemView.findViewById(R.id.fp_item_title);
            fileSubtitle = itemView.findViewById(R.id.fp_item_subtitle);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    listener.onItemClicked(view, getAdapterPosition());
                }
            });
        }
    }

    public interface FileInteractionListener {
        void onItemClicked(View view, int position);
    }
}
