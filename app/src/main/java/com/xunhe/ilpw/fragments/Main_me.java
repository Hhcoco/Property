package com.xunhe.ilpw.fragments;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;
import com.xunhe.ilpw.R;
import com.xunhe.ilpw.activity.ApplyRecordActivity;
import com.xunhe.ilpw.activity.NoticeActivity;
import com.xunhe.ilpw.activity.PersonAdminActivity;
import com.xunhe.ilpw.activity.PersonDataActivity;
import com.xunhe.ilpw.activity.SettingActivity;
import com.xunhe.ilpw.activity.SuggestionActivity;
import com.xunhe.ilpw.application.BaseApplication;
import com.xunhe.ilpw.config.Config;
import com.xunhe.ilpw.model.MEventBusMsg;
import com.xunhe.ilpw.model.MUserInfo;
import com.xunhe.ilpw.utils.HttpHelper;
import com.xunhe.ilpw.utils.LoadingDialog;
import com.xunhe.ilpw.utils.OperateHelper;
import com.xunhe.ilpw.utils.OriginalDialog;
import com.xunhe.ilpw.utils.SPHelper;
import com.xunhe.ilpw.utils.UriToPath;
import com.xunhe.ilpw.utils.WlCallback;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

import de.hdodenhof.circleimageview.CircleImageView;
import okhttp3.Call;
import okhttp3.Request;

/**
 * Created by wangliang on 2016/7/15.
 */
public class Main_me extends Fragment implements View.OnClickListener{

    private View view;
    private RelativeLayout mRlRecord , mRlNotice , mRlSetting , mRlAdmin , mRlFamily , mRlSuggestion;
    private TextView mTvName;
    private CircleImageView mImgProfile;
    /*头像临时路径*/
    private static final String TEMP_IMG_NAME = "xunhe_temp_head.jpg";
    private static final String TEMP_IMG_CROP = "xunhe_temp_crop.jpg";
    /* 请求识别码 */
    private static final int CODE_GALLERY_REQUEST = 0xa0;
    private static final int CODE_CAMERA_REQUEST = 0xa1;
    private static final int CODE_RESULT_REQUEST = 0xa2;
    // 裁剪后图片的宽(X)和高(Y),480 X 480的正方形。
    private static int output_X = 480;
    private static int output_Y = 480;
    private OriginalDialog originalDialog;
    private LoadingDialog loadingDialog;
    private Bitmap bitmap;
    /*是否已经读到了用户信息*/
    public boolean isReadUserInfo = false;

    @Override
    public View onCreateView(LayoutInflater inflater , ViewGroup viewGroup , Bundle savedInstanc){
        view = inflater.inflate(R.layout.fragment_main_me,null);
        EventBus.getDefault().register(this);
        if(view != null){
            initView();
        }
        return  view;

    }
    /*init view*/
    public void initView(){

        mRlRecord = (RelativeLayout) view.findViewById(R.id.personcenter_rl_record);
        mRlRecord.setOnClickListener(this);
        mRlNotice = (RelativeLayout) view.findViewById(R.id.personcenter_rl_tip);
        mRlNotice.setOnClickListener(this);
        mRlSetting = (RelativeLayout) view.findViewById(R.id.personcenter_rl_setup);
        mRlSetting.setOnClickListener(this);
        mTvName = (TextView) view.findViewById(R.id.personcenter_tv_username);
        mTvName.setOnClickListener(this);
        mImgProfile = (CircleImageView) view.findViewById(R.id.personcenter_img_person);
        mImgProfile.setOnClickListener(this);
        mRlAdmin = (RelativeLayout) view.findViewById(R.id.personcenter_rl_admin);
        mRlAdmin.setOnClickListener(this);
        mRlFamily = (RelativeLayout) view.findViewById(R.id.personcenter_rl_family);
        mRlFamily.setOnClickListener(this);
        mRlSuggestion = (RelativeLayout) view.findViewById(R.id.personcenter_rl_suggestion);
        mRlSuggestion.setOnClickListener(this);
        loadingDialog = new LoadingDialog();
        ((BaseApplication)getActivity().getApplication()).cacheThreadPool.execute(new Runnable() {
            @Override
            public void run() {
                String userInfo = SPHelper.getStringData(getActivity() ,"user_info");
                final MUserInfo mUserInfo = com.alibaba.fastjson.JSONObject.parseObject(userInfo , MUserInfo.class);
                if(mUserInfo!=null){
                    isReadUserInfo = true;
                    String userPhone = SPHelper.getStringData(getActivity(),"user_phone");
                    mTvName.setText(userPhone);
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Picasso.with(getActivity()).load(mUserInfo.getHead_picture()).error(R.drawable.person).into(mImgProfile);
                        }
                    });
                }else {
                    String userPhone = SPHelper.getStringData(getActivity(),"user_phone");
                    mTvName.setText(userPhone);
                    final String url = SPHelper.getStringData(getActivity() , "user_head_picture");
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if(!"".equalsIgnoreCase(url))
                            Picasso.with(getActivity()).load(url).error(R.drawable.person).into(mImgProfile);
                        }
                    });
                }
            }
        });

    }
    /*弹出对话框选择获取相片方式*/
    public void postDialog(){
        originalDialog = new OriginalDialog();
        originalDialog.show(getFragmentManager() , "getpicturedialog");
        originalDialog.setListener(this , this);
    }

    /*从相册选取*/
    public void chooseFromGallery(){
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(intent , CODE_GALLERY_REQUEST);
    }
    /*从相机选取*/
    public void chooseFromCamera(){
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if(OperateHelper.isHaveSDCard()){
            intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(new File(Environment.getExternalStorageDirectory() , TEMP_IMG_NAME)));
            startActivityForResult(intent , CODE_CAMERA_REQUEST);
        }else Toast.makeText(getActivity() , "当前没有存储设备可用" , Toast.LENGTH_LONG).show();
    }
    /*获取到图片后裁剪图片*/
    public void cropImg(Uri uri){
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri , "image/*");
        intent.putExtra("crop" , "true");
        // aspectX , aspectY :宽高的比例
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);

        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(new File(Environment.getExternalStorageDirectory() , TEMP_IMG_CROP)));
        // outputX , outputY : 裁剪图片宽高
        intent.putExtra("outputX", output_X);
        intent.putExtra("outputY", output_Y);
        intent.putExtra("return-data", false);  //设置为true，bitmap数据会从intent中返回，但是仅限于很小的图

        startActivityForResult(intent , CODE_RESULT_REQUEST);
    }
    /*上传头像*/
    public void upload(String pic){
        WlCallback callback = new WlCallback() {
            @Override
            public void onBefore(Request request, int id) {
                loadingDialog.show(getActivity().getSupportFragmentManager() , "loaddialog");
            }

            @Override
            public void onError(Call call, Exception e, int id) {
                loadingDialog.dismiss();
                bitmap.recycle();
                Toast.makeText(getActivity() , "上传失败!" ,Toast.LENGTH_LONG).show();
            }

            @Override
            public void onResponse(String response, int id) {
                loadingDialog.dismiss();
                if(response != null) {
                    if (bitmap != null)
                        mImgProfile.setImageBitmap(bitmap);
                    bitmap.recycle();
                }
            }
        };
        String token = SPHelper.getStringData(getActivity() , "user_token");
        if(!"".equalsIgnoreCase(token)) {
            JSONObject prm = new JSONObject();
            try {
                prm.put("token", token);
                prm.put("head_picture", pic);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            Boolean isConnected = OperateHelper.isNetworkConnected(getActivity());
            if(isConnected) {
                HttpHelper.request("api/user/modifyHead", prm.toString(), callback);
            }else Toast.makeText(getActivity(),"网络已断开，请检查网络连接!",Toast.LENGTH_SHORT).show();
        }
    }

    @Subscribe(threadMode = ThreadMode.BACKGROUND)
    public void update(MEventBusMsg msg){
        if(isReadUserInfo) return;
        if(Config.USER_INFO.equalsIgnoreCase(msg.getMsg())){
            String userInfo = SPHelper.getStringData(getActivity() ,"user_info");
            final MUserInfo mUserInfo = com.alibaba.fastjson.JSONObject.parseObject(userInfo , MUserInfo.class);
            if(mUserInfo!=null){
                isReadUserInfo = true;
                String userPhone = SPHelper.getStringData(getActivity(),"user_phone");
                mTvName.setText(userPhone);
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Picasso.with(getActivity()).load(mUserInfo.getHead_picture()).error(R.drawable.person).into(mImgProfile);
                    }
                });
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode , int responseCode , Intent intent){
        /*RESULT_CANCEL，因为在Fragment中，所以直接使用0*/
        if(responseCode == 0){
            Toast.makeText(getActivity().getApplication(), "已取消", Toast.LENGTH_LONG).show();
            return;
        }
        switch (requestCode){
            case CODE_GALLERY_REQUEST:
                Uri uri = intent.getData();
                String path = UriToPath.getPath(getActivity() , uri);
                File Img = new File(path);
                if(Img != null && Img.length()>0) {
                    FileInputStream inputStream;
                    try {
                        inputStream = new FileInputStream(Img);
                        bitmap = BitmapFactory.decodeStream(inputStream);
                                /*base64转码*/
                        mImgProfile.setImageBitmap(bitmap);
                        String bitmapString = OperateHelper.bitmpaToString(bitmap);
                        upload(bitmapString);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }
                }
                break;
            case CODE_CAMERA_REQUEST:
                ((BaseApplication)getActivity().getApplication()).cacheThreadPool.execute(new Runnable() {
                    @Override
                    public void run() {
                        File cropImg = new File(Environment.getExternalStorageDirectory(), TEMP_IMG_NAME);
                        if(cropImg != null && cropImg.length()>0) {
                            FileInputStream inputStream;
                            try {
                                inputStream = new FileInputStream(cropImg);
                                bitmap = BitmapFactory.decodeStream(inputStream);
                                /*base64转码*/
                                getActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        mImgProfile.setImageBitmap(bitmap);
                                    }
                                });
                                String bitmapString = OperateHelper.bitmpaToString(bitmap);
                                upload(bitmapString);
                            } catch (FileNotFoundException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                });
                break;
        }
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.personcenter_rl_record:
                startActivity(new Intent(getActivity() , ApplyRecordActivity.class));
                break;
            case R.id.personcenter_rl_tip:
                startActivity(new Intent(getActivity() , NoticeActivity.class));
                break;
            case R.id.personcenter_rl_setup:
                startActivity(new Intent(getActivity() , SettingActivity.class));
                break;
            case R.id.personcenter_tv_username:
                startActivity(new Intent(getActivity() , PersonDataActivity.class));
                break;
            case R.id.personcenter_img_person:
                postDialog();
                break;
            case R.id.original_tv_takephoto:
                /*拍照*/
                originalDialog.dismiss();
                chooseFromCamera();
                break;
            case R.id.original_tv_from_album:
                /*相册*/
                originalDialog.dismiss();
                chooseFromGallery();
                break;
            case R.id.personcenter_rl_admin:
                Intent intent = new Intent(getActivity() , PersonAdminActivity.class);
                intent.putExtra("persontype" , 0x002);
                startActivity(intent);
                break;
            case R.id.personcenter_rl_family:
                Intent intent2 = new Intent(getActivity() , PersonAdminActivity.class);
                intent2.putExtra("persontype" , 0x001);
                startActivity(intent2);
                break;
            case R.id.personcenter_rl_suggestion:
                startActivity(new Intent(getActivity() , SuggestionActivity.class));
                break;
        }
    }
}
