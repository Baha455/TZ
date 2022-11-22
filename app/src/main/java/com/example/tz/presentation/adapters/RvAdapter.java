package com.example.tz.presentation.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.tz.R;
import com.example.tz.databinding.AppItemBinding;
import com.example.tz.domain.ApksModel;
import com.example.tz.domain.Status;
import com.squareup.picasso.Picasso;
import java.util.ArrayList;
import java.util.List;

public class RvAdapter extends RecyclerView.Adapter<RvAdapter.ImageViewHolder>{

    private ArrayList<ApksModel> list = new ArrayList<ApksModel>();
    private ItemClick click;

    public RvAdapter(ItemClick click) {
        this.click  = click;
    }


    @NonNull
    @Override
    public ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(
                R.layout.app_item, parent, false);
        return new ImageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ImageViewHolder holder, int position) {
        holder.bind(list.get(position));
        holder.binding.tvName.setText(list.get(position).getTitle());
        Picasso.get().load(list.get(position).getLogo50Link()).into(holder.binding.logo);
        holder.binding.getRoot().setOnClickListener(view -> {
            click.onItemCLick(list.get(position));
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }


    public void setList(List<ApksModel> apks){
        list.clear();
        list.addAll(apks);
        notifyDataSetChanged();
    }

    public class ImageViewHolder extends RecyclerView.ViewHolder {

        AppItemBinding binding;
        public ImageViewHolder(@NonNull View itemView) {
            super(itemView);
            binding = AppItemBinding.bind(itemView);
        }

        void bind(ApksModel apk){
            //binding.tvName.setText(apk.getTitle());
            switch (apk.getStatus()){
                case canInstalled: binding.tvStatus.setText("Можно установить");
                break;
                case installed: binding.tvStatus.setText("Установлено");
                break;
                case downloaded: binding.tvStatus.setText("Скачано");
                break;
                case haveUpdated: binding.tvStatus.setText("Есть обновление");
                break;
            }
            //Picasso.get().load(apk.getLogo50Link()).into(binding.logo);

        }
    }

    public interface ItemClick{
        public void onItemCLick(ApksModel apk);
    }
}



