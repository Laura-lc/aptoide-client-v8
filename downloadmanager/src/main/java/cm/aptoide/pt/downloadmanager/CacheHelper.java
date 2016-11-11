/*
 * Copyright (c) 2016.
 * Modified by SithEngineer on 02/09/2016.
 */

package cm.aptoide.pt.downloadmanager;

import cm.aptoide.pt.downloadmanager.interfaces.CacheManager;
import cm.aptoide.pt.downloadmanager.interfaces.DownloadSettingsInterface;
import cm.aptoide.pt.logger.Logger;
import cm.aptoide.pt.utils.AptoideUtils;
import cm.aptoide.pt.utils.FileUtils;
import java.io.File;
import java.util.List;
import lombok.Data;
import rx.Observable;
import rx.schedulers.Schedulers;

/**
 * Created by trinkes on 7/7/16.
 */
public class CacheHelper implements CacheManager {
  private static final int VALUE_TO_CONVERT_MB_TO_BYTES = 1024 * 1024;
  public static String TAG = CacheHelper.class.getSimpleName();
  private final List<FolderToManage> foldersToCleanPath;
  private DownloadSettingsInterface dirSettings;

  public CacheHelper(DownloadSettingsInterface dirSettings,
      List<FolderToManage> foldersToCleanPath) {
    this.dirSettings = dirSettings;
    this.foldersToCleanPath = foldersToCleanPath;
  }

  public Observable<Long> cleanCache() {
    long now = System.currentTimeMillis();
    return Observable.just(foldersToCleanPath)
        .filter(folderToManages -> shouldClean(folderToManages,
            dirSettings.getMaxCacheSize() * VALUE_TO_CONVERT_MB_TO_BYTES))
        .flatMapIterable(folders -> folders)
        .filter(folder -> folder.getFolder().exists())
        .map(folder -> removeOldFiles(folder.getFolder(), folder.getCacheTime(), now))
        .toList()
        .map(sizes -> {
          long size = 0;
          for (int i = 0; i < sizes.size(); i++) {
            size += sizes.get(i);
          }
          Logger.d(TAG, "Cache cleaned: " + AptoideUtils.StringU.formatBytes(size));
          return size;
        })
        .subscribeOn(Schedulers.io());
  }

  private boolean shouldClean(List<FolderToManage> foldersToCleanPath, long maxCacheSize) {
    long cacheSize = 0;
    for (int i = 0; i < foldersToCleanPath.size(); i++) {
      cacheSize += FileUtils.dirSize(this.foldersToCleanPath.get(i).getFolder());
    }
    return cacheSize > maxCacheSize;
  }

  private long removeOldFiles(File folder, long timeToCache, long now) {
    File[] list = folder.listFiles();
    long deletedSize = 0;
    if (list == null) {
      return deletedSize;
    }
    //iterate all files inside the folder
    for (File file : list) {
      //check if it's going to ben handled
      if (!checkIfInFoldersToClean(foldersToCleanPath, file)) {
        //if it's a directory, check inside of it
        if (file.isDirectory()) {
          deletedSize += removeFilesFromDir(timeToCache, now, deletedSize, file);
        } else {
          deletedSize += removeFile(timeToCache, now, deletedSize, file);
        }
      }
    }
    return deletedSize;
  }

  private long removeFilesFromDir(long timeToCache, long now, long deletedSize, File aList) {
    long dirSize = removeOldFiles(aList, timeToCache, now);
    //check if directory is empty, delete it if it's and update the deleted size
    File[] dirFiles = aList.listFiles();
    if ((dirFiles == null || dirFiles.length <= 0) && aList.delete()) {
      deletedSize += dirSize;
    }
    return deletedSize;
  }

  /**
   * check if the ttl of the file was reached and delete it if it was
   */
  private long removeFile(long timeToCache, long now, long deletedSize, File file) {
    if ((now - file.lastModified()) > timeToCache) {
      long fileSize = file.length();
      Logger.d(TAG, "removeFile: " + file.getAbsolutePath());
      //update deleted size if file was deleted
      if (file.delete()) {
        deletedSize += fileSize;
      }
    }
    return deletedSize;
  }

  /**
   * This method checks if the the folder is contained in the folders to handle
   *
   * @param foldersToCleanPath folders to handle
   * @param folder folder to check
   * @return true if folder is in foldersToCleanPath
   */
  private boolean checkIfInFoldersToClean(List<FolderToManage> foldersToCleanPath, File folder) {
    for (FolderToManage folderToManage : foldersToCleanPath) {
      if (folderToManage.getFolder().getAbsolutePath().equals(folder.getAbsolutePath())) {
        return true;
      }
    }
    return false;
  }

  @Data public static class FolderToManage {
    /**
     * Folder to be managed
     */
    final File folder;
    /**
     * how long the files should be kept
     */
    final long cacheTime;
  }
}
