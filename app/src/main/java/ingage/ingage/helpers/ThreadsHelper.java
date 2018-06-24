package ingage.ingage.helpers;

import android.graphics.Bitmap;

/**
 * Created by Davis on 4/14/2017.
 */

public class ThreadsHelper {
    private String thread_id, thread_title, thread_content, thread_date, thread_category, thread_img, thread_duration;
    private int[] time_remaining;

    public ThreadsHelper(String thread_id, String thread_title, String thread_content,
                         String thread_duration, String thread_date, String thread_category, String thread_img){
        this.setThread_id(thread_id);
        this.setThread_title(thread_title);
        this.setThread_content(thread_content);
        this.setThread_by(thread_duration);
        this.setThread_date(thread_date);
        this.setThread_category(thread_category);
        this.setThread_img(thread_img);
    }

    public ThreadsHelper(String thread_id, String thread_title, String thread_content,
                         String thread_by, String thread_date, String thread_category, String thread_img, int[] time_remaining){
        this.setThread_id(thread_id);
        this.setThread_title(thread_title);
        this.setThread_content(thread_content);
        this.setThread_by(thread_by);
        this.setThread_date(thread_date);
        this.setThread_category(thread_category);
        this.setThread_img(thread_img);
        this.setTime_remaining(time_remaining);
    }

    public String getThread_id() {
        return thread_id;
    }

    public String getThread_title(){
        return thread_title;
    }

    public String getThread_content(){
        return thread_content;
    }

    public String getThread_duration(){
        return thread_duration;
    }

    public String getThread_date(){
        return thread_date;
    }

    public String getThread_category(){return thread_category; }

    public String getThread_img() {
        return thread_img;
    }

    public int[] getTime_remaining() { return time_remaining; }

    public void setThread_title(String thread_title){
        this.thread_title = thread_title;
    }

    public void setThread_content(String thread_content){
        this.thread_content = thread_content;
    }

    public void setThread_by(String thread_duration){
        this.thread_duration = thread_duration;
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

    public void setTime_remaining(int[] time_remaining) {
        this.time_remaining = time_remaining;
    }
}
