package ingage.ingage.util;

import com.google.gson.annotations.SerializedName;

/**
 * Created by wuv66 on 2/11/2018.
 */

public class RecentComment {

    @SerializedName("username")
    public String username;

    @SerializedName("thread_id")
    public String thread_id;

    @SerializedName("recent_comment")
    public String recent_comment;

    @SerializedName("thread_title")
    public String thread_title;

    @SerializedName("thread_content")
    public String thread_content;

    @SerializedName("thread_by")
    public String thread_by;

    @SerializedName("thread_date")
    public String thread_date;

    @SerializedName("thread_category")
    public String thread_category;

    @SerializedName("thread_image_link")
    public String thread_image_link;

    @SerializedName("archived")
    public String archived;

    @SerializedName("side")
    public String side;


}
