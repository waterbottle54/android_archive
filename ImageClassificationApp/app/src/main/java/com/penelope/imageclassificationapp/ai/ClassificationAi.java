package com.penelope.imageclassificationapp.ai;

import android.content.Context;
import android.graphics.Bitmap;

import com.penelope.imageclassificationapp.ml.Classifier;

import org.tensorflow.lite.support.image.TensorImage;
import org.tensorflow.lite.support.label.Category;

import java.io.IOException;
import java.util.List;

// 특정 이미지를 읽고 가장 가능성이 높은 식별값을 알려주는 AI

public class ClassificationAi {

    public final Context context;

    public ClassificationAi(Context context) {
        this.context = context;
    }

    public List<Category> getResults(Bitmap src) {

        Bitmap bitmap = src.copy(Bitmap.Config.ARGB_8888, true);

        try {
            Classifier model = Classifier.newInstance(context);

            // Creates inputs for reference.
            TensorImage image = TensorImage.fromBitmap(bitmap);

            // Runs model inference and gets result.
            Classifier.Outputs outputs = model.process(image);
            List<Category> probability = outputs.getProbabilityAsCategoryList();

            // Releases model resources if no longer used.
            model.close();

            return probability;

        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    public String getBestResult(Bitmap bitmap) {

        List<Category> results = getResults(bitmap);
        Category best = null;
        float maxScore = 0;

        // 결과값에서 가장 스코어(가능성)이 높은 레이블을 리턴한다
        for (Category category : results) {
            if (category.getScore() > maxScore) {
                best = category;
                maxScore = category.getScore();
            }
        }

        return best != null ? best.getLabel() : null;
    }

}
