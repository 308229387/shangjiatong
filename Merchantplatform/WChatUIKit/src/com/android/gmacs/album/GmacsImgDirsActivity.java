package com.android.gmacs.album;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;

import com.android.gmacs.R;
import com.android.gmacs.activity.BaseActivity;
import com.android.gmacs.album.GmacsImageDao.ImgDir;

import java.util.ArrayList;
import java.util.List;

/**
 * 照片目录Activity
 */
public class GmacsImgDirsActivity extends BaseActivity {

    private GridView gridView;
    private List<ImgDir> dataList = new ArrayList<>();
    private GridImgDirAdapter gridImageAdapter;

    @Override
    protected void initView() {
        setTitle(getText(R.string.local_album));
        mTitleBar.setBackListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myCancel();
            }
        });
        gridView = (GridView) findViewById(R.id.myGrid);
    }

    @Override
    protected void initData() {
        initListener();
        refreshData();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gmacs_activity_album_dir);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private void initListener() {
        gridImageAdapter = new GridImgDirAdapter(GmacsImgDirsActivity.this, dataList);
        gridView.setAdapter(gridImageAdapter);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String dirPath = dataList.get(position).dirPath;
                if (!TextUtils.isEmpty(dirPath)) {
                    Intent intent = new Intent();
                    intent.putExtra(AlbumConstant.FUNC, AlbumConstant.FUNC_UPDATE);
                    intent.putExtra(AlbumConstant.DIR_PATH, dirPath);
                    setResult(RESULT_OK, intent);
                    onBackPressed();
                    overridePendingTransition(R.anim.gmacs_push_left_in, R.anim.gmacs_push_left_out);
                }
            }
        });
    }

    private void refreshData() {
        new AsyncTask<String, Void, List<GmacsImageDao.ImgDir>>() {

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }

            @Override
            protected List<ImgDir> doInBackground(String... params) {
                return GmacsImageDao.instance(GmacsImgDirsActivity.this).loadAllImgDirs();
            }

            protected void onPostExecute(List<ImgDir> tmpList) {
                if (GmacsImgDirsActivity.this.isFinishing()) {
                    return;
                }
                dataList.clear();
                if (tmpList != null) {
                    dataList.addAll(tmpList);
                }
                gridImageAdapter.notifyDataSetChanged();
            }

        }.execute();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            myCancel();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    private void myCancel() {
        Intent intent = new Intent();
        intent.putExtra(AlbumConstant.FUNC, AlbumConstant.FUNC_CANCEL);
        setResult(RESULT_OK, intent);
        onBackPressed();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

}
