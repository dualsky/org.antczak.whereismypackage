package org.antczak.whereIsMyPackage.utils;

import android.content.Context;
import android.content.pm.PackageManager;

public class CheckPackage {

	public static boolean isInstalled(Context ctx, String uri) {
		PackageManager pm = ctx.getPackageManager();
		boolean app_installed = false;
		try {
			pm.getPackageInfo(uri, PackageManager.GET_ACTIVITIES);
			app_installed = true;
		} catch (PackageManager.NameNotFoundException e) {
			app_installed = false;
		}
		return app_installed;
	}
}
