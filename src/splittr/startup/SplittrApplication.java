package splittr.startup;

import java.io.File;

import com.nostra13.universalimageloader.cache.disc.impl.UnlimitedDiscCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.utils.StorageUtils;

import android.app.Application;
import android.os.Bundle;

public class SplittrApplication extends Application {

	@Override
	public void onCreate()
	{
		super.onCreate();
		
        DisplayImageOptions loaderDefaults = new DisplayImageOptions.Builder()
	        .cacheInMemory(true)
	        .cacheOnDisc(true)
	        .resetViewBeforeLoading(true)
	        .build();
		ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(this)
				.defaultDisplayImageOptions(loaderDefaults)
		        .discCacheFileCount(100)
		        .writeDebugLogs()
		        .build();
		ImageLoader.getInstance().init(config);
	}
}
