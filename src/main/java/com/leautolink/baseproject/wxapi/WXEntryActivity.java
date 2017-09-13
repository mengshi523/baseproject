package com.leautolink.baseproject.wxapi;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.letv.loginsdk.LetvLoginSdkManager;
import com.tencent.mm.sdk.modelbase.BaseReq;
import com.tencent.mm.sdk.modelbase.BaseResp;
import com.tencent.mm.sdk.modelmsg.SendAuth;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

public class WXEntryActivity extends Activity implements IWXAPIEventHandler {
	private IWXAPI api;
	public String code;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(com.letv.loginsdk.R.layout.activity_wxentry);
		api = WXAPIFactory.createWXAPI(this, LetvLoginSdkManager.WX_APP_ID, false);
		api.handleIntent(getIntent(), this);
	}

	@Override
	public void onReq(BaseReq req) {
		finish();
	}

	@Override
	public void onResp(BaseResp resp) {
		switch (resp.errCode) {
		case BaseResp.ErrCode.ERR_OK:
			code = ((SendAuth.Resp) resp).code;
			new LetvLoginSdkManager().getAccessTokenByCode(code, LetvLoginSdkManager.WX_APP_ID, LetvLoginSdkManager.WX_APP_SECRET, WXEntryActivity.this);
			finish();
			break;
		default:
			finish();
			break;
		}

	}

	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		setIntent(intent);
		api.handleIntent(intent, this);
		finish();
	}
}
