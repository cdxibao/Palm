package org.kteam.palm.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import org.apache.log4j.Logger;
import org.kteam.common.network.volleyext.RequestClient;
import org.kteam.common.utils.ViewUtils;
import org.kteam.common.view.pickerview.OptionsPickerView;
import org.kteam.palm.R;
import org.kteam.palm.adapter.PensionGrantAdapter;
import org.kteam.palm.common.utils.Constants;
import org.kteam.palm.common.utils.UserStateUtils;
import org.kteam.palm.common.view.HorizontalItemDecoration;
import org.kteam.palm.model.User;
import org.kteam.palm.model.Year;
import org.kteam.palm.network.NetworkUtils;
import org.kteam.palm.network.response.PensionGrantInfo;
import org.kteam.palm.network.response.PensionGrantResponse;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

/**
 * @Package org.kteam.palm.fragment
 * @Project Palm
 *
 * @Description  养老金构成情况
 *
 * @Author Micky Liu
 * @Email mickyliu@126.com
 * @Team KTEAM
 * @Date 2015-12-16 23:09
 */
public class PensionGrantInfoFragment extends Fragment {
    private final Logger mLogger = Logger.getLogger(getClass());

    private PensionGrantAdapter mPensionGrantAdapter;
    private OptionsPickerView<Year> mPvOptionsYear;
    private Year mCurSelectYear;

    private int mItem1Width;
    private int mItem2Width;
    private int mItem3Width;
    private int mItem4Width;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_pension_grant_info, container, false);
        initView(view);
        return view;
    }


    @Override
    public void onResume() {
        super.onResume();
        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        loadData(year);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_filter) {
            if (mPvOptionsYear != null && mPvOptionsYear.getmOptions1Items() != null && mPvOptionsYear.getmOptions1Items().size() > 0) {
                if (mCurSelectYear != null) {
                    int index = mPvOptionsYear.getmOptions1Items().indexOf(mCurSelectYear);
                    mPvOptionsYear.setSelectOptions(index);
                }
                mPvOptionsYear.show();
            }
        }
        return true;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mPvOptionsYear != null) {
            mPvOptionsYear = null;
        }
    }

    private void initView(View view) {

        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.addItemDecoration(new HorizontalItemDecoration(getActivity()));

        mPensionGrantAdapter = new PensionGrantAdapter();
        recyclerView.setAdapter(mPensionGrantAdapter);

        DisplayMetrics metrics = ViewUtils.getScreenInfo(getActivity());
        mItem1Width = (int) (metrics.widthPixels * 0.25);
        mItem2Width = (int) (metrics.widthPixels * 0.25);
        mItem3Width = (int) (metrics.widthPixels * 0.25);
        mItem4Width = (int) (metrics.widthPixels * 0.25);
        mPensionGrantAdapter.setItemWidth(mItem1Width, mItem2Width, mItem3Width, mItem4Width);

        LinearLayout layout1 = (LinearLayout) view.findViewById(R.id.layout1);
        LinearLayout layout2 = (LinearLayout) view.findViewById(R.id.layout2);
        LinearLayout layout3 = (LinearLayout) view.findViewById(R.id.layout3);
        LinearLayout layout4 = (LinearLayout) view.findViewById(R.id.layout4);

        layout1.getLayoutParams().width = mItem1Width;
        layout2.getLayoutParams().width = mItem2Width;
        layout3.getLayoutParams().width = mItem3Width;
        layout4.getLayoutParams().width = mItem4Width;

        ArrayList<Year> yearList = new ArrayList<Year>(50);
        Calendar cal = Calendar.getInstance();
        int curYear = cal.get(Calendar.YEAR);
        for (int i = curYear; i > curYear - 50; i--) {
            Year year = new Year();
            year.year = i;
            yearList.add(year);
        }
        mPvOptionsYear = new OptionsPickerView<Year>(getActivity());
        mPvOptionsYear.setPicker(yearList);
        mPvOptionsYear.setCyclic(false);
        mPvOptionsYear.setOnOptionsSelectListener(new OptionsPickerView.OnOptionsSelectListener() {
            @Override
            public void onOptionsSelect(int options1, int option2, int options3) {
                mCurSelectYear = mPvOptionsYear.getmOptions1Items().get(options1);
                loadData(mCurSelectYear.year);
            }
        });

    }

    private void loadData(int year) {
        User user = UserStateUtils.getInstance().getUser();
        HashMap<String, String> paramMap = new HashMap<String, String>();
        paramMap.put("aac001", user.social_security_card);
        paramMap.put("zac001", String.valueOf(year));
        String token = NetworkUtils.getToken(getActivity(), paramMap, Constants.URL_GET_PENSION_GRANT);
        paramMap.put("token", token);

        RequestClient<PensionGrantResponse> requestClient = new RequestClient<PensionGrantResponse>();
        requestClient.setOnLoadListener(new RequestClient.OnLoadListener<PensionGrantResponse>() {
            @Override
            public void onLoadComplete(PensionGrantResponse response) {
                if (response.code == 0) {
                    if (response.body != null) {
                        List<PensionGrantInfo> payInfoList = response.body;
                        mPensionGrantAdapter.setData(payInfoList);
                        mPensionGrantAdapter.notifyDataSetChanged();
                    }
                } else {
                    if (!TextUtils.isEmpty(response.msg)) {
                        ViewUtils.showToast(getActivity(), response.msg);
                    } else {
                        ViewUtils.showToast(getActivity(), R.string.failed_get_pension_grant);
                    }
                }
            }

            @Override
            public void onNetworkError() {
                mLogger.error(getString(R.string.network_error));
                ViewUtils.showToast(getActivity(), R.string.network_error);
            }
        });
        requestClient.executePost(getActivity(),
                getString(R.string.loading),
                Constants.BASE_URL + Constants.URL_GET_PENSION_GRANT,
                PensionGrantResponse.class,
                paramMap);
    }

    public void showYearSelector() {
        if (mPvOptionsYear != null && mPvOptionsYear.getmOptions1Items() != null && mPvOptionsYear.getmOptions1Items().size() > 0) {
            if (mCurSelectYear != null) {
                int index = mPvOptionsYear.getmOptions1Items().indexOf(mCurSelectYear);
                mPvOptionsYear.setSelectOptions(index);
            }
            mPvOptionsYear.show();
        }
    }
}
