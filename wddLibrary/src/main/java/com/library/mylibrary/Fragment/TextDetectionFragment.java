package com.library.mylibrary.Fragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.amazonaws.services.rekognition.model.Label;
import com.amazonaws.services.rekognition.model.TextDetection;
import com.library.mylibrary.R;

import java.util.ArrayList;
import java.util.List;

public class TextDetectionFragment extends Fragment {
    List<TextDetection> textDetections;

    public TextDetectionFragment(List<TextDetection> labels) {
        this.textDetections=labels;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_text_detection, container, false);
        final ListView list = view.findViewById(R.id.wdd_text_detected);
        ArrayList<String> arrayListLables = new ArrayList<>();
        if(textDetections!=null)
        for (TextDetection text: textDetections) {

            System.out.println("Text are: " + text.getDetectedText());
            ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_list_item_1, arrayListLables);
            list.setAdapter(arrayAdapter);
            arrayListLables.add(text.getDetectedText());

        }
        return view;
    }
}
