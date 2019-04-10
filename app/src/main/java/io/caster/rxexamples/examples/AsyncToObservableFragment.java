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
import java.util.concurrent.Callable;

import io.caster.rxexamples.R;
import io.caster.rxexamples.models.Gist;
import io.caster.rxexamples.models.GistFile;

import rx.Observable;
import rx.Scheduler;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func0;
import rx.schedulers.Schedulers;

/**
 * A simple {@link Fragment} subclass.
 */
public class AsyncToObservableFragment extends Fragment {
    private Subscription subscription;

    public AsyncToObservableFragment() {
        // Required empty public constructor
    }

    public static Fragment newInstance() {
        return new AsyncToObservableFragment();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Just some view
        return inflater.inflate(R.layout.fragment_async_to_observable, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        subscription = getGistObservable().subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Subscriber<Gist>() {
            @Override
            public void onCompleted() {

            }

            @Override
            public void onError(Throwable e) {

            }

            @Override
            public void onNext(Gist gist) {
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
        });

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(subscription!=null && subscription.isUnsubscribed())
            subscription.unsubscribe();
    }

    private Gist getGist() throws IOException{
        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url("https://api.github.com/gists/db72a05cc03ef523ee74")
                .build();
        Response response = client.newCall(request).execute();

        if (response.isSuccessful()) {
            Gist gist = new Gson().fromJson(response.body().charStream(), Gist.class);
            return gist;
        }
        return null;
    }

//    private Observable<Gist> getGistObservable()
//    {
//        try {
//            return Observable.just(getGist());
//        } catch (IOException e) {
//            return null;
//        }
//    }

    private Observable<Gist> getGistObservable()
    {
            Observable.defer(new Func0<Observable<Gist>>() {
                @Override
                public Observable<Gist> call() {
                    try {
                        return Observable.just(getGist());
                    } catch (IOException e) {
                       return null;
                    }
                }
            });
    }



}
