package org.kteam.palm.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import org.apache.log4j.Logger;
import org.kteam.common.network.volleyext.RequestClient;
import org.kteam.common.utils.ViewUtils;
import org.kteam.palm.R;
import org.kteam.palm.adapter.YanglaoAccountPayInfoAdapter;
import org.kteam.palm.common.utils.Constants;
import org.kteam.palm.common.utils.LoadDataListener;
import org.kteam.palm.common.utils.UserStateUtils;
import org.kteam.palm.common.view.ArticleDividerItemDecoration;
import org.kteam.palm.model.User;
import org.kteam.palm.network.NetworkUtils;
import org.kteam.palm.network.response.AccountPayInfoResponse;

import java.util.HashMap;

/**
 * @Package org.kteam.palm.fragment
 * @Project Palm
 *
 * @Description 养老保险个人缴费情况
 *
 * @Author Micky Liu
 * @Email mickyliu@126.com
 * @Team KTEAM
 * @Date 2017-2-12 23:09
 */
public class QueryYanglaoAccountNjFragment extends Fragment implements View.OnClickListener {
    private final Logger mLogger = Logger.getLogger(getClass());

    private int mItem1Width;
    private int mItem2Width;
    private int mItem3Width;
    private int mItem4Width;

    private LoadDataListener mLoadDataListener;


    private YanglaoAccountPayInfoAdapter mYanglaoAccountPayInfoAdapter;
    private User mUser;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_query_yanglao_account_pay_info, container, false);
        mUser = UserStateUtils.getInstance().getUser();
        initView(view);
        loadData();
        return view;
    }

    private void initView(View view) {
        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.addItemDecoration(new ArticleDividerItemDecoration(getActivity()));

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(linearLayoutManager);

        mYanglaoAccountPayInfoAdapter = new YanglaoAccountPayInfoAdapter();
        recyclerView.setAdapter(mYanglaoAccountPayInfoAdapter);

        DisplayMetrics metrics = ViewUtils.getScreenInfo(getActivity());
        mItem1Width = (int) (metrics.widthPixels * 0.2);
        mItem2Width = (int) (metrics.widthPixels * 0.25);
        mItem3Width = (int) (metrics.widthPixels * 0.25);
        mItem4Width = (int) (metrics.widthPixels * 0.3);
        mYanglaoAccountPayInfoAdapter.setItemWidth(mItem1Width, mItem2Width, mItem3Width, mItem4Width);

        LinearLayout layout1 = (LinearLayout) view.findViewById(R.id.layout1);
        LinearLayout layout2 = (LinearLayout) view.findViewById(R.id.layout2);
        LinearLayout layout3 = (LinearLayout) view.findViewById(R.id.layout3);
        LinearLayout layout4 = (LinearLayout) view.findViewById(R.id.layout4);


        layout1.getLayoutParams().width = mItem1Width;
        layout2.getLayoutParams().width = mItem2Width;
        layout3.getLayoutParams().width = mItem3Width;
        layout4.getLayoutParams().width = mItem4Width;
    }

//    private void initSpinnerSetData() {
//        ArrayList<BaseData> timeList = new ArrayList<BaseData>();
//        timeList.add(new BaseData(getString(R.string.qxz), ""));
//        Calendar c = Calendar.getInstance();
//        int year = c.get(Calendar.YEAR);
//        int endYear = 1996;
//
//        for (int i = year; i >= endYear; i--) {
//            timeList.add(new BaseData(getString(R.string.year, String.valueOf(i)), String.valueOf(i)));
//        }
//
//        mYearOptions = new OptionsPickerView<BaseData>(getActivity());
//        mYearOptions.setPicker(timeList);
//        mYearOptions.setSelectOptions(0);
//        BaseData baseData = timeList.get(0);
//        mSetYear.setTextWidth((int) ViewUtils.getTextViewLength(mSetYear.getTextView().getPaint(), baseData.label) + ViewUtils.dip2px(getActivity(), 15));
//        mSetYear.setText(baseData.label);
//        mSetYear.setTag(baseData);
//
//        mYearOptions.setCyclic(false);
//        mYearOptions.setOnOptionsSelectListener(new OptionsPickerView.OnOptionsSelectListener() {
//            @Override
//            public void onOptionsSelect(int options1, int option2, int options3) {
//                BaseData baseData = mYearOptions.getmOptions1Items().get(options1);
//                mSetYear.setText(baseData.label);
//                mSetYear.setTag(baseData);
//                mSetYear.setTextWidth((int) ViewUtils.getTextViewLength(mSetYear.getTextView().getPaint(), baseData.label) + ViewUtils.dip2px(getActivity(), 15));
//                loadSearchData();
//            }
//        });
//    }

    @Override
    public void onClick(View v) {
//        switch (v.getId()) {
//            case R.id.set_year:
//                if (mYearOptions != null && mYearOptions.getmOptions1Items() != null && mYearOptions.getmOptions1Items().size() > 0) {
//                    if (mSetYear.getTag() != null && mSetYear.getTag() instanceof BaseData) {
//                        BaseData baseData = (BaseData) mSetYear.getTag();
//                        int index = mYearOptions.getmOptions1Items().indexOf(baseData);
//                        if (index != -1) {
//                            mYearOptions.setSelectOptions(index);
//                        }
//                    }
//                    mYearOptions.show();
//                    mYearOptions.setWheelGravity(Gravity.CENTER);
//                }
//                break;
//        }
    }

    public void setLoadDataListener(LoadDataListener listener) {
        mLoadDataListener = listener;
    }

    public void loadData() {
        mYanglaoAccountPayInfoAdapter.clearData();
        mYanglaoAccountPayInfoAdapter.notifyDataSetChanged();


        HashMap<String, String> paramMap = new HashMap<String, String>();
        paramMap.put("aac002", mUser.idcard);
        paramMap.put("aac003", mUser.name);
        String token = NetworkUtils.getToken(getActivity(), paramMap, Constants.URL_YANGLAO_ACCOUT_NJ_INFO);
        paramMap.put("token", token);

        RequestClient<AccountPayInfoResponse> requestClient = new RequestClient<AccountPayInfoResponse>();
        requestClient.setUseProgress(false);
        requestClient.setOnLoadListener(new RequestClient.OnLoadListener<AccountPayInfoResponse>() {
            @Override
            public void onLoadComplete(AccountPayInfoResponse response) {
                if (mLoadDataListener != null) {
                    mLoadDataListener.onCompleted();
                }
                if (response.code == 0) {
                    if (response.body != null && response.body.size() > 0) {
                        mYanglaoAccountPayInfoAdapter.setData(response.body);
                        mYanglaoAccountPayInfoAdapter.notifyDataSetChanged();
                    }
                } else {
                    ViewUtils.showToast(getActivity(), response.msg);
                }
            }

            @Override
            public void onNetworkError() {
                if (mLoadDataListener != null) {
                    mLoadDataListener.onCompleted();
                }
                mLogger.error(getString(R.string.network_error));
                ViewUtils.showToast(getActivity(), R.string.network_error);
            }
        });
        requestClient.executePost(getActivity(),
                getString(R.string.loading),
                Constants.BASE_URL + Constants.URL_YANGLAO_ACCOUT_NJ_INFO,
                AccountPayInfoResponse.class,
                paramMap);
    }


}
