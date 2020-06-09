package com.library.mylibrary.interfaces;

import com.amazonaws.services.rekognition.model.TextDetection;

import java.util.List;

public interface TextDetectionInterface {

        void textDetectionCallback(List<TextDetection> textDetections, Boolean checkBooleanResulDetectLabel);
    }

