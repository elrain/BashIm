package com.elrain.bashim.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.elrain.bashim.R;
import com.elrain.bashim.dal.QuotesTableHelper;
import com.elrain.bashim.object.ImageSimpleItem;
import com.elrain.bashim.util.Constants;
import com.elrain.bashim.util.TouchImageView;

import java.util.ArrayList;

/**
 * Created by denys.husher on 13.11.2015.
 */
public class ImagePagerActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scale_image_view);
        long id = getIntent().getLongExtra(Constants.KEY_INTENT_IMAGE_ID, 1);
        ArrayList<ImageSimpleItem> images = QuotesTableHelper.getImages(this);

        if (null != getSupportActionBar()) {
            getSupportActionBar().setTitle(getString(R.string.action_comics));
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        int position = id == 1 ? 1 : getPosition(id, images);

        SectionsPagerAdapter sectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager(), images);
        ViewPager viewPager = (ViewPager) findViewById(R.id.container);
        viewPager.setAdapter(sectionsPagerAdapter);
        viewPager.setCurrentItem(position);
    }

    private int getPosition(long id, ArrayList<ImageSimpleItem> images) {
        int size = images.size();
        for (int index = 0; index < size; ++index) {
            if (id == images.get(index).getId()) return index;
        }
        return 1;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                ImagePagerActivity.this.finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public static class PlaceholderFragment extends Fragment {
        public PlaceholderFragment() {
        }

        public static PlaceholderFragment newInstance(long id, String url) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putLong(QuotesTableHelper.ID, id);
            args.putString(QuotesTableHelper.LINK, url);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.scale_image_view, container, false);
            TouchImageView iv = (TouchImageView) rootView.findViewById(R.id.ivComics);
            Glide.with(getActivity()).load(getArguments().getString(QuotesTableHelper.LINK)).into(iv);
            return rootView;
        }
    }

    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        private final ArrayList<ImageSimpleItem> images;

        public SectionsPagerAdapter(FragmentManager fm, ArrayList<ImageSimpleItem> images) {
            super(fm);
            this.images = images;
        }

        @Override
        public Fragment getItem(int position) {
            return PlaceholderFragment.newInstance(images.get(position).getId(),
                    images.get(position).getLink());
        }

        @Override
        public int getCount() {
            return images.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return images.get(position).getTitle();
        }
    }
}
