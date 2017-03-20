package com.example.android.popularmovies.model.remote.trailer;

import com.google.gson.annotations.Expose;

/**
 * One result of trailer response.
 */
public class TrailerResult {
    @Expose
    private String key;
    @Expose
    private String name;
    @Expose
    private String site;
    @Expose
    private String size;
    @Expose
    private String type;

    /**
     * Gets key of the trailer to play it.
     * @return Key of the trailer.
     */
    public String getKey() {
        return key;
    }

    /**
     * Gets name of the trailer.
     * @return Name of the trailer.
     */
    public String getName() {
        return name;
    }


    /**
     * Gets site, where the trailer could be found.
     * @return Site
     */
    public String getSite() {
        return site;
    }

    /**
     * Gets size of the trailer.
     * @return Size of the trailer
     */
    public String getSize() {
        return size;
    }

    /**
     * Gets type of the trailer.
     * @return Type of the trailer.
     */
    public String getType() {
        return type;
    }
}
