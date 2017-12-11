package com.upgrade.channey.common_upgrade;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.daimajia.numberprogressbar.NumberProgressBar;
import com.daimajia.numberprogressbar.OnProgressBarListener;

/**
 * Created by channey on 2017/2/28.
 */

public class UpgradeDialog {
    private static final String TAG = "UpgradeDialog";
    private static UpgradeDialog INSTANCE = null;
    private Activity activity;
    private AlertDialog dialog;
    private TextView content;
    private NumberProgressBar bar;
    private TextView negBtn;
    private TextView posBtn;
    private TextView forceBtn;
    private LinearLayout bottomBtnGroup;
    private FrameLayout progressBarGroup;
    private TextView noticeTv;
    private static final int MAX_PROGRESS = 100;
    private int mDownLoadStatus = -1;
    private static final int DOWNLOAD_STATUS_START = 0;
    private static final int DOWNLOAD_STATUS_DOWNLOADING = 1;
    private static final int DOWNLOAD_STATUS_FAILURE = 2;
    private static final int DOWNLOAD_STATUS_END = 3;
    private boolean forceUpdate = false;
    private boolean gotoMarket = false;
    private OnNegativeButtonClickListener onNegativeButtonClickListener;
    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case DOWNLOAD_STATUS_FAILURE:
                    Toast.makeText(activity,"下载失败^_^!!",Toast.LENGTH_SHORT).show();
//                    posBtn.setText("重新下载");
                    break;
                case DOWNLOAD_STATUS_DOWNLOADING:
                    int progress = msg.arg1;
                    bar.setProgress(progress);
                    break;
                case DOWNLOAD_STATUS_END:
                    Toast.makeText(activity,"下载完成",Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                    break;
            }
        }
    };

   public static UpgradeDialog getInstance(@NonNull Activity context){
       if(INSTANCE == null){
           synchronized (UpgradeDialog.class){
               if(INSTANCE == null){
                   INSTANCE = new UpgradeDialog(context);
               }
           }
       }
       return INSTANCE;
   }

    private UpgradeDialog(@NonNull Activity ctx){
        activity = ctx;
    }

    /**
     * show upgrade dialog
     *
     * @param msg message to show
     * @param apkLink 安装包链接
     * @param authority android sdk 7.0需要的file provider authority
     * notice:该方法请最后调用
     */
    public void show(String msg,final String apkLink,@Nullable final String authority){
        if(activity.isFinishing()){
            return;
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            if (TextUtils.isEmpty(authority)){
                Log.e(TAG,"need file provider authority.");
                return;
            }
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        dialog = builder.create();
        dialog.show();
        dialog.setCanceledOnTouchOutside(false);
        View view = LayoutInflater.from(activity).inflate(R.layout.pop_upgrade_dialog,null);
        dialog.getWindow().setContentView(view);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable());

        content = (TextView) view.findViewById(R.id.upgrade_dialog_content);
        bar = (NumberProgressBar) view.findViewById(R.id.upgrade_dialog_progressBar);
        negBtn = (TextView) view.findViewById(R.id.upgrade_dialog_neg_btn);
        posBtn = (TextView) view.findViewById(R.id.upgrade_dialog_pos_btn);
        noticeTv = (TextView) view.findViewById(R.id.upgrade_dialog_notice);
        forceBtn = (TextView) view.findViewById(R.id.upgrade_dialog_force_btn);
        bottomBtnGroup = (LinearLayout) view.findViewById(R.id.upgrade_dialog_button_group);
        progressBarGroup = (FrameLayout) view.findViewById(R.id.upgrade_dialog_progressBar_group);

        content.setText(msg);
        if(forceUpdate){
            noticeTv.setVisibility(View.VISIBLE);
            forceBtn.setVisibility(View.VISIBLE);
            bottomBtnGroup.setVisibility(View.GONE);
        }else {
            noticeTv.setVisibility(View.GONE);
            forceBtn.setVisibility(View.GONE);
            bottomBtnGroup.setVisibility(View.VISIBLE);
        }
        negBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onNegativeButtonClickListener.onClick();
                dialog.dismiss();
                bar.setVisibility(View.GONE);
            }
        });
        posBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                noticeTv.setVisibility(View.GONE);
                progressBarGroup.setVisibility(View.VISIBLE);
                if(mDownLoadStatus == DOWNLOAD_STATUS_START || mDownLoadStatus == DOWNLOAD_STATUS_DOWNLOADING){
                    Toast.makeText(activity,"下载已开始",Toast.LENGTH_LONG).show();
                }else {
                    if (gotoMarket){
                        try {
                            Uri uri = Uri.parse("market://details?id=" + activity.getPackageName());
                            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            activity.startActivity(intent);
                        } catch (Exception e) {
                            downloadFromServer(apkLink,authority);
                        }
                    }else {
                        downloadFromServer(apkLink,authority);
                    }
                }
            }
        });
        forceBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                noticeTv.setVisibility(View.GONE);
                progressBarGroup.setVisibility(View.VISIBLE);
                if(mDownLoadStatus == DOWNLOAD_STATUS_START || mDownLoadStatus == DOWNLOAD_STATUS_DOWNLOADING){
                    Toast.makeText(activity,"下载已开始",Toast.LENGTH_LONG).show();
                }else {
                    if (gotoMarket){
                        try {
                            Uri uri = Uri.parse("market://details?id=" + activity.getPackageName());
                            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            activity.startActivity(intent);
                        } catch (Exception e) {
                            downloadFromServer(apkLink,authority);
                        }
                    }else {
                        downloadFromServer(apkLink,authority);
                    }
                }
            }
        });
        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                if(forceUpdate){
                    activity.finish();
                    System.exit(0);
                }
            }
        });
        initProgressBar();
    }

    private void initProgressBar(){
        bar.setMax(MAX_PROGRESS);
        bar.setOnProgressBarListener(new OnProgressBarListener() {
            @Override
            public void onProgressChange(int current, int max) {
                if(current == max){
                    dialog.dismiss();
                    Toast.makeText(activity,"下载完成,即将安装",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    /**
     * 是否强制更新，强制更新时，用户不更新将无法进入app
     * 请谨慎使用
     * notice:该方法调用请放在{@link #show(String, String, String)}前面
     * @param force 是否强制更新
     */
    public UpgradeDialog forceUpdate(boolean force){
        forceUpdate = force;
        return this;
    }

    public UpgradeDialog setOnNegativeButtonClickListener(OnNegativeButtonClickListener listener){
        onNegativeButtonClickListener = listener;
        return this;
    }

    public interface OnNegativeButtonClickListener{
        void onClick();
    }

    /**
     * notice:该方法调用请放在{@link #show(String, String, String)}前面
     * @param gotoMarket 是否去应用市场下载新版
     */
    public UpgradeDialog gotoMarket(boolean gotoMarket){
        this.gotoMarket = gotoMarket;
        return this;
    }

    public void downloadFromServer(final String apkLink,@Nullable final String authority){
        bar.setVisibility(View.VISIBLE);
//                        Toast.makeText(activity,"未检测到应用市场,已通过浏览器下载",Toast.LENGTH_LONG).show();
        UpdateUtil.downLoadApk(activity, new UpdateUtil.DownloadListener() {
            @Override
            public void startDownload() {
                mDownLoadStatus = DOWNLOAD_STATUS_START;
                Log.d(TAG,"startDownloading");
            }

            @Override
            public void downloadSuccess() {
                mDownLoadStatus = DOWNLOAD_STATUS_END;
                Log.d(TAG,"downloadSuccess");
                UpdateUtil.installApk(activity,authority);
            }

            @Override
            public void downloadFailure(Exception e) {
                mDownLoadStatus = DOWNLOAD_STATUS_END;
                Log.d(TAG,"downloadFailure"+e.getMessage());
                mHandler.sendEmptyMessage(DOWNLOAD_STATUS_FAILURE);
            }

            @Override
            public void onDownload(long totalSize, long current) {
                mDownLoadStatus = DOWNLOAD_STATUS_DOWNLOADING;
                int i = (int) (current*100 /totalSize);
                Message msg = Message.obtain();
                if(i == 100){
                    mHandler.sendEmptyMessage(DOWNLOAD_STATUS_END);
                }else {
                    msg.what = DOWNLOAD_STATUS_DOWNLOADING;
                    msg.arg1 = i;
                    mHandler.sendMessage(msg);
                }
            }
        },apkLink);
    }
}
