package org.kteam.palm.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.apache.log4j.Logger;
import org.kteam.common.network.volleyext.RequestClient;
import org.kteam.common.utils.ViewUtils;
import org.kteam.common.view.ProgressHUD;
import org.kteam.common.view.pickerview.OptionsPickerView;
import org.kteam.palm.BaseActivity;
import org.kteam.palm.MainActivity;
import org.kteam.palm.R;
import org.kteam.palm.adapter.YiliaoPayHistoryInfoAdapter;
import org.kteam.palm.common.utils.Constants;
import org.kteam.palm.common.view.ArticleDividerItemDecoration;
import org.kteam.palm.common.view.SpinnerEditText;
import org.kteam.palm.common.view.pullrefresh.PullLoadMoreRecyclerView;
import org.kteam.palm.model.BaseData;
import org.kteam.palm.network.NetworkUtils;
import org.kteam.palm.network.response.YiliaoPayHistoryInfoResponse;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

/**
 * @Package org.kteam.palm.activity
 * @Project Palm
 * @Description 职工医疗保险历年缴费情况明细（某年）
 * @Author Micky Liu
 * @Email mickyliu@126.com
 * @Team KTEAM
 * @Date 2015-12-12 18:13
 */
public class YiliaoPayHistoryInfoActivity extends BaseActivity implements View.OnClickListener,  DialogInterface.OnCancelListener, PullLoadMoreRecyclerView.PullLoadMoreListener {
    private final Logger mLogger = Logger.getLogger(getClass());

    private TextView mTvUserName;
    private TextView mTvIdCard;

    private View mLayoutUserInfo;
    private ProgressHUD mProgressHUD;
    private PullLoadMoreRecyclerView mPullLoadMoreRecyclerView;
    private RecyclerView mRecyclerView;
    private SpinnerEditText mSetType;
    private SpinnerEditText mSetEndTime;
    private OptionsPickerView<BaseData> mTypeOptions;
    private OptionsPickerView<BaseData> mBeginTimeOptions;

    private YiliaoPayHistoryInfoAdapter mYiliaoPayHistoryInfoAdapter;
    private String mYear;

    private int mItem1Width;
    private int mItem2Width;
    private int mItem3Width;
    private int mItem4Width;
    private int mPage = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_yiliao_pay_history_info);
        initToolBar();

        if (mUser == null) {
            finish();
            return;
        }
        mYear = getIntent().getStringExtra("year");
        initView();

        if (!TextUtils.isEmpty(mYear)) {
            findView(R.id.layout_search).setVisibility(View.GONE);
            setTitleText(getString(R.string.zgyibyhzmx_year, mYear));
            loadYearData(mYear);
        } else {
            setTitleText(getString(R.string.zgyibyhzmx));
            mPage = 1;
            mProgressHUD.show(getString(R.string.loading), true, true, this);
            loadUserData();
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mProgressHUD != null) {
            mProgressHUD.dismiss();
        }
    }

    private void initView() {
        mProgressHUD = new ProgressHUD(this);
        mLayoutUserInfo = findView(R.id.layout_user_info);

        mTvUserName = findView(R.id.tv_user_name);
        mTvUserName.setText(getString(R.string.order_username, mUser.name));
        mTvIdCard = findView(R.id.tv_id_card);
        mTvIdCard.setText(getString(R.string.order_idcard, mUser.idcard));

        mSetType = findView(R.id.set_type);
        mSetType.setTextWidth(ViewUtils.dip2px(this, 70));
        mSetType.setOnClickListener(this);

        mSetEndTime = findView(R.id.set_begin_time);
        mSetEndTime.setTextWidth(ViewUtils.dip2px(this, 70));
        mSetEndTime.setOnClickListener(this);

        initSpinnerSetData();

        findView(R.id.btn_search).setOnClickListener(this);


        mPullLoadMoreRecyclerView = (PullLoadMoreRecyclerView) findView(R.id.recyclerView);
        //获取mRecyclerView对象
        mRecyclerView = mPullLoadMoreRecyclerView.getRecyclerView();
        //代码设置scrollbar无效？未解决！
        mRecyclerView.setVerticalScrollBarEnabled(true);
        //设置下拉刷新是否可见
        mPullLoadMoreRecyclerView.setRefreshing(false);
        //设置是否可以下拉刷新
        mPullLoadMoreRecyclerView.setPullRefreshEnable(false);
        //设置是否可以下拉加载更多
        mPullLoadMoreRecyclerView.setPushRefreshEnable(true);
        //显示下拉刷新
//        mPullLoadMoreRecyclerView.setRefreshing(false);
        //设置上拉刷新文字
//        mPullLoadMoreRecyclerView.setFooterViewText("loading");
        //设置上拉刷新文字颜色
        //mPullLoadMoreRecyclerView.setFooterViewTextColor(R.color.white);
        //设置加载更多背景色
        //mPullLoadMoreRecyclerView.setFooterViewBackgroundColor(R.color.colorBackground);
        mPullLoadMoreRecyclerView.setLinearLayout();

        mPullLoadMoreRecyclerView.setOnPullLoadMoreListener(this);

        if (!TextUtils.isEmpty(mYear)) {
            mPullLoadMoreRecyclerView.setRefreshing(false);
            mPullLoadMoreRecyclerView.setIsLoadMore(false);
            mPullLoadMoreRecyclerView.setOnPullLoadMoreListener(null);
            mPullLoadMoreRecyclerView.setPushRefreshEnable(false);
            mPullLoadMoreRecyclerView.setPullRefreshEnable(false);
        }

        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.addItemDecoration(new ArticleDividerItemDecoration(this));

        mYiliaoPayHistoryInfoAdapter = new YiliaoPayHistoryInfoAdapter();
        mRecyclerView.setAdapter(mYiliaoPayHistoryInfoAdapter);

        DisplayMetrics metrics = ViewUtils.getScreenInfo(this);
        mItem1Width = (int) (metrics.widthPixels * 0.26);
        mItem2Width = (int) (metrics.widthPixels * 0.37);
        mItem3Width = (int) (metrics.widthPixels * 0.16);
        mItem4Width = (int) (metrics.widthPixels * 0.21);
        mYiliaoPayHistoryInfoAdapter.setItemWidth(mItem1Width, mItem2Width, mItem3Width, mItem4Width);

        LinearLayout layout1 = findView(R.id.layout1);
        LinearLayout layout2 = findView(R.id.layout2);
        LinearLayout layout3 = findView(R.id.layout3);
        LinearLayout layout4 = findView(R.id.layout4);

        layout1.getLayoutParams().width = mItem1Width;
        layout2.getLayoutParams().width = mItem2Width;
        layout3.getLayoutParams().width = mItem3Width;
        layout4.getLayoutParams().width = mItem4Width;
    }

    private void initSpinnerSetData() {
        ArrayList<BaseData> typeList = new ArrayList<BaseData>();
        typeList.add(new BaseData(getString(R.string.all), "0"));
        typeList.add(new BaseData(getString(R.string.ybxf), "1"));
        typeList.add(new BaseData(getString(R.string.zhhz), "2"));
        typeList.add(new BaseData(getString(R.string.jx), "3"));
        typeList.add(new BaseData(getString(R.string.grzhkbc), "5"));

        mTypeOptions = new OptionsPickerView<BaseData>(this);
        mTypeOptions.setPicker(typeList);
        mTypeOptions.setSelectOptions(0);
        BaseData baseData = typeList.get(0);
        mSetType.setTextWidth((int) ViewUtils.getTextViewLength(mSetType.getTextView().getPaint(), baseData.label) + ViewUtils.dip2px(this, 15));
        mSetType.setText(baseData.label);
        mSetType.setTag(baseData);

        mTypeOptions.setCyclic(false);
        mTypeOptions.setOnOptionsSelectListener(new OptionsPickerView.OnOptionsSelectListener() {
            @Override
            public void onOptionsSelect(int options1, int option2, int options3) {
                BaseData baseData = mTypeOptions.getmOptions1Items().get(options1);
                mSetType.setText(baseData.label);
                mSetType.setTag(baseData);
                mSetType.setTextWidth((int) ViewUtils.getTextViewLength(mSetType.getTextView().getPaint(), baseData.label) + ViewUtils.dip2px(YiliaoPayHistoryInfoActivity.this, 15));
                loadSearchData();
            }
        });

        ArrayList<BaseData> timeList = new ArrayList<BaseData>();
        timeList.add(new BaseData(getString(R.string.all), ""));
        Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int endYear = 2015;

        for (int i = year; i >= endYear; i--) {

            for (int j = 12; j >= 1; j--) {
                if (j < 10) {
                    timeList.add(new BaseData(getString(R.string.year_month, String.valueOf(i), String.valueOf(j)), i + "0" + j));
                } else {
                    timeList.add(new BaseData(getString(R.string.year_month, String.valueOf(i), String.valueOf(j)), i + "" + j));
                }
            }
        }

        mBeginTimeOptions = new OptionsPickerView<BaseData>(this);
        mBeginTimeOptions.setPicker(timeList);
        mBeginTimeOptions.setSelectOptions(0);
        baseData = timeList.get(0);
        mSetEndTime.setTextWidth((int) ViewUtils.getTextViewLength(mSetEndTime.getTextView().getPaint(), baseData.label) + ViewUtils.dip2px(this, 15));
        mSetEndTime.setText(baseData.label);
        mSetEndTime.setTag(baseData);

        mBeginTimeOptions.setCyclic(false);
        mBeginTimeOptions.setOnOptionsSelectListener(new OptionsPickerView.OnOptionsSelectListener() {
            @Override
            public void onOptionsSelect(int options1, int option2, int options3) {
                BaseData baseData = mBeginTimeOptions.getmOptions1Items().get(options1);
                mSetEndTime.setText(baseData.label);
                mSetEndTime.setTag(baseData);
                mSetEndTime.setTextWidth((int) ViewUtils.getTextViewLength(mSetType.getTextView().getPaint(), baseData.label) + ViewUtils.dip2px(YiliaoPayHistoryInfoActivity.this, 15));
                loadSearchData();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.go_home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (ViewUtils.isFastClick()) return super.onOptionsItemSelected(item);
        int id = item.getItemId();
        if (id == R.id.action_home){
            Intent intent = new Intent(this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onRefresh() {

    }

    @Override
    public void onLoadMore() {
        mPage++;
        loadUserData();
    }

    private void loadYearData(String year) {
        mProgressHUD.show(getString(R.string.loading), true, true, this);

        HashMap<String, String> paramMap = new HashMap<String, String>();
        paramMap.put("aac002", String.valueOf(mUser.idcard));
        paramMap.put("aac003", String.valueOf(mUser.name));
        paramMap.put("year", year);
        String token = NetworkUtils.getToken(this, paramMap, Constants.URL_YL_PAY_HISTORY_YUE);
        paramMap.put("token", token);

        RequestClient<YiliaoPayHistoryInfoResponse> requestClient = new RequestClient<YiliaoPayHistoryInfoResponse>();
        requestClient.setUseProgress(false);
        requestClient.setOnLoadListener(new RequestClient.OnLoadListener<YiliaoPayHistoryInfoResponse>() {
            @Override
            public void onLoadComplete(YiliaoPayHistoryInfoResponse response) {
                mProgressHUD.dismiss();
                if (response.code == 0) {
                    if (response.body != null && response.body.size() > 0) {
                        mYiliaoPayHistoryInfoAdapter.setData(response.body);
                        mYiliaoPayHistoryInfoAdapter.notifyDataSetChanged();
                    }
                } else {
                    ViewUtils.showToast(YiliaoPayHistoryInfoActivity.this, response.msg);
                    finish();
                }
            }

            @Override
            public void onNetworkError() {
                mLogger.error(getString(R.string.network_error));
                ViewUtils.showToast(YiliaoPayHistoryInfoActivity.this, R.string.network_error);
                mProgressHUD.dismiss();
                finish();
            }
        });
        requestClient.executePost(this,
                getString(R.string.loading),
                Constants.BASE_URL + Constants.URL_YL_PAY_HISTORY_YUE,
                YiliaoPayHistoryInfoResponse.class,
                paramMap);
    }

    private void loadSearchData() {
        mPage = 1;
        mYiliaoPayHistoryInfoAdapter.clearData();
        mYiliaoPayHistoryInfoAdapter.notifyDataSetChanged();
        mProgressHUD.show(getString(R.string.loading), true, true, this);
        loadUserData();
    }

    private void loadUserData() {
        String type = mSetType.getTag() == null ? "0" : ((BaseData) mSetType.getTag()).value;
        String time = mSetEndTime.getTag() == null ? "" : ((BaseData) mSetEndTime.getTag()).value;
        loadUserData(type, time);
    }

    private void loadUserData(String type, String endTime) {

        HashMap<String, String> paramMap = new HashMap<String, String>();
        paramMap.put("aac002", String.valueOf(mUser.idcard));
        paramMap.put("aac003", String.valueOf(mUser.name));
        paramMap.put("zhszlx", type);
        paramMap.put("startDate", "");
        paramMap.put("endDate", endTime);
        paramMap.put("pageNum", String.valueOf(mPage));
        String token = NetworkUtils.getToken(this, paramMap, Constants.URL_YL_PAY_HISTORY_MX);
        paramMap.put("token", token);

        RequestClient<YiliaoPayHistoryInfoResponse> requestClient = new RequestClient<YiliaoPayHistoryInfoResponse>();
        requestClient.setUseProgress(false);
        requestClient.setOnLoadListener(new RequestClient.OnLoadListener<YiliaoPayHistoryInfoResponse>() {
            @Override
            public void onLoadComplete(YiliaoPayHistoryInfoResponse response) {
                mProgressHUD.dismiss();
                mPullLoadMoreRecyclerView.setPullLoadMoreCompleted();
                if (response.code == 0) {
                    if (response.body != null && response.body.size() > 0) {
                        mYiliaoPayHistoryInfoAdapter.appendListData(response.body);
                        mYiliaoPayHistoryInfoAdapter.notifyDataSetChanged();
                    }
                } else {
                    mPage--;
                    ViewUtils.showToast(YiliaoPayHistoryInfoActivity.this, response.msg);
                    finish();
                }
            }

            @Override
            public void onNetworkError() {
                mPullLoadMoreRecyclerView.setPullLoadMoreCompleted();
                mPage--;
                mLogger.error(getString(R.string.network_error));
                ViewUtils.showToast(YiliaoPayHistoryInfoActivity.this, R.string.network_error);
                mProgressHUD.dismiss();
                finish();
            }
        });
        requestClient.executePost(this,
                getString(R.string.loading),
                Constants.BASE_URL + Constants.URL_YL_PAY_HISTORY_MX,
                YiliaoPayHistoryInfoResponse.class,
                paramMap);
    }


    @Override
    public void onCancel(DialogInterface dialog) {
        finish();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.set_type:
                if (mTypeOptions != null && mTypeOptions.getmOptions1Items() != null && mTypeOptions.getmOptions1Items().size() > 0) {
                    if (mSetType.getTag() != null && mSetType.getTag() instanceof  BaseData) {
                        BaseData baseData = (BaseData) mSetType.getTag();
                        int index = mTypeOptions.getmOptions1Items().indexOf(baseData);
                        if (index != -1) {
                            mTypeOptions.setSelectOptions(index);
                        }
                    }
                    mTypeOptions.show();
                    mTypeOptions.setWheelGravity(Gravity.CENTER);
                }
                break;
            case R.id.set_begin_time:
                if (mBeginTimeOptions != null && mBeginTimeOptions.getmOptions1Items() != null && mBeginTimeOptions.getmOptions1Items().size() > 0) {
                    if (mSetEndTime.getTag() != null && mSetEndTime.getTag() instanceof  BaseData) {
                        BaseData baseData = (BaseData) mSetEndTime.getTag();
                        int index = mBeginTimeOptions.getmOptions1Items().indexOf(baseData);
                        if (index != -1) {
                            mBeginTimeOptions.setSelectOptions(index);
                        }
                    }
                    mBeginTimeOptions.show();
                    mBeginTimeOptions.setWheelGravity(Gravity.CENTER);
                }
                break;
            case R.id.btn_search:

                break;
            default:
                break;
        }
    }
}
