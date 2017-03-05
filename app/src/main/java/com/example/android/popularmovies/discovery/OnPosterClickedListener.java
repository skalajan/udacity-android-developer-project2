package com.example.android.popularmovies.discovery;


interface OnPosterClickedListener {
    /**
     * Method reacting to the click on the item.
     * @param position Index of the clicked item.
     */
    void onPosterClicked(int position);
}
