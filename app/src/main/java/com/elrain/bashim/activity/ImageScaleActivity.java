package com.elrain.bashim.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.Toast;

import com.elrain.bashim.BashApp;
import com.elrain.bashim.R;
import com.elrain.bashim.dal.QuotesTableHelper;
import com.elrain.bashim.object.ImageSimpleItem;
import com.elrain.bashim.util.Constants;
import com.elrain.bashim.util.TouchImageView;
import com.squareup.picasso.Picasso;
import com.squareup.sqlbrite.BriteDatabase;

import javax.inject.Inject;

public class ImageScaleActivity extends AppCompatActivity {

    @Inject
    BriteDatabase mDb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((BashApp) getApplication()).getComponent().inject(this);
        if (null != getSupportActionBar()) {
            getSupportActionBar().setTitle(getString(R.string.action_favorite));
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        long id = getIntent().getLongExtra(Constants.KEY_INTENT_IMAGE_ID, -1);
        if (id == -1) errorAction();

        ImageSimpleItem isi = QuotesTableHelper.getImage(mDb, id);
        if (isi != null) {
            setContentView(R.layout.scale_image_view);
            TouchImageView iv = (TouchImageView) findViewById(R.id.ivComics);
            Picasso.with(this).load(isi.getLink()).into(iv);
        } else errorAction();
    }

    private void errorAction() {
        Toast.makeText(this, Constants.WRONG_IMAGE_ID, Toast.LENGTH_SHORT).show();
        this.finish();
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