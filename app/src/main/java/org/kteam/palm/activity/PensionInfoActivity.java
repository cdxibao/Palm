package org.kteam.palm.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.Menu;
import android.view.View;
import android.widget.RadioGroup;
import android.widget.TextView;

import org.kteam.palm.BaseActivity;
import org.kteam.palm.MainActivity;
import org.kteam.palm.R;
import org.kteam.palm.common.view.NoScrollViewPager;
import org.kteam.palm.fragment.PensionAdjustWagesFragment;
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
 * @Date 2016-01-25 03:25
 */
public class PensionInfoActivity extends BaseActivity implements RadioGroup.OnCheckedChangeListener, View.OnClickListener {

    private NoScrollViewPager mViewPager;
    private Menu mMenu;

    private List<Fragment> mFragmentList;

    private TextView mTvFilter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pension_info);
//        initToolBar();
        if (mUser == null) {
            finish();
            return;
        }

        initView();
    }

    private void initView() {
        TextView tvTitle = findView(R.id.tv_title);
        tvTitle.setText(getString(R.string.pension_info_title, mUser.name));
        findViewById(R.id.iv_back).setOnClickListener(this);
        findViewById(R.id.tv_home).setOnClickListener(this);
        mTvFilter = findView(R.id.tv_filter);
        mTvFilter.setOnClickListener(this);
        RadioGroup rgTab = findView(R.id.rg_tab);
        rgTab.setOnCheckedChangeListener(this);
        mViewPager =  findView(R.id.view_pager);
        mFragmentList = new ArrayList<Fragment>(2);
        mFragmentList.add(new PensionGrantInfoFragment());
        mFragmentList.add(new PensionAdjustWagesFragment());
        mViewPager.setAdapter(new PensionViewPagerAdapter(getSupportFragmentManager()));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_back:
                finish();
                break;
            case R.id.tv_home:
                Intent intent = new Intent(this, MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
                break;
            case R.id.tv_filter:
                if (mFragmentList != null && mFragmentList.size() > 0 && mFragmentList.get(0) instanceof PensionGrantInfoFragment) {
                    ((PensionGrantInfoFragment) mFragmentList.get(0)).showYearSelector();
                }
                break;
        }
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        switch (group.getCheckedRadioButtonId()) {
            case R.id.rb_grant_info:
                mViewPager.setCurrentItem(0);
                mTvFilter.setVisibility(View.VISIBLE);
                break;
            case R.id.rb_adjust_wages:
                mViewPager.setCurrentItem(1);
                mTvFilter.setVisibility(View.GONE);
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
