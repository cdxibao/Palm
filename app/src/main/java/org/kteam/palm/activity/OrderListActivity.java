package org.kteam.palm.activity;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.RadioGroup;

import org.apache.log4j.Logger;
import org.kteam.palm.BaseActivity;
import org.kteam.palm.R;
import org.kteam.palm.common.view.NoScrollViewPager;
import org.kteam.palm.fragment.OtherPayOrderFragment;
import org.kteam.palm.fragment.OwnPayOrderFragment;
import org.kteam.palm.fragment.PensionGrantInfoFragment;

import java.util.ArrayList;
import java.util.List;

/**
 * @Package org.kteam.palm.activity
 * @Project Palm
 * @Description
 * @Author Micky Liu
 * @Email mickyliu@126.com
 * @Team KTEAM
 * @Date 2016-03-27 16:10
 */
public class OrderListActivity extends BaseActivity implements RadioGroup.OnCheckedChangeListener{
    private final Logger mLogger = Logger.getLogger(getClass());

    private NoScrollViewPager mViewPager;
    private List<Fragment> mFragmentList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_list);
        if (mUser == null) {
            finish();
            return;
        }
        initToolBar();
        setTitleText(getString(R.string.order));

        initView();
    }

    private void initView() {
        RadioGroup rgTab = findView(R.id.rg_tab);
        rgTab.setOnCheckedChangeListener(this);
        mViewPager =  findView(R.id.view_pager);
        mFragmentList = new ArrayList<Fragment>(2);
        mFragmentList.add(new OwnPayOrderFragment());
        mFragmentList.add(new OtherPayOrderFragment());
        mViewPager.setAdapter(new PensionViewPagerAdapter(getSupportFragmentManager()));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.order_list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_filter) {
            if (mFragmentList.get(mViewPager.getCurrentItem())  instanceof OwnPayOrderFragment) {
                OwnPayOrderFragment fragment = (OwnPayOrderFragment)mFragmentList.get(mViewPager.getCurrentItem());
                fragment.showSelector();
            } else if (mFragmentList.get(mViewPager.getCurrentItem())  instanceof OtherPayOrderFragment) {
                OtherPayOrderFragment fragment = (OtherPayOrderFragment)mFragmentList.get(mViewPager.getCurrentItem());
                fragment.showSelector();
            }
        }
        return true;
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        switch (group.getCheckedRadioButtonId()) {
            case R.id.rb_own_pay:
                mViewPager.setCurrentItem(0);
                break;
            case R.id.rb_other_pay:
                mViewPager.setCurrentItem(1);
                break;
            default:
                break;
        }
    }

    public class PensionViewPagerAdapter extends FragmentPagerAdapter {
        public PensionViewPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int arg0) {
            return mFragmentList.get(arg0);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }
    }

}
