package org.kteam.palm.activity;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.Menu;
import android.widget.RadioGroup;

import org.kteam.common.view.ProgressHUD;
import org.kteam.palm.BaseActivity;
import org.kteam.palm.R;
import org.kteam.palm.common.utils.LoadDataListener;
import org.kteam.palm.common.view.NoScrollViewPager;
import org.kteam.palm.fragment.PensionAdjustWagesFragment;
import org.kteam.palm.fragment.PensionGrantInfoFragment;
import org.kteam.palm.fragment.QueryYanglaoAccountNjFragment;
import org.kteam.palm.fragment.QueryYanglaoAccountPayInfoFragment;

import java.util.ArrayList;
import java.util.List;

/**
 * @Package org.kteam.palm.activity
 * @Project Palm
 * @Description 养老保险个人账户情况
 * @Author Micky Liu
 * @Email mickyliu@126.com
 * @Team KTEAM
 * @Date 2015-12-12 18:13
 */
public class QueryYanglaoAccountInfoActivity extends BaseActivity implements RadioGroup.OnCheckedChangeListener, LoadDataListener, DialogInterface.OnCancelListener {
    private NoScrollViewPager mViewPager;

    private List<Fragment> mFragmentList;

    private ProgressHUD mProgressHUD;
    private int loadDataCount = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_query_yanglao_account_info);
        initToolBar();

        if (mUser == null) {
            finish();
            return;
        }
        setTitleText(getString(R.string.ylbxjfqk1, mUser.name));
        initView();
        mProgressHUD.show(getString(R.string.loading), true, true, this);
    }

    private void initView() {
        mProgressHUD = new ProgressHUD(this);

        RadioGroup rgTab = findView(R.id.rg_tab);
        rgTab.setOnCheckedChangeListener(this);
        mViewPager =  findView(R.id.view_pager);

        mFragmentList = new ArrayList<Fragment>(2);

        QueryYanglaoAccountPayInfoFragment accountPayInfoFragment = new QueryYanglaoAccountPayInfoFragment();
        accountPayInfoFragment.setLoadDataListener(this);
        mFragmentList.add(accountPayInfoFragment);
        QueryYanglaoAccountNjFragment accountNjFragment = new QueryYanglaoAccountNjFragment();
        accountNjFragment.setLoadDataListener(this);
        mFragmentList.add(accountNjFragment);
        mViewPager.setAdapter(new PensionViewPagerAdapter(getSupportFragmentManager()));
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        switch (group.getCheckedRadioButtonId()) {
            case R.id.rb_grant_info:
                setTitleText(getString(R.string.ylbxjfqk1, mUser.name));
                mViewPager.setCurrentItem(0);
                break;
            case R.id.rb_adjust_wages:
                setTitleText(getString(R.string.ylbxzynj1, mUser.name));
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

    @Override
    public void onCompleted() {
        loadDataCount++;
        if (loadDataCount >= 2) {
            mProgressHUD.dismiss();
        }
    }

    @Override
    public void onCancel(DialogInterface dialog) {
        finish();
    }

}
