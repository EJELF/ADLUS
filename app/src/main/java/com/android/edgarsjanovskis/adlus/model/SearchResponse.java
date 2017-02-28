package com.android.edgarsjanovskis.adlus.model;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class SearchResponse {

    public List results;

    @SerializedName("max_id")
    public long maxId;

    @SerializedName("since_id")
    public int sinceId;

    @SerializedName("refresh_url")
    public String refreshUrl;

    @SerializedName("next_page")
    public String nextPage;

    @SerializedName("results_per_page")
    public int resultsPerPage;

    public int page;

    @SerializedName("completed_in")
    public double completedIn;

    @SerializedName("since_id_str")
    public String sinceIdStr;

    @SerializedName("max_id_str")
    public String maxIdStr;

    public String query;

}
