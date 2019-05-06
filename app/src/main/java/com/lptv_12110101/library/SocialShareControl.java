package com.lptv_12110101.library;

import com.kakao.KakaoLink;
import com.kakao.kakaolink.v2.KakaoLinkResponse;
import com.kakao.kakaolink.v2.KakaoLinkService;
import com.kakao.message.template.ButtonObject;
import com.kakao.message.template.ContentObject;
import com.kakao.message.template.FeedTemplate;
import com.kakao.message.template.LinkObject;
import com.kakao.network.ErrorResult;
import com.kakao.network.callback.ResponseCallback;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

public class SocialShareControl {
	private static SocialShareControl sControl;

	/***********************************************************************************************
	 * @AlarmControl Instance
	 **********************************************************************************************/
	public static SocialShareControl ShareInstance(){
		if(sControl == null){
			sControl = new SocialShareControl();
		}
		return sControl;
	}


	/***********************************************************************************************
	 *@shareContentInfo : 공유하기 Control
	 * @param _title : 공유 제목
	 * @param _descrption :공유 내용
	 * @param _imageUrl : 공유 내용에 들어갈 썸네일(카카오톡에서 사용)
	 * @param _imgWidth :썸네일의 가로 사이즈(카카오톡에서 사용)
	 * @param _imgHeight :썸네일의 세로 사이즈(카카오톡에서 사용)
	 * @param _webUrl : 기본적으로 사용될 URL 주소
	 * @param _shortUrl : 공유 내용에 들어갈 짧은 URL 주소
	 * @param _kakaoBtnTitle : 카카오톡 에서 사용할 버튼 제목
	 * @param _isFinish : 공유하기를 진행 후 해당 Activity를 종료할 것인지 여부
	 **********************************************************************************************/
	public void shareContentInfo(final Activity context, final String _title, final String _descrption, final String _imageUrl, final int _imgWidth, final int _imgHeight,
								 final String _webUrl, final String _shortUrl, final String _kakaoBtnTitle, final Boolean _isFinish){
		Intent sharingIntent = new Intent(Intent.ACTION_SEND);
		// what type of data needs to be send by sharing
		sharingIntent.setType("text/plain");
		// package names
		PackageManager pm = context.getPackageManager();
		// list package
		List<ResolveInfo> activityList = pm.queryIntentActivities(sharingIntent, 0);

		final ShareListAdapter objShareListAdapter = new ShareListAdapter(context, activityList.toArray());

		// Create alert dialog box
		AlertDialog.Builder builder = new AlertDialog.Builder(context);
		builder.setTitle("공유하기");
		builder.setAdapter(objShareListAdapter, new DialogInterface.OnClickListener() {

			public void onClick(DialogInterface dialog, int item) {
				ResolveInfo info = (ResolveInfo) objShareListAdapter.getItem(item);
				Intent intent;

				String packNm = info.activityInfo.packageName.toLowerCase();

				if (packNm.equals("com.kakao.talk")) {
					String urlStr = TextUtils.isEmpty(_webUrl) ? _shortUrl : _webUrl;

					KakaoLinkService.getInstance().sendScrap(context, urlStr, new ResponseCallback<KakaoLinkResponse>() {
						@Override
						public void onFailure(ErrorResult errorResult) {
							Log.e("KakaoLinkService_error", "ErrorResult:" + errorResult.toString());
						}

						@Override
						public void onSuccess(KakaoLinkResponse result) {
							Log.e("KakaoLinkService_success", "KakaoLinkResponse:" + result.toString());
						}
					});

				} else if(packNm.equals("com.kakao.story")){
					String post="", appname ="";
					try {
						if(TextUtils.isEmpty(_webUrl)){
							appname = Uri.parse(com.lptv_12110101.application.Application.application.getLptvDomain()).getHost();
							post = _descrption + "\n" + _shortUrl;
						}else{
							appname = Uri.parse(_webUrl).getHost();
							post = _descrption + "\n" + _webUrl;
						}
					}catch (Exception e){
						e.printStackTrace();
					}

					Map<String, Object> urlInfoAndroid = new Hashtable<String, Object>(1);
					urlInfoAndroid.put("title", _title);
					urlInfoAndroid.put("desc", _descrption);
					urlInfoAndroid.put("imageurl", new String[] {_imageUrl});
//						urlInfoAndroid.put("type", "website");

					StoryLink storyLink = StoryLink.getLink(context.getApplicationContext());

					if (!storyLink.isAvailableIntent()) {
						Toast.makeText(context, "카카오 스토리가 설치되지 않았습니다.", Toast.LENGTH_SHORT).show();
						return;
					}

					storyLink.openKakaoLink((Activity)context,
							post,
							context.getPackageName(),
							"1.0",
							appname,
							"UTF-8",
							urlInfoAndroid);
				}else {
					intent = new Intent(Intent.ACTION_SEND);
					intent.setClassName(info.activityInfo.packageName, info.activityInfo.name);
					intent.setType("text/plain"); //링크 및 테그스만 적용

					try {
						if (packNm.contains("facebook")) {
							intent.putExtra(Intent.EXTRA_TEXT, _webUrl);
						} else if(packNm.equals("com.nhn.android.band")) {
							intent.putExtra(Intent.EXTRA_TEXT, _descrption +"\n"+ _webUrl);
						} else {
							intent.putExtra(Intent.EXTRA_TEXT, _descrption +"\n"+ _webUrl);
						}
					}catch (Exception e){
						e.printStackTrace();
					}
					context.startActivity(intent);
				}

				if(_isFinish){
					context.finish();
				}
			}// end onClick
		});

		AlertDialog alert = builder.create();
		alert.show();
	}
}
