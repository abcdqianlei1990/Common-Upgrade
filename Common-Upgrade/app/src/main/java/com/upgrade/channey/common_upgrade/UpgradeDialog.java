package com.upgrade.channey.common_upgrade;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.daimajia.numberprogressbar.NumberProgressBar;
import com.daimajia.numberprogressbar.OnProgressBarListener;

/**
 * Created by channey on 2017/2/28.
 */

public class UpgradeDialog {
    private static final String TAG = "UpgradeDialog";
    private static Context context;
    private AlertDialog dialog;
    private String link;    //apk link
    private TextView content;
    private NumberProgressBar bar;
    private TextView negBtn;
    private TextView posBtn;
    private static String msg;
    private String authority;
    private static final int MAX_PROGRESS = 100;
    private int mDownLoadStatus = -1;
    private static final int DOWNLOAD_STATUS_START = 0;
    private static final int DOWNLOAD_STATUS_DOWNLOADING = 1;
    private static final int DOWNLOAD_STATUS_FAILURE = 2;
    private static final int DOWNLOAD_STATUS_END = 3;
    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case DOWNLOAD_STATUS_FAILURE:
                    Toast.makeText(context,"下载失败^_^!!",Toast.LENGTH_SHORT).show();
//                    posBtn.setText("重新下载");
                    break;
                case DOWNLOAD_STATUS_DOWNLOADING:
                    int progress = msg.arg1;
                    bar.setProgress(progress);
                    break;
                case DOWNLOAD_STATUS_END:
                    Toast.makeText(context,"下载完成",Toast.LENGTH_SHORT).show();
                    dialog.dismiss();
                    break;
            }
        }
    };

    public UpgradeDialog(@NonNull Context ctx, String str,String link,String authority) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            if (TextUtils.isEmpty(authority)){
                Log.e(TAG,"need file provider authority.");
                return;
            }
        }
        context = ctx;
        msg = str;
        this.link = link;
        this.authority = authority;
        show();
        initViews(context);
    }

    private void show(){
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        dialog = builder.create();
        dialog.show();
        dialog.setCanceledOnTouchOutside(false);
        View view = LayoutInflater.from(context).inflate(R.layout.pop_upgrade_dialog,null);
        dialog.getWindow().setContentView(view);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable());

        content = (TextView) view.findViewById(R.id.upgrade_dialog_content);
        bar = (NumberProgressBar) view.findViewById(R.id.upgrade_dialog_progressBar);
        negBtn = (TextView) view.findViewById(R.id.upgrade_dialog_neg_btn);
        posBtn = (TextView) view.findViewById(R.id.upgrade_dialog_pos_btn);

        content.setText(msg);
        negBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                bar.setVisibility(View.GONE);
            }
        });
        posBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bar.setVisibility(View.VISIBLE);
                if(mDownLoadStatus == DOWNLOAD_STATUS_START || mDownLoadStatus == DOWNLOAD_STATUS_DOWNLOADING){
                    Toast.makeText(context,"下载已开始",Toast.LENGTH_LONG).show();
                }else {
                    UpdateUtil.downLoadApk(context, new UpdateUtil.DownloadListener() {
                        @Override
                        public void startDownload() {
                            mDownLoadStatus = DOWNLOAD_STATUS_START;
                            Log.d("qian","startDownloading");
                        }

                        @Override
                        public void downloadSuccess() {
                            mDownLoadStatus = DOWNLOAD_STATUS_END;
                            Log.d("qian","downloadSuccess");
                            UpdateUtil.installApk(context,authority);
                        }

                        @Override
                        public void downloadFailure(Exception e) {
                            mDownLoadStatus = DOWNLOAD_STATUS_END;
                            Log.d("qian","downloadFailure"+e.getMessage());
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
                    },link);
                }
            }
        });
    }

    private void initViews(Context context){

        initProgressBar();
    }

    private void initProgressBar(){
        bar.setMax(MAX_PROGRESS);
        bar.setOnProgressBarListener(new OnProgressBarListener() {
            @Override
            public void onProgressChange(int current, int max) {
                if(current == max){
                    dialog.dismiss();
                    Toast.makeText(context,"下载完成,即将安装",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
