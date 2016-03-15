package com.elrain.bashim.activity;

import android.app.Activity;
import android.os.Bundle;

import com.elrain.bashim.R;
import com.elrain.bashim.util.Constants;
import com.elrain.bashim.util.TouchImageView;
import com.squareup.picasso.Picasso;

/**
 * Created by denys.husher on 13.11.2015.
 */
public class ImageScaleActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (null == getIntent().getStringExtra(Constants.KEY_INTENT_IMAGE_URL)
                || "".equals(getIntent().getStringExtra(Constants.KEY_INTENT_IMAGE_URL)))
            finish();
        setContentView(R.layout.scale_image_view);
        TouchImageView iv = (TouchImageView) findViewById(R.id.ivComics);
        Picasso.with(this).load(getIntent().getStringExtra(Constants.KEY_INTENT_IMAGE_URL)).into(iv);
    }
}
