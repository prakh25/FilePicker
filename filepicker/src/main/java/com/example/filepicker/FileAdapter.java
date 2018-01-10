package com.example.filepicker;

import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.util.SparseBooleanArray;
import android.util.TypedValue;
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
    private SparseBooleanArray selectedItems;

    private AdapterInteractionListener listener;

    public FileAdapter(List<File> files) {
        fileList = new ArrayList<>();
        fileList.addAll(files);
        selectedItems = new SparseBooleanArray();
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

        if(selectedItems.get(position)) {
            holder.itemView.setBackgroundColor(ContextCompat.getColor(holder.itemView.getContext(),
                    R.color.fp_selected_item_bg));
        } else {
            TypedValue outValue = new TypedValue();
            holder.itemView.getContext().getTheme()
                    .resolveAttribute(android.R.attr.selectableItemBackground, outValue, true);
            holder.itemView.setBackgroundResource(outValue.resourceId);
        }
    }

    @Override
    public int getItemCount() {
        return fileList.size();
    }

    private File getModel(int index) {
        return fileList.get(index);
    }

    public void toggleSelection(int pos) {
        if(selectedItems.get(pos, false)) {
            selectedItems.delete(pos);
        } else {
            selectedItems.put(pos, true);
        }
        notifyItemChanged(pos);
    }

    public void clearSelection() {
        selectedItems.clear();
        notifyDataSetChanged();
    }

    public int getSelectedItemCount() {
        return selectedItems.size();
    }

    public String getSelectedFilePath(int pos) {
        return fileList.get(pos).getPath();
    }

    public List<Integer> getSelectedItems() {
        List<Integer> items = new ArrayList<>(selectedItems.size());
        for(int i=0; i<selectedItems.size(); i++) {
            items.add(selectedItems.keyAt(i));
        }
        return items;
    }

    public void setListener(AdapterInteractionListener listener) {
        this.listener = listener;
    }

    public class FileViewHolder extends RecyclerView.ViewHolder {

        private ImageView fileImage;
        private TextView fileTitle;
        private TextView fileSubtitle;

        public FileViewHolder(final View itemView, final AdapterInteractionListener listener) {
            super(itemView);

            fileImage = itemView.findViewById(R.id.fp_item_image);
            fileTitle = itemView.findViewById(R.id.fp_item_title);
            fileSubtitle = itemView.findViewById(R.id.fp_item_subtitle);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    listener.onItemClicked(getModel(getAdapterPosition()),
                            getAdapterPosition());
                }
            });

            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    listener.onItemLongClicked(getModel(getAdapterPosition()),
                            getAdapterPosition());
                    return true;
                }
            });
        }
    }

    public interface AdapterInteractionListener {
        void onItemClicked(File file, int position);

        void onItemLongClicked(File file, int position);
    }
}
