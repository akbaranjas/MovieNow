package akbaranjas.movieapp.app.adapter;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import akbaranjas.movieapp.R;
import akbaranjas.movieapp.app.pojo.Result;
import akbaranjas.movieapp.app.pojo.video.ResultVideo;

/**
 * Created by akbaranjas on 29/11/16.
 */

public class VideosAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    public static final int TYPE_HEADER = 0;
    public static final int TYPE_ITEM = 1;
    private List<ResultVideo> videos;
    private Context context;
    private boolean mWithHeader;
    RecyclerView.ViewHolder mholder;

    public VideosAdapter(Context context ,List<ResultVideo> videos, boolean withHeader) {
        this.videos = videos;
        this.context = context;
        this.mWithHeader = withHeader;
    }

    public void updateList(List<ResultVideo> videos){
        this.videos = videos;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == TYPE_HEADER) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.header_item_videos, parent, false);
            mholder = new HeaderViewHolder(v);
            return mholder;
        } else if (viewType == TYPE_ITEM) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_videos, parent, false);
            mholder = new ItemViewHolder(v);
            return mholder;
        }
        throw new RuntimeException("there is no type that matches the type " + viewType + " + make sure your using types correctly");
    }

    @Override
    public int getItemViewType(int position) {
        if (isPositionHeader(position))
            return TYPE_HEADER;
        return TYPE_ITEM;
    }

    private boolean isPositionHeader(int position) {
        return this.mWithHeader && position == 0;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder.getItemViewType() == TYPE_ITEM) {
            ItemViewHolder mholder = (ItemViewHolder) holder;
            if(mWithHeader){mholder.tv_test.setText(videos.get(position-1).getName());}
        }
    }

    @Override
    public int getItemCount() {
        if(videos != null) {
            if(mWithHeader){
                return videos.size() + 1;
            }else{
                return videos.size();
            }
        }else {
            return 0;
        }
    }

    public static class HeaderViewHolder extends RecyclerView.ViewHolder {
        CardView cardView;

        public HeaderViewHolder(View itemView) {
            super(itemView);
            cardView = (CardView) itemView.findViewById(R.id.cardview_header_videos);
        }
    }

    public static class ItemViewHolder extends RecyclerView.ViewHolder {
        TextView tv_test;

        ItemViewHolder(View itemView) {
            super(itemView);
            tv_test = (TextView) itemView.findViewById(R.id.test_videos);
        }
    }
}
