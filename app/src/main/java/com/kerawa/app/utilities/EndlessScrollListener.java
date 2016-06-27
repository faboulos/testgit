package com.kerawa.app.utilities;

import android.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.widget.AbsListView;

import com.kerawa.app.ListAdsActivity;

public abstract class EndlessScrollListener implements AbsListView.OnScrollListener {
    // The minimum amount of items to have below your current scroll position
    // before loading more.
    private int visibleThreshold = 5;
    // The current offset index of data you have loaded
    private int currentPage = 0;
    // The total number of items in the dataset after the last load
    private int previousTotalItemCount = 0;
    // True if we are still waiting for the last set of data to load.
    private boolean loading = true;
    // Sets the starting page index
    private int startingPageIndex = 0;
    int last = 0;
    boolean control = true;


    private int mLastFirstVisibleItem;

    public abstract void onScrollUp();

    public abstract void onScrollDown();

    public EndlessScrollListener() {
    }

    public EndlessScrollListener(int visibleThreshold) {
        this.visibleThreshold = visibleThreshold;
    }

    public EndlessScrollListener(int visibleThreshold, int startPage) {
        this.visibleThreshold = visibleThreshold;
        this.startingPageIndex = startPage;
        this.currentPage = startPage;
    }

    // This happens many times a second during a scroll, so be wary of the code you place here.
    // We are given a few useful parameters to help us work out if we need to load some more data,
    // but first we check if we are waiting for the previous load to finish.
    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount)
    {
        // If the total item count is zero and the previous isn't, assume the
        // list is invalidated and should be reset back to initial state

        if (totalItemCount < previousTotalItemCount) {
            this.currentPage = this.startingPageIndex;
            this.previousTotalItemCount = totalItemCount;
            if (totalItemCount == 0) { this.loading = true; }
        }
       if (loading && (totalItemCount > previousTotalItemCount)) {
            loading = false;
            previousTotalItemCount = totalItemCount;
            currentPage++;
        }

       if (!loading && (totalItemCount - visibleItemCount)<=(firstVisibleItem + visibleThreshold)) {
            onLoadMore(currentPage + 1, totalItemCount);
            loading = true;
        }

        //condition used for hiding buttom bars
        if (firstVisibleItem < last && !control) {
            onScrollUp();
            control = true;
        } else if (firstVisibleItem > last && control) {
            onScrollDown();
            control = false;
        }

        last = firstVisibleItem;


    }

    // Defines the process for actually loading more data based on page
    public abstract void onLoadMore(int page, int totalItemsCount);

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        // Don't take any action on changed
    }
}