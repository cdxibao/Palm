package org.kteam.palm.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.apache.log4j.Logger;
import org.kteam.common.network.volleyext.RequestClient;
import org.kteam.common.utils.ViewUtils;
import org.kteam.palm.R;
import org.kteam.palm.adapter.PensionAdjustWagesAdapter;
import org.kteam.palm.common.utils.Constants;
import org.kteam.palm.common.utils.UserStateUtils;
import org.kteam.palm.common.view.HorizontalItemDecoration;
import org.kteam.palm.model.User;
import org.kteam.palm.network.NetworkUtils;
import org.kteam.palm.network.response.PensionAdjustWages;
import org.kteam.palm.network.response.PensionAdjustWagesResponse;
import org.kteam.palm.network.response.PensionForm;
import org.kteam.palm.network.response.PensionFormResponse;

import java.util.HashMap;

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
public class PensionAdjustWagesFragment extends Fragment {
    private final Logger mLogger = Logger.getLogger(getClass());
    private TextView mTvRetirementYearMonth;
    private TextView mTvRetirementPensionStart;
    private TextView mTvCurrPension;
    private TextView mTvAdjustWagesNum;
    private View mLayoutAdjustWagesInfo;
    private TextView mTvAdjustYear;
    private TextView mTvAdjustMoney;
    private View mLayoutAdjustTitle;

    private PensionForm mPensionForm;

    private PensionAdjustWagesAdapter mPensionAdjustWagesAdapter;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_pension_adjust_wages, container, false);
        initView(view);
        loadData();
        return view;
    }

    private void initView(View view) {

        mLayoutAdjustWagesInfo = view.findViewById(R.id.layout_adjust_wages_info);
        mTvRetirementYearMonth = (TextView) view.findViewById(R.id.tv_retirement_year_month);
        mTvRetirementPensionStart = (TextView) view.findViewById(R.id.tv_retirement_pension_start);
        mTvCurrPension = (TextView) view.findViewById(R.id.tv_curr_pension);
        mTvAdjustWagesNum = (TextView) view.findViewById(R.id.tv_adjust_wages_num);
        mTvAdjustYear = (TextView) view.findViewById(R.id.tv_adjust_year);
        mTvAdjustMoney = (TextView) view.findViewById(R.id.tv_adjust_money);
        mLayoutAdjustTitle = view.findViewById(R.id.layout_adjust_title);
        mLayoutAdjustTitle.setVisibility(View.GONE);

        RecyclerView recyclerView = (RecyclerView) view.findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.addItemDecoration(new HorizontalItemDecoration(getActivity()));

        mPensionAdjustWagesAdapter = new PensionAdjustWagesAdapter();
        recyclerView.setAdapter(mPensionAdjustWagesAdapter);
    }

    private void loadData() {
        User user = UserStateUtils.getInstance().getUser();
        HashMap<String, String> paramMap = new HashMap<String, String>();
        paramMap.put("aac001", user.social_security_card);
        String token = NetworkUtils.getToken(getActivity(), paramMap, Constants.URL_PENSION_FORM);
        paramMap.put("token", token);

        RequestClient<PensionFormResponse> requestClient = new RequestClient<PensionFormResponse>();
        requestClient.setOnLoadListener(new RequestClient.OnLoadListener<PensionFormResponse>() {
            @Override
            public void onLoadComplete(PensionFormResponse response) {
                if (response.code == 0 && response.body != null) {
                    if (response.body.size() > 0) {;
                        mPensionForm = response.body.get(0);
                        if (!TextUtils.isEmpty(mPensionForm.aic162) && mPensionForm.aic162.length() >= 6) {
                            mTvRetirementYearMonth.setText(getString(R.string.year_month, mPensionForm.aic162.substring(0, 4), mPensionForm.aic162.substring(4, 6)));
                        }
                        if (!TextUtils.isEmpty(mPensionForm.aic262)) {
                            mTvRetirementPensionStart.setText(getString(R.string.yuan, mPensionForm.aic262));
                        }
                        if (!TextUtils.isEmpty(mPensionForm.aic263)) {
                            mTvCurrPension.setText(getString(R.string.yuan, mPensionForm.aic263));
                        }
                        if (!TextUtils.isEmpty(mPensionForm.tzje)) {
                            mTvAdjustWagesNum.setText(getString(R.string.yuan, mPensionForm.tzje));
                        }
                        mLayoutAdjustWagesInfo.setVisibility(View.VISIBLE);
                        loadAdjustWagesData();
                    } else {
                        mLayoutAdjustWagesInfo.setVisibility(View.GONE);
                    }
                } else {
                    ViewUtils.showToast(getActivity(), response.msg);
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
                Constants.BASE_URL + Constants.URL_PENSION_FORM,
                PensionFormResponse.class,
                paramMap);
    }

    private void loadAdjustWagesData() {

        User user = UserStateUtils.getInstance().getUser();
        HashMap<String, String> paramMap = new HashMap<String, String>();
        paramMap.put("aac001", user.social_security_card);
        String token = NetworkUtils.getToken(getActivity(), paramMap, Constants.URL_PENSION_ADJUST_WAGES);
        paramMap.put("token", token);

        RequestClient<PensionAdjustWagesResponse> requestClient = new RequestClient<PensionAdjustWagesResponse>();
        requestClient.setOnLoadListener(new RequestClient.OnLoadListener<PensionAdjustWagesResponse>() {
            @Override
            public void onLoadComplete(PensionAdjustWagesResponse response) {
                if (response.code == 0) {
                    if (response.body != null && response.body.size() > 0) {
                        mLayoutAdjustTitle.setVisibility(View.VISIBLE);
                        mTvAdjustYear.setVisibility(View.VISIBLE);
                        mTvAdjustMoney.setVisibility(View.VISIBLE);
                        PensionAdjustWages pensionAdjustWages = response.body.get(response.body.size() - 1);
                        mTvAdjustYear.setText(getString(R.string.adjust_wages_year, pensionAdjustWages.aae002.substring(0, 4)));
                        mPensionAdjustWagesAdapter.setData(response.body);
                        mPensionAdjustWagesAdapter.notifyDataSetChanged();
                    }
                } else {
                    ViewUtils.showToast(getActivity(), response.msg);
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
                Constants.BASE_URL + Constants.URL_PENSION_ADJUST_WAGES,
                PensionAdjustWagesResponse.class,
                paramMap);
    }

}
