package com.wojder.notifapp;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.support.design.widget.FloatingActionButton;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * A simple {@link Fragment} subclass.
 */
public class ContentFragmentWithFab extends Fragment implements View.OnClickListener {

    private FloatingActionButton fab = null;

    public ContentFragmentWithFab() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState){
    View result = inflater.inflate(R.layout.content_fragment_with_fab, container, false);

        fab = (FloatingActionButton) result.findViewById(R.id.fab_button);
        fab.setOnClickListener(this);

    return (result);
    }

    @Override
    public void onClick(View view) {
        Intent i=new Intent(getActivity(), Downloader.class);

        i.setDataAndType(Uri.parse("http://commonsware.com/Android/excerpt.pdf"),
                "application/pdf");

        getActivity().startService(i);
        getActivity().finish();
    }
}