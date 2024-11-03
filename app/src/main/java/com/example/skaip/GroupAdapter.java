package com.example.skaip;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class GroupAdapter extends RecyclerView.Adapter<GroupAdapter.GroupViewHolder> {

    private List<Group> groupList;

    public GroupAdapter(List<Group> groupList) {
        this.groupList = groupList;
    }

    @NonNull
    @Override
    public GroupViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.group_container_item, parent, false);
        return new GroupViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull GroupViewHolder holder, int position) {
        Group group = groupList.get(position);
        holder.setGroupData(group);
    }

    @Override
    public int getItemCount() {
        return groupList.size();
    }

    class GroupViewHolder extends RecyclerView.ViewHolder {
        private ImageView groupImage;
        private TextView groupName;

        GroupViewHolder(View itemView) {
            super(itemView);
            groupImage = itemView.findViewById(R.id.group_image);
            groupName = itemView.findViewById(R.id.group_name);
        }

        void setGroupData(Group group) {
            groupName.setText(group.getName());
            if (group.getEncodedImage() != null) {
                Bitmap imageBitmap = getClassImage(group.getEncodedImage());
                groupImage.setImageBitmap(imageBitmap);
            }
        }
    }

    private Bitmap getClassImage(String encodedImage) {
        byte[] bytes = Base64.decode(encodedImage, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
    }
}
