package com.cuz.travelnote;

import android.Manifest;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.cuz.travelnote.utils.PermissionUtils;
import com.nostra13.universalimageloader.cache.disc.impl.UnlimitedDiskCache;
import com.nostra13.universalimageloader.cache.memory.impl.WeakMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.core.download.BaseImageDownloader;
import com.nostra13.universalimageloader.utils.StorageUtils;

import java.io.File;
import java.util.Timer;
import java.util.TimerTask;

public class LoadingActivity extends AppCompatActivity {
    private final int MIN_SHOW_TIME = 2000;
    private Timer timer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_loading);


        String[] permissions = {Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE};
        String[] permissionsNeedToGrant = PermissionUtils.checkPermission(this,
                permissions);
        if (permissionsNeedToGrant != null) {
            PermissionUtils.grantPermission(this, permissionsNeedToGrant,
                    PermissionUtils.REQUEST_GRANT_READ_AND_WRITE_EXTERNAL_STORAGE_PERMISSIONS);
        } else {
            doingSomeThing();
        }
    }

    long init(){
        long start = System.currentTimeMillis();
        // 初始化
        initImageLoader();
        long end = System.currentTimeMillis();
        Log.e("init time", end - start + "mm");
        return end - start;
    }

    TimerTask task = new TimerTask(){
        public void run() {
            Intent intent = new Intent(LoadingActivity.this, TabMainActivity.class);
            startActivity(intent);
            timer.cancel();
            finish();
        }
    };

    private void initImageLoader(){
        DisplayImageOptions options = new DisplayImageOptions.Builder()
                .showImageOnLoading(R.drawable.ic_default) // 加载图片时的图片
                .showImageForEmptyUri(R.drawable.ic_default) // 没有图片资源时的默认图片
                .showImageOnFail(R.drawable.ic_default) // 加载失败时的图片
                .imageScaleType(ImageScaleType.EXACTLY)
                .cacheInMemory(false)
                .cacheOnDisk(true) // 启用外存缓存
                .bitmapConfig(Bitmap.Config.ARGB_8888).build(); //用 EXIF 和 JPEG 图像格式
        File caFile = StorageUtils.getOwnCacheDirectory(this, "UniversalImageLoader/Cache");
        Log.e("disk cache", caFile.toString());
        UnlimitedDiskCache cakCache = new UnlimitedDiskCache(caFile);
        ImageLoaderConfiguration imageLoaderConfiguration = new
                ImageLoaderConfiguration.Builder(
                this)
                .memoryCacheExtraOptions(960, 1600) //保存的每个缓存文件的最大长宽
                .threadPoolSize(3) // 线程池内加载的数量
                .threadPriority(Thread.NORM_PRIORITY - 2)
                .denyCacheImageMultipleSizesInMemory()
//拒绝缓存多个图片。解释：当同一个 Uri 获取不同大小的图片，缓存到内存时，只缓存一个。默认会缓存多个不同的大小的相同图片
                .discCacheSize(50 * 1024 * 1024)
                .tasksProcessingOrder(QueueProcessingType.LIFO) //设置图片下载和显示的工作 队列排序
                .discCacheFileCount(100) // 缓存的文件数量
                .discCache(cakCache) // 自定义缓存路径
                .defaultDisplayImageOptions(options) //显示图片的参数
                .memoryCache(new WeakMemoryCache())
                .imageDownloader(new BaseImageDownloader(this, 5 * 1000, 30 * 1000))
                .writeDebugLogs() // Remove for releaseapp
                .build();
        ImageLoader.getInstance().init(imageLoaderConfiguration);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode){
            case
                    PermissionUtils.REQUEST_GRANT_READ_AND_WRITE_EXTERNAL_STORAGE_PERMISSIONS:
                if(PermissionUtils.isGrantedAllPermissions(permissions,
                        grantResults)){
                    Toast.makeText(this, "你允许了全部授权",
                            Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this,
                            "你拒绝了部分权限，可能造成程序运行不正常",
                            Toast.LENGTH_SHORT).show();
                }
                doingSomeThing();
                break;
            default:
        }
    }
    void doingSomeThing(){
        long delay = MIN_SHOW_TIME - init();
        if(delay < 0)
            delay = 0;
        timer = new Timer(true);
        timer.schedule(task,delay);
    }

}
