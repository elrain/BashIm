package com.elrain.bashim.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import com.bumptech.glide.Glide;
import com.elrain.bashim.R;
import com.elrain.bashim.util.Constants;
import com.elrain.bashim.util.TouchImageView;

/**
 * Created by denys.husher on 10.12.2015.
 */
public class ImageScaleActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (null != getSupportActionBar()) {
            getSupportActionBar().setTitle(getString(R.string.action_favorite));
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        if (null == getIntent().getStringExtra(Constants.KEY_INTENT_IMAGE_URL)
                || "".equals(getIntent().getStringExtra(Constants.KEY_INTENT_IMAGE_URL)))
            finish();
        setContentView(R.layout.scale_image_view);
        TouchImageView iv = (TouchImageView) findViewById(R.id.ivComics);
        Glide.with(this).load(getIntent().getStringExtra(Constants.KEY_INTENT_IMAGE_URL)).into(iv);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                ImageScaleActivity.this.finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}