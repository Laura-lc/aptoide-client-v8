package cm.aptoide.pt.v8engine.repository;

import cm.aptoide.pt.database.accessors.DownloadAccessor;
import cm.aptoide.pt.database.realm.Download;
import java.util.List;
import rx.Observable;
import rx.schedulers.Schedulers;

public class DownloadRepository implements Repository<Download, String> {

  private final DownloadAccessor accessor;

  DownloadRepository(DownloadAccessor downloadAccessor) {
    this.accessor = downloadAccessor;
  }

  @Override public void save(Download entity) {
    accessor.save(entity);
  }

  @Override public Observable<Download> get(String md5) {
    return accessor.get(md5);
  }

  public Observable<Download> getAsList(String md5) {
    return accessor.getAsList(md5).observeOn(Schedulers.io()).map(downloads -> {
      if (downloads.isEmpty()) {
        return null;
      } else {
        return downloads.get(0);
      }
    });
  }

  public Observable<List<Download>> getAll() {
    return accessor.getAll();
  }
}
