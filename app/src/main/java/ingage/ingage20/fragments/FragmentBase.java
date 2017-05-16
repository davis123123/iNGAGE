package ingage.ingage20.Fragments;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.View;

/**
 * Created by Davis on 4/5/2017.
 */

public class FragmentBase extends Fragment{
    @Override
    public void onViewCreated(final View view, final Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(final Void... params) {
                return null;
            }

            @Override
            protected void onPostExecute(final Void aVoid) {
                final View view = getView();
            }
        }.execute();
    }

    @Override
    public void onPause() {
        super.onPause();

        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(final Void... params) {
                return null;
            }
        }.execute();
    }
}

