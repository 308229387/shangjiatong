package com.android.gmacs.album;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.TextView;

import com.wchatuikit.R;
import com.android.gmacs.activity.BaseActivity;
import com.common.gmacs.utils.GLog;
import com.common.gmacs.utils.GmacsEnvi;
import com.common.gmacs.utils.ToastUtil;

import java.util.ArrayList;

/**
 * 相册Activity
 */
public class GmacsAlbumActivity extends BaseActivity {

    private GridView gridView;
    private ArrayList<String> dataList = new ArrayList<>();
    private ArrayList<String> selectedDataList = new ArrayList<>();

    private GmacsAlbumAdapter gridImageAdapter;
    private TextView mSelectAlbumBtn;
    private TextView mPreviewBtn;

    private String currentDir = null;
    private String currentDirName = null;
    private int mMaxCount;

    private boolean rawPicture;

    @Override
    protected void initView() {
        setTitle("");
        mTitleBar.setBackListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ToastUtil.cancelToast();
                onBackPressed();
            }
        });
        mTitleBar.mRightTextView.setVisibility(View.VISIBLE);
        mTitleBar.mRightTextView.setText(getText(R.string.send));
        mTitleBar.mRightTextView.setBackgroundResource(R.drawable.gmacs_blue_btn_stroke);
        mTitleBar.mRightTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selectedDataList.size() > 0) {
                    onOk(selectedDataList, false);
                } else {
                    ToastUtil.showToast(getText(R.string.select_picture_first));
                }
            }
        });
        gridView = (GridView) findViewById(R.id.myGrid);
        initBottomView();
    }

    @Override
    protected void initData() {
        currentDir = getIntent().getStringExtra(AlbumConstant.DIR_PATH);
        mMaxCount = getIntent().getIntExtra(AlbumConstant.EXTRA_PHOTO_MAX_COUNT, 10);
        gridImageAdapter = new GmacsAlbumAdapter(this, dataList, selectedDataList, mMaxCount);
        gridView.setAdapter(gridImageAdapter);
        gridImageAdapter.setOnItemClickListener(new GmacsAlbumAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int size) {
                notifyCountInfo(size);
            }
        });
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                startPhotoBrowseActivity(position, false);
            }
        });
        initBottomViewListener();
        updateData();
    }

    private void initBottomView() {
        mSelectAlbumBtn = (TextView) findViewById(R.id.select_album);
        mPreviewBtn = (TextView) findViewById(R.id.preview_button);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gmacs_activity_album_choose);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == AlbumConstant.ALBUM_REQUEST_CODE) {
                if (data != null) {
                    if (AlbumConstant.FUNC_UPDATE.equals(data.getStringExtra(AlbumConstant.FUNC))) {
                        currentDir = data.getStringExtra(AlbumConstant.DIR_PATH);
                        updateData();
                    }
                }
            } else if (requestCode == AlbumConstant.REQUEST_CODE_ALBUM_BROWSE) {
                if (data != null) {
                    ImageUrlArrayListWrapper wrapper = data.getParcelableExtra(AlbumConstant.KEY_SELECTED_IMG_DATA);
                    selectedDataList.clear();
                    if (AlbumConstant.FUNC_OK.equals(data.getStringExtra(AlbumConstant.FUNC))) {
                        onOk(wrapper.mList, data.getBooleanExtra(AlbumConstant.RAW, false));
                    } else if (AlbumConstant.FUNC_UPDATE.equals(data.getStringExtra(AlbumConstant.FUNC))) {
                        rawPicture = data.getBooleanExtra(AlbumConstant.RAW, false);
                        if (wrapper.mList != null) {
                            selectedDataList.addAll(wrapper.mList);
                        }
                        gridImageAdapter.notifyDataSetChanged();
                        int position = data.getIntExtra(AlbumConstant.KEY_IMG_POSITION, -1);
                        if (position != -1) {
                            try {
                                gridView.smoothScrollToPosition(position);
                            } catch (Exception ignored) {
                            }
                        }
                        notifyCountInfo(selectedDataList.size());
                    }
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    protected void updateData() {
        if (!TextUtils.isEmpty(currentDir)) {
            try {
                currentDirName = currentDir.substring(currentDir.lastIndexOf("/") + 1, currentDir.length());
            } catch (Exception e) {
                GLog.d(TAG, e.getMessage());
            }
        }
        dataList.clear();
        selectedDataList.clear();
        notifyCountInfo(0);
        refreshData(currentDir);
    }

    private void initBottomViewListener() {
        mSelectAlbumBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO: 相册列表
                gotoGmacsImgDirsActivity();
//                PopupWindow popupWindow = new PopupWindow(GmacsAlbumActivity.this);
//                ListView listView = (ListView) LayoutInflater.from(GmacsAlbumActivity.this).inflate(R.layout.gmacs_activity_album_dir, null);
//                popupWindow.setContentView(listView);
//                popupWindow.setFocusable(true);
//                popupWindow.setBackgroundDrawable(new ColorDrawable(0));
//                popupWindow.setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
//                popupWindow.setHeight(ViewGroup.LayoutParams.MATCH_PARENT);
//                popupWindow.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.transparent)));
//                popupWindow.setOutsideTouchable(true);
//                popupWindow.setAnimationStyle(R.style.popupwindow_anim);
//                popupWindow.showAtLocation(gridView, Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);
            }
        });
        mPreviewBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selectedDataList.size() > 0) {
                    startPhotoBrowseActivity(0, true);
                } else {
                    ToastUtil.showToast(getText(R.string.select_picture_first));
                }
            }
        });
    }

    private void startPhotoBrowseActivity(int position, boolean isPreview) {
        Intent intent = new Intent(GmacsAlbumActivity.this, GmacsPhotoBrowseActivity.class);
        intent.putExtra(AlbumConstant.KEY_SELECTED_IMG_DATA, new ImageUrlArrayListWrapper(selectedDataList));
        intent.putExtra(AlbumConstant.IS_PREVIEW, isPreview);
        intent.putExtra(AlbumConstant.KEY_IMG_POSITION, position);
        intent.putExtra(AlbumConstant.DIR_PATH, currentDir);
        intent.putExtra(AlbumConstant.RAW, rawPicture);
        intent.putExtra(AlbumConstant.EXTRA_PHOTO_MAX_COUNT, mMaxCount);
        startActivityForResult(intent, AlbumConstant.REQUEST_CODE_ALBUM_BROWSE);
    }

    private void notifyCountInfo(int count) {
        if (count > 0) {
            mTitleBar.mRightTextView.setText(String.format(getString(R.string.send_count), count, mMaxCount));
            mPreviewBtn.setText(String.format(getString(R.string.preview_count), count));
            mPreviewBtn.setEnabled(true);
        } else {
            mTitleBar.mRightTextView.setText(R.string.send);
            mPreviewBtn.setText(R.string.preview);
            mPreviewBtn.setEnabled(false);
        }
    }

    private void refreshData(String dirPath) {
        new AsyncTask<String, Void, GmacsImageDao.ImgDir>() {

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }

            @Override
            protected GmacsImageDao.ImgDir doInBackground(String... params) {
                GmacsImageDao.destroy();
                if (TextUtils.isEmpty(params[0])) {
                    GmacsImageDao.instance(GmacsEnvi.appContext).loadAllImgDirs();
                    return GmacsImageDao.instance(GmacsEnvi.appContext).getDefaultDir();
                } else {
                    return GmacsImageDao.instance(GmacsEnvi.appContext).getImgListByDir(params[0]);
                }
            }

            @Override
            protected void onPostExecute(GmacsImageDao.ImgDir imgDir) {
                if (GmacsAlbumActivity.this.isFinishing()) {
                    return;
                }
                if (imgDir != null) {
                    dataList.clear();
                    dataList.addAll(imgDir.dataList);
                    currentDirName = imgDir.dirName;
                    currentDir = imgDir.dirPath;
                } else {
                    dataList.clear();
                    currentDir = null;
                    currentDirName = null;
                    ToastUtil.showToast(getText(R.string.no_available_picture));
                }
                setTitle(getText(R.string.local_album));
                gridImageAdapter.notifyDataSetChanged();

                mSelectAlbumBtn.setText(currentDirName);
            }

        }.execute(dirPath);
    }

    private void gotoGmacsImgDirsActivity() {
        ToastUtil.cancelToast();
        Intent intent = new Intent(GmacsAlbumActivity.this, GmacsImgDirsActivity.class);
        startActivityForResult(intent, AlbumConstant.ALBUM_REQUEST_CODE);
        overridePendingTransition(R.anim.gmacs_push_left_in, R.anim.gmacs_push_left_out);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private void onOk(ArrayList<String> selectedDataList, boolean rawPicture) {
        ToastUtil.cancelToast();
        Intent intent = new Intent();
        intent.putExtra(AlbumConstant.KEY_SELECTED_IMG_DATA, new ImageUrlArrayListWrapper(selectedDataList));
        intent.putExtra(AlbumConstant.RAW, rawPicture);
        setResult(RESULT_OK, intent);
        finish();
    }

}
