package com.novel.collection.util;

import java.io.File;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import android.content.Context;
import android.os.StatFs;
import android.os.storage.StorageManager;


public class SDCardHelper {

	/**
	 * 获取设备扩展存储列表(含内置扩展存储),包含根路径、存储总量（单位）、可用存储（单位）、读写状态
	 * @param paramContext 调用上下文句柄
	 * @return List类型 HashMap存储列表,包含（SDPath,AllSize,AllSizeUnit,FreeSize,FreeSizeUnit,CanWrite,Status）
	 */
	@SuppressWarnings({ "unchecked", "deprecation", "rawtypes", "unused", "static-access" })
	public static List GetStorageList(Context paramContext) {
		List ResultList = new ArrayList();
		List TempList = new ArrayList();
		StorageManager localStorageManager = (StorageManager) paramContext.getSystemService(paramContext.STORAGE_SERVICE);
		try {
			List<String> localList = Arrays.asList((String[]) localStorageManager.getClass().getMethod("getVolumePaths", new Class[0]).invoke(localStorageManager));
			Iterator<String> localIterator = localList.iterator();
			while (localIterator.hasNext()) {
				String str1 = localIterator.next();
				if(str1.contains("Private")){
					continue;
				}
				try {
					StatFs localStatFs = new StatFs(str1);
					long l = localStatFs.getBlockSize();
					float f1 = (float) (l * localStatFs.getAvailableBlocks()) / 1024.0F / 1024.0F / 1024.0F;
					String str2 = "MB";
					float f2 = (float) (l * localStatFs.getFreeBlocks()) / 1024.0F / 1024.0F;
					if (f2 > 1.0F) {
						if (f2 > 1000.0F) {
							f2 /= 1024.0F;
							str2 = "GB";
						}
						HashMap localHashMap = new HashMap();
						localHashMap.put("SDPath", str1);
						localHashMap.put("AllSize",
								new StringBuilder(String.valueOf(f1))
										.toString());
						localHashMap.put("AllSizeUnit", "GB");
						localHashMap.put("FreeSize",
								new StringBuilder(String.valueOf(f2))
										.toString());
						localHashMap.put("FreeSizeUnit", str2);
						boolean bool = new File(str1).canWrite();
						localHashMap.put("CanWrite", Boolean.valueOf(bool));
						if (bool) {
							localHashMap.put("Status", "正常");
						} else {
							localHashMap.put("Status", "只读");
						}
						ResultList.add(localHashMap);

					}
				} catch (IllegalArgumentException localIllegalArgumentException1) {
				} catch (Exception localException2) {
				}
			}
		} catch (NoSuchMethodException localNoSuchMethodException) {
		} catch (IllegalArgumentException localIllegalArgumentException2) {
		} catch (IllegalAccessException localIllegalAccessException) {
		} catch (InvocationTargetException localInvocationTargetException) {
		} catch (Exception localException1) {
		}

		return ResultList;
	}

	/**
	 * 获取设备扩展存储根路径（外置存储卡）
	 * @return 外置存储卡路径字符串
	 */
	public static String getExternalSDRootPath() {
		HashSet<String> set = getExternalMounts();
		Iterator<String> iter = set.iterator();
		if (iter.hasNext()) {
			return iter.next();
		}
		return null;
	}


	private static HashSet<String> getExternalMounts() {
		final HashSet<String> out = new HashSet<String>();
		String reg = "(?i).*vold.*(vfat|ntfs|exfat|fat32|ext3|ext4).*rw.*";
		String s = "";
		try {
			final Process process = new ProcessBuilder().command("mount")
					.redirectErrorStream(true).start();
			process.waitFor();
			final InputStream is = process.getInputStream();
			final byte[] buffer = new byte[1024];
			while (is.read(buffer) != -1) {
				s = s + new String(buffer);
			}
			is.close();
		} catch (final Exception e) {
			e.printStackTrace();
		}

		// parse output
		final String[] lines = s.split("\n");
		for (String line : lines) {
			if (!line.toLowerCase(Locale.US).contains("asec")) {
				if (line.matches(reg)) {
					String[] parts = line.split(" ");
					for (String part : parts) {
						if (part.startsWith("/"))
							if (!part.toLowerCase(Locale.US).contains("vold"))
								out.add(part);
					}
				}
			}
		}
		return out;
	}

}
