package io.caster.rxexamples.examples;


import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.gson.Gson;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.Response;

import java.io.IOException;
import java.util.Map;

import io.caster.rxexamples.R;
import io.caster.rxexamples.models.Gist;
import io.caster.rxexamples.models.GistFile;

/**
 * A simple {@link Fragment} subclass.
 */
public class AsyncTaskFragment extends Fragment {


    public AsyncTaskFragment() {
        // Required empty public constructor
    }

    public static Fragment newInstance() {
        return new AsyncTaskFragment();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Just some view
        return inflater.inflate(R.layout.fragment_async_task, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        new AsyncTask<Void, Void, Gist>() {

            public IOException error;

            @Override
            protected Gist doInBackground(Void... params) {
                OkHttpClient client = new OkHttpClient();

                Request request = new Request.Builder()
                        .url("https://api.github.com/gists/db72a05cc03ef523ee74")
                        .build();
                try {
                    Response response = client.newCall(request).execute();

                    if (response.isSuccessful()) {
                        Gist gist = new Gson().fromJson(response.body().charStream(), Gist.class);
                        return gist;
                    }
                    return null;
                }catch (IOException e)
                {
                    this.error = e;
                    return null;
                }
            }

            @Override
            protected void onPostExecute(Gist gist) {
                super.onPostExecute(gist);

                StringBuilder sb = new StringBuilder();
                // Output
                for (Map.Entry<String, GistFile> entry : gist.files.entrySet()) {
                    sb.append(entry.getKey());
                    sb.append(" - ");
                    sb.append("Length of file ");
                    sb.append(entry.getValue().content.length());
                    sb.append("\n");
                }

                TextView text = (TextView) getView().findViewById(R.id.main_message);
                text.setText(sb.toString());
            }
        }.execute();
    }
}
