package ingage.ingage20.helpers;

/**
 * Created by Davis on 4/14/2017.
 */

public class ThreadsHelper {
    private String thread_id, thread_title, thread_content, thread_by, thread_date, thread_category, thread_img;
    public ThreadsHelper(String thread_id, String thread_title, String thread_content,
                         String thread_by, String thread_date, String thread_category, String thread_img){
        this.setThread_id(thread_id);
        this.setThread_title(thread_title);
        this.setThread_content(thread_content);
        this.setThread_by(thread_by);
        this.setThread_date(thread_date);
        this.setThread_category(thread_category);
        this.setThread_img(thread_img);
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

    public String getThread_by(){
        return thread_by;
    }

    public String getThread_date(){
        return thread_date;
    }

    public String getThread_category(){return thread_category; }

    public String getThread_img() {
        return thread_img;
    }


    public void setThread_title(String thread_title){
        this.thread_title = thread_title;
    }

    public void setThread_content(String thread_content){
        this.thread_content = thread_content;
    }

    public void setThread_by(String thread_by){
        this.thread_by = thread_by;
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
