package com.elrain.bashim.fragment.helper;

import android.content.Context;
import android.test.InstrumentationTestCase;
import android.test.suitebuilder.annotation.SmallTest;

import com.elrain.bashim.util.BashPreferences;

/**
 * Created by denys.husher on 29.12.2015.
 */
public class SearchHelperTest extends InstrumentationTestCase {

    private SearchHelper mHelper;
    private Context mContext;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        mContext = getInstrumentation().getContext();
        mHelper = new SearchHelper(mContext);
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
        mHelper = null;
    }

    @SmallTest
    public void testOnSubmit(){
        String textFilter = "test1";
        mHelper.onQueryTextSubmit(textFilter);
        assertEquals(textFilter, BashPreferences.getInstance(mContext).getSearchFilter());

        assertEquals(false, mHelper.onQueryTextSubmit(""));
        assertEquals(false, mHelper.onQueryTextSubmit(null));
    }

    @SmallTest
    public void testOnSubmitWithListener(){
        final String textFilter = "test2";
        BashPreferences.OnFilterChanged listener = new BashPreferences.OnFilterChanged() {
            @Override
            public void onFilterChange() {
                assertEquals(textFilter, BashPreferences.getInstance(mContext).getSearchFilter());
            }
        };
        BashPreferences.getInstance(mContext).setFilterListener(listener);
        mHelper.onQueryTextSubmit(textFilter);
    }

    @SmallTest
    public void testOnChange(){
        assertEquals(false, mHelper.onQueryTextChange("test3"));
        assertEquals(true, mHelper.onQueryTextChange(""));
        assertEquals(true, mHelper.onQueryTextChange(null));
        assertEquals(null, BashPreferences.getInstance(mContext).getSearchFilter());
    }
}
