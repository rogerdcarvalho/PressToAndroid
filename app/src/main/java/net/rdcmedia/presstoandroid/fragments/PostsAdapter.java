package net.rdcmedia.presstoandroid.fragments;

import android.content.Intent;
import android.os.Build;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import net.rdcmedia.presstoandroid.R;
import net.rdcmedia.presstoandroid.activities.MainActivity;
import net.rdcmedia.presstoandroid.activities.WebActivity;
import net.rdcmedia.presstoandroid.model.Post;
import net.rdcmedia.presstoandroid.core.GlideApp;

public class PostsAdapter extends RecyclerView.Adapter<PostsAdapter.PostViewHolder> {

    public static class PostViewHolder extends RecyclerView.ViewHolder {

        CardView cardView;
        TextView titleTextView;
        TextView summaryTextView;
        ImageView imageView;

        PostViewHolder(View itemView) {
            super(itemView);
            cardView = (CardView)itemView.findViewById(R.id.post_cardview);
            titleTextView = (TextView)itemView.findViewById(R.id.post_title);
            summaryTextView = (TextView)itemView.findViewById(R.id.post_summary);
            imageView = (ImageView)itemView.findViewById(R.id.post_image);
        }
    }


    int defaultMargin;
    private PostsFragment postsFragment;

    PostsAdapter(PostsFragment postsFragment){
        defaultMargin = 0;
        this.postsFragment = postsFragment;
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    @Override
    public PostViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.card_post, viewGroup, false);
        PostViewHolder pvh = new PostViewHolder(v);
        return pvh;
    }

    @Override
    public void onBindViewHolder(PostViewHolder postViewHolder, int i) {
        if (i == (getItemCount() - 1)) {
            if (postsFragment.getActivity() instanceof MainActivity){
                MainActivity mainActivity = (MainActivity)(postsFragment.getActivity());
                if (mainActivity.hasMorePosts()){
                    postsFragment.scrollPosition = i;
                    postsFragment.mustReload = true;
                    postsFragment.progressBar.setVisibility(View.VISIBLE);
                    mainActivity.loadMorePosts();
                }
            }
        }
        final int postNum = i;
        final Post post = postsFragment.getPosts().getValue().get(i);
        postViewHolder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent webIntent = new Intent( postsFragment.getActivity() ,
                        WebActivity.class);
                webIntent.putExtra("url", post.getLink());
                postsFragment.getActivity().startActivity(webIntent);

            }
        });


        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            postViewHolder.titleTextView.setText(Html.fromHtml(post.getTitle(), Html.FROM_HTML_MODE_COMPACT));
            String htmlText = post.getSummaryText();
            postViewHolder.summaryTextView.setText(Html.fromHtml(htmlText, Html.FROM_HTML_MODE_COMPACT));
        } else {
            postViewHolder.titleTextView.setText(Html.fromHtml(post.getTitle()));
            String htmlText = post.getSummaryText();
            postViewHolder.summaryTextView.setText(Html.fromHtml(htmlText));
        }
        if (post.getFeaturedImage() != null) {
            GlideApp.with(postViewHolder.itemView).
                    load(post.getFeaturedImage()).
                    centerCrop().
                    into(postViewHolder.imageView);
        } else {
            postViewHolder.imageView.setImageDrawable(null);
        }

        if (i == 0) {
            ViewGroup.MarginLayoutParams p = (ViewGroup.MarginLayoutParams) postViewHolder.cardView.getLayoutParams();
            if (defaultMargin == 0) {
                defaultMargin = p.topMargin;
            }
            p.setMargins(p.leftMargin,  defaultMargin * 2 , p.rightMargin, defaultMargin);
            postViewHolder.cardView.requestLayout();
        } else if (i == getItemCount() -1) {
            ViewGroup.MarginLayoutParams p = (ViewGroup.MarginLayoutParams) postViewHolder.cardView.getLayoutParams();
            p.setMargins(p.leftMargin,  defaultMargin, p.rightMargin, defaultMargin * 2);
            postViewHolder.cardView.requestLayout();
        }
    }

    @Override
    public int getItemCount() {
        if (postsFragment.getPosts().getValue() != null) {
            return postsFragment.getPosts().getValue().size();
        }
        return 0;
    }
}