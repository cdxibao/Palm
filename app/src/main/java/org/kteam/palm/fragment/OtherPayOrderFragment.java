package org.kteam.palm.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import org.apache.log4j.Logger;
import org.kteam.common.network.volleyext.RequestClient;
import org.kteam.common.utils.ViewUtils;
import org.kteam.common.view.pickerview.OptionsPickerView;
import org.kteam.palm.R;
import org.kteam.palm.activity.OrderInfoActivity;
import org.kteam.palm.adapter.BaseListAdapter;
import org.kteam.palm.adapter.OrderAdapter;
import org.kteam.palm.common.utils.Constants;
import org.kteam.palm.common.utils.UserStateUtils;
import org.kteam.palm.common.view.OnRVItemClickListener;
import org.kteam.palm.domain.manager.BaseDataManager;
import org.kteam.palm.model.BaseData;
import org.kteam.palm.model.User;
import org.kteam.palm.model.Year;
import org.kteam.palm.network.NetworkUtils;
import org.kteam.palm.network.response.OrderListResponse;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * @Package org.kteam.palm.fragment
 * @Project Palm
 *
 * @Description  为他人缴费订单
 *
 * @Author Micky Liu
 * @Email mickyliu@126.com
 * @Team KTEAM
 * @Date 2015-12-16 23:09
 */
public class OtherPayOrderFragment extends Fragment implements OnRVItemClickListener {
    private final Logger mLogger = Logger.getLogger(getClass());

    private RecyclerView mRvOrder;
    private OrderAdapter mOrderAdapter;

    private BaseData mPvSelected;
    private OptionsPickerView<BaseData> mPvOptions;
    private ArrayList<BaseData> mPayedStatusList;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_other_pay_order, container, false);
        initView(view);

        loadData("");
        return view;
    }

    private void initView(View view) {
        mRvOrder = (RecyclerView) view.findViewById(R.id.recyclerView);
        mRvOrder.setHasFixedSize(true);
//        mRvOrder.addItemDecoration(new ArticleDividerItemDecoration(getActivity()));

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        linearLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRvOrder.setLayoutManager(linearLayoutManager);

        mOrderAdapter = new OrderAdapter(true);
        mOrderAdapter.setOnItemClickListener(this);
        mRvOrder.setAdapter(mOrderAdapter);

        mPayedStatusList = (ArrayList)new BaseDataManager().getPayStatusList();
        if (mPayedStatusList != null) {
            mPvOptions = new OptionsPickerView<BaseData>(getActivity());
            mPvOptions.setPicker(mPayedStatusList);
            mPvOptions.setCyclic(false);
            mPvOptions.setOnOptionsSelectListener(new OptionsPickerView.OnOptionsSelectListener() {
                @Override
                public void onOptionsSelect(int options1, int option2, int options3) {
                    mPvSelected = mPvOptions.getmOptions1Items().get(options1);
                    loadData(mPvSelected.value);
                }
            });
        }
    }

    private void loadData(String payStatus) {
        User user = UserStateUtils.getInstance().getUser();
        HashMap<String, String> paramMap = new HashMap<String, String>();
        paramMap.put("aac001", user.social_security_card);
        paramMap.put("payStatus", payStatus);
        paramMap.put("type", "1");
        paramMap.put("category", "");
        String token = NetworkUtils.getToken(getActivity(), paramMap, Constants.URL_GET_ORDER);
        paramMap.put("token", token);

        RequestClient<OrderListResponse> requestClient = new RequestClient<OrderListResponse>();
        requestClient.setOnLoadListener(new RequestClient.OnLoadListener<OrderListResponse>() {
            @Override
            public void onLoadComplete(OrderListResponse response) {
                if (response.code == 0) {
                    mOrderAdapter.setData(response.body);
                    mOrderAdapter.notifyDataSetChanged();
                } else {
                    ViewUtils.showToast(getActivity(), response.msg);
                }
            }

            @Override
            public void onNetworkError() {
                mLogger.error(getString(R.string.network_error));
            }
        });
        requestClient.executePost(getActivity(),
                getString(R.string.loading),
                Constants.BASE_URL + Constants.URL_GET_ORDER,
                OrderListResponse.class,
                paramMap);
    }

    @Override
    public void onItemClick(BaseListAdapter adapter, int position) {
        Intent intent = new Intent(getActivity(), OrderInfoActivity.class);
        intent.putExtra("order", mOrderAdapter.getItem(position));
        intent.putExtra("payedOther", true);
        startActivity(intent);
    }

    public void showSelector() {
        if (mPvOptions != null && mPvOptions.getmOptions1Items() != null && mPvOptions.getmOptions1Items().size() > 0) {
            if (mPvSelected != null) {
                int index = mPvOptions.getmOptions1Items().indexOf(mPvSelected);
                mPvOptions.setSelectOptions(index);
            }
            mPvOptions.show();
        }
    }
}
