package org.kteam.palm.common.view;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.kteam.palm.R;
import org.kteam.palm.model.User;

/**
 * @Package org.kteam.palm.common.view
 * @Project Palm
 * @Description
 * @Author Micky Liu
 * @Email mickyliu@126.com
 * @Team KTEAM
 * @Date 2015-12-26 13:43
 */
public class UserInfoView extends LinearLayout {

    private TextView mTvUserName;
    private TextView mTvIDCard;
    private TextView mTvContactAddr;
    private TextView mTvContactPhone;
    private View mLayoutUserInfo;
    private View mLayoutPhone;
    private View mLayoutAddress;
    
    public UserInfoView(Context context) {
        super(context);
        init(context);
    }

    public UserInfoView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context) {
        LayoutInflater.from(context).inflate(R.layout.view_user_info, this);
        mTvIDCard = (TextView) findViewById(R.id.tv_idcard);
        mTvUserName = (TextView) findViewById(R.id.tv_username);
        mTvContactPhone = (TextView) findViewById(R.id.tv_contact_phone);
        mTvContactAddr = (TextView) findViewById(R.id.tv_contact_addr);
        mLayoutUserInfo = findViewById(R.id.layout_user_info);
        mLayoutPhone = findViewById(R.id.layout_phone);
        mLayoutAddress = findViewById(R.id.layout_address);
    }

    public void hidePhoneAndAddress() {
        mLayoutPhone.setVisibility(View.GONE);
        mLayoutAddress.setVisibility(View.GONE);
    }

    public void setUser(User user) {
        mLayoutUserInfo.setVisibility(View.GONE);
        if (!TextUtils.isEmpty(user.name)) {
            mLayoutUserInfo.setVisibility(View.VISIBLE);
            mTvIDCard.setText(user.idcard);
            mTvUserName.setText(user.name);
            mTvContactPhone.setText(user.phone);
            if (!TextUtils.isEmpty(user.address)) {
                mTvContactAddr.setText(user.address);
            } else {
//                mTvContactAddr.setText(getContext().getString(R.string.no_address));
            }
        }
    }
}
