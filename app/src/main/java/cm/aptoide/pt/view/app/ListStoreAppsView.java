package cm.aptoide.pt.view.app;

import cm.aptoide.pt.presenter.View;
import java.util.List;
import rx.Observable;
import rx.subjects.PublishSubject;

/**
 * Created by trinkes on 17/10/2017.
 */

public interface ListStoreAppsView extends View {

  void addApps(List<Application> appsList);

  Observable<Application> getAppClick();

  Observable<Object> reachesBottom();

  void hideLoading();

  void showLoading();

  void hideStartingLoading();

  PublishSubject<Void> getRefreshEvent();

  void hideRefreshLoading();

  void setApps(List<Application> applications);
}
