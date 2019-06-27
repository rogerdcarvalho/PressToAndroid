package net.rdcmedia.presstoandroid.fragments;

import android.arch.lifecycle.MutableLiveData;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import net.rdcmedia.presstoandroid.R;
import net.rdcmedia.presstoandroid.activities.MainActivity;
import net.rdcmedia.presstoandroid.model.Post;
import java.util.ArrayList;

public class PostsFragment extends Fragment {

    private PostsAdapter postsAdapter;
    private RecyclerView postsView;
    private SwipeRefreshLayout layout;
    private LinearLayoutManager linearLayoutManager;
    public ProgressBar progressBar;
    public int scrollPosition;
    public boolean mustReload;
    public boolean enableScroll;

    public PostsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        postsAdapter =  new PostsAdapter(this);

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_posts, container, false);
        progressBar = (ProgressBar) view.findViewById(R.id.postsFragmentProgressBar);
        progressBar.setVisibility(View.INVISIBLE);
        layout = (SwipeRefreshLayout)view.findViewById(R.id.swiperefresh);
                layout.setOnRefreshListener(
                        new SwipeRefreshLayout.OnRefreshListener() {
                            @Override
                            public void onRefresh() {
                                if (getActivity() instanceof MainActivity){
                                    mustReload = true;
                                    enableScroll = false;
                                    ((MainActivity) getActivity()).reload();
                                } else {
                                    layout.setRefreshing(false);
                                }
                            }
                        }
                );

        enableScroll = true;
        postsView = (RecyclerView) view.findViewById(R.id.posts_recycler_view);
        linearLayoutManager = new LinearLayoutManager(getContext()) {
            @Override
            public boolean canScrollVertically() {
                return enableScroll;
            }
        };
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        postsView.setLayoutManager(linearLayoutManager);
        postsView.setHasFixedSize(true);
        postsView.setAdapter(postsAdapter);
        return view;

    }

    public void onReload(){
        if (mustReload){
            layout.setRefreshing(false);
            progressBar.setVisibility(View.INVISIBLE);
            postsAdapter =  new PostsAdapter(this);
            postsView.setAdapter(postsAdapter);
            postsView.post(new Runnable() {
                @Override
                public void run() {
                    linearLayoutManager.scrollToPositionWithOffset(scrollPosition, 0);
                }
            });
            mustReload = false;
            enableScroll = true;
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (getActivity() instanceof MainActivity){
            int bottomMargin = ((MainActivity)getActivity()).getBottomNavigationMargin();
            View refreshLayout = (View)postsView.getParent();
            ViewGroup.MarginLayoutParams p = (ViewGroup.MarginLayoutParams) refreshLayout.getLayoutParams();
            p.setMargins(0,  0 , 0, bottomMargin);
            refreshLayout.requestLayout();
            postsView.requestLayout();
        }

    }

    public MutableLiveData<ArrayList<Post>> getPosts(){
        if (getActivity() instanceof MainActivity) {
            MainActivity activity = (MainActivity) getActivity();
            if (activity != null){
                return activity.getPosts();
            }
        }
        return null;
    }

}