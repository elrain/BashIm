package com.elrain.bashim.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.MenuItem;

import com.elrain.bashim.R;
import com.elrain.bashim.util.Constants;
import com.elrain.bashim.util.TouchImageView;
import com.squareup.picasso.Picasso;

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

        if (TextUtils.isEmpty(getIntent().getStringExtra(Constants.KEY_INTENT_IMAGE_URL)))
            finish();
        setContentView(R.layout.scale_image_view);
        TouchImageView iv = (TouchImageView) findViewById(R.id.ivComics);
        Picasso.with(this).load(getIntent().getStringExtra(Constants.KEY_INTENT_IMAGE_URL)).into(iv);
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