package ingage.ingage.helpers;

import android.graphics.Bitmap;

/**
 * Created by Davis on 4/14/2017.
 */

public class ThreadsHelper {
    private String thread_id;
    private String thread_title;
    private String thread_content;
    private String thread_date;
    private String thread_category;
    private String thread_img;
    private String thread_by;
    private long thread_duration  = -2;

    public ThreadsHelper(){

    }

    public ThreadsHelper(String thread_id, String thread_title, String thread_content,
                         String thread_by, String thread_date, String thread_category, String thread_img){
        this.setThread_id(thread_id);
        this.setThread_title(thread_title);
        this.setThread_content(thread_content);
        this.setThread_by(thread_by);
        this.setThread_date(thread_date);
        this.setThread_category(thread_category);
        this.setThread_img(thread_img);
    }//archived

    public ThreadsHelper(String thread_id, String thread_title, String thread_content,
                         String thread_by, String thread_date, String thread_category, String thread_img, long time_remaining){
        this.setThread_id(thread_id);
        this.setThread_title(thread_title);
        this.setThread_content(thread_content);
        this.setThread_by(thread_by);
        this.setThread_date(thread_date);
        this.setThread_category(thread_category);
        this.setThread_img(thread_img);
        this.setThread_duration(time_remaining);
    }//active

    public String getThread_id() {
        return thread_id;
    }

    public String getThread_title(){
        return thread_title;
    }

    public String getThread_content(){
        return thread_content;
    }

    public String getThread_date(){ return thread_date; }

    public String getThread_category(){return thread_category; }

    public String getThread_img() {
        return thread_img;
    }

    public long getThread_duration() { return thread_duration; }

    public String getThread_by() { return thread_by; }

    public void setThread_by(String thread_by) { this.thread_by = thread_by; }

    public void setThread_duration(long thread_duration) { this.thread_duration = thread_duration; }

    public void setThread_title(String thread_title){
        this.thread_title = thread_title;
    }

    public void setThread_content(String thread_content){
        this.thread_content = thread_content;
    }

    public void setThread_date(String thread_date){
        this.thread_date = thread_date;
    }

    public void setThread_category(String thread_category){this.thread_category = thread_category; }

    public void setThread_id(String thread_id) {
        this.thread_id = thread_id;
    }

    public void setThread_img(String thread_img) {
        this.thread_img = thread_img;
    }

}
