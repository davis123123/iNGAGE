package ingage.ingage.util;

import android.os.Handler;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

public class CustomRunnable implements Runnable {

  public long millisUntilFinished = 40000;
  public TextView holder;
  Handler handler;
  //ImageView imageView;

  public CustomRunnable(Handler handler, TextView holder, long millisUntilFinished) {
    this.handler = handler;
    this.holder = holder;
    this.millisUntilFinished = millisUntilFinished;
    //this.imageView = imageView;
  }

  @Override
  public void run() {
      /* do what you need to do */
    long seconds = millisUntilFinished / 1000;
    long minutes = seconds / 60;
    long hours = minutes / 60;
    String time = hours % 24 + ":" + minutes % 60 + ":" + seconds % 60;
    holder.setText(time);
    millisUntilFinished -= 1000;

    Log.d("DEV123"," " + seconds);
    //imageView.setX(imageView.getX()+seconds);
      /* and here comes the "trick" */
    if(seconds <= 0) {
      Log.d("SECONDS", "HERE" + seconds);
      handler.removeCallbacks(this);
    }
    else
      handler.postDelayed(this, 1000);
  }

}