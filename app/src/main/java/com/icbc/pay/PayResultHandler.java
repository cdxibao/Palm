package com.icbc.pay;

import com.icbc.icbcfactory.ICBCPAPIFactory;
import com.icbc.paysdk.ICBCAPI;
import com.icbc.paysdk.IPayEventHandler;
import com.icbc.paysdk.constants.Constants;
import com.icbc.paysdk.model.PayResp;
import com.icbc.paysdk.model.ReqErr;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import org.kteam.palm.R;

public class PayResultHandler extends Activity implements IPayEventHandler {
	
	ICBCAPI api;
	TextView result_text;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.pay_result_handler_layout);
		result_text = (TextView) findViewById(R.id.pay_result);
		
		Log.i(Constants.LogFlag, "new ICBCPAPIFactory() ------ ");
		ICBCPAPIFactory factory = new ICBCPAPIFactory();
		api = factory.createICBCAPI(PayResultHandler.this);
		Log.i(Constants.LogFlag, "createICBCAPI()  ------  ");
		api.handleIntent(getIntent(), this);
				
	}
	
	
	@Override
	protected void onNewIntent(Intent intent) {
		// TODO Auto-generated method stub
		super.onNewIntent(intent);
		setIntent(intent);
		api.handleIntent(intent, this);
	}


	
	@Override
	public void onErr(ReqErr err) {
		// TODO Auto-generated method stub
		Log.i(Constants.LogFlag, "onErr() ...... ");
		
		result_text.setText("支付错误："+ err.getErrorType());
		
	}


	@Override
	public void onResp(PayResp resp) {
		// TODO Auto-generated method stub
		Log.i(Constants.LogFlag, "onResp() ...... ");
		String tranCode = resp.getTranCode();
		String tranMsg = resp.getTranMsg();
		String orderNo = resp.getOrderNo();
		
		if(tranCode.equals("200")){
			result_text.setText("支付成功");
		}
		
		if(tranCode.equals("201")){
			result_text.setText("支付失败");
		}
		
		
	}
	

}
