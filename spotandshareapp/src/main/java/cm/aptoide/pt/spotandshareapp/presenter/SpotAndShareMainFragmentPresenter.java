package cm.aptoide.pt.spotandshareapp.presenter;

import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import cm.aptoide.pt.spotandshareapp.SpotAndSharePermissionProvider;
import cm.aptoide.pt.spotandshareapp.SpotAndShareUser;
import cm.aptoide.pt.spotandshareapp.SpotAndShareUserManager;
import cm.aptoide.pt.spotandshareapp.view.SpotAndShareMainFragmentView;
import cm.aptoide.pt.v8engine.presenter.Presenter;
import cm.aptoide.pt.v8engine.presenter.View;
import cm.aptoide.pt.v8engine.view.permission.PermissionProvider;
import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;

/**
 * Created by filipe on 08-06-2017.
 */

public class SpotAndShareMainFragmentPresenter implements Presenter {
  public static final int EXTERNAL_STORAGE_LOCATION_REQUEST_CODE_SEND = 0;
  public static final int EXTERNAL_STORAGE_LOCATION_REQUEST_CODE_RECEIVE = 1;
  public static final int WRITE_SETTINGS_REQUEST_CODE_SEND = 2;
  public static final int WRITE_SETTINGS_REQUEST_CODE_RECEIVE = 3;

  private SpotAndShareUserManager spotAndShareUserManager;
  private SpotAndSharePermissionProvider spotAndSharePermissionProvider;
  private SpotAndShareMainFragmentView view;

  public SpotAndShareMainFragmentPresenter(SpotAndShareMainFragmentView view,
      SpotAndShareUserManager spotAndShareUserManager,
      SpotAndSharePermissionProvider spotAndSharePermissionProvider) {
    this.view = view;
    this.spotAndShareUserManager = spotAndShareUserManager;
    this.spotAndSharePermissionProvider = spotAndSharePermissionProvider;
  }

  @Override public void present() {

    loadProfileInformationOnView();

    subscribe(clickedReceive());

    subscribe(clickedSend());

    subscribe(editProfile());

    handleLocationAndExternalStoragePermissionsResult();

    handleWriteSettingsPermissionResult();
  }

  @Override public void saveState(Bundle state) {

  }

  @Override public void restoreState(Bundle state) {

  }

  private Subscription subscribe(Observable<Void> voidObservable) {
    return view.getLifecycle()
        .filter(event -> event.equals(View.LifecycleEvent.RESUME))
        .flatMap(created -> voidObservable.compose(view.bindUntilEvent(View.LifecycleEvent.PAUSE)))
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(__ -> {
        }, err -> err.printStackTrace());
  }

  private Observable<Void> clickedSend() {
    return view.startSend()
        .observeOn(AndroidSchedulers.mainThread())
        .doOnNext(__ -> {
          if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            spotAndSharePermissionProvider.requestLocationAndExternalStorageSpotAndSharePermissions(
                EXTERNAL_STORAGE_LOCATION_REQUEST_CODE_SEND);
          } else {
            Log.i(getClass().getName(), "GOING TO START SENDING");
            view.openAppSelectionFragment(true);
          }
        });
  }

  private Observable<Void> clickedReceive() {
    return view.startReceive()
        .observeOn(AndroidSchedulers.mainThread())
        .doOnNext(selection -> {
          if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            spotAndSharePermissionProvider.requestLocationAndExternalStorageSpotAndSharePermissions(
                EXTERNAL_STORAGE_LOCATION_REQUEST_CODE_RECEIVE);
          } else {
            Log.i(getClass().getName(), "GOING TO START RECEIVING");
            view.openWaitingToReceiveFragment();
          }
        });
  }

  private Observable<Void> editProfile() {
    return view.editProfile()
        .observeOn(AndroidSchedulers.mainThread())
        .doOnNext(selection -> {
          view.openEditProfile();
        });
  }

  private SpotAndShareUser getSpotAndShareProfileInformation() {
    return spotAndShareUserManager.getUser();
  }

  private void loadProfileInformationOnView() {
    view.getLifecycle()
        .filter(lifecycleEvent -> lifecycleEvent.equals(View.LifecycleEvent.CREATE))
        .observeOn(AndroidSchedulers.mainThread())
        .doOnNext(__ -> view.loadProfileInformation(getSpotAndShareProfileInformation()))
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(created -> {
        }, error -> error.printStackTrace());
  }

  private void handleLocationAndExternalStoragePermissionsResult() {
    view.getLifecycle()
        .filter(lifecycleEvent -> lifecycleEvent.equals(View.LifecycleEvent.CREATE))
        .flatMap(__ -> Observable.merge(
            spotAndSharePermissionProvider.locationAndExternalStoragePermissionsResultSpotAndShare(
                EXTERNAL_STORAGE_LOCATION_REQUEST_CODE_SEND),
            spotAndSharePermissionProvider.locationAndExternalStoragePermissionsResultSpotAndShare(
                EXTERNAL_STORAGE_LOCATION_REQUEST_CODE_RECEIVE))
            .filter(permissions -> {
              for (PermissionProvider.Permission permission : permissions) {
                if (!permission.isGranted()) {
                  return false;
                }
              }
              return true;
            })
            .doOnNext(permissions -> {
              if (permissions.get(0)
                  .getRequestCode() == EXTERNAL_STORAGE_LOCATION_REQUEST_CODE_SEND) {
                spotAndSharePermissionProvider.requestWriteSettingsPermission(
                    WRITE_SETTINGS_REQUEST_CODE_SEND);
              } else if (permissions.get(0)
                  .getRequestCode() == EXTERNAL_STORAGE_LOCATION_REQUEST_CODE_RECEIVE) {
                spotAndSharePermissionProvider.requestWriteSettingsPermission(
                    WRITE_SETTINGS_REQUEST_CODE_RECEIVE);
              }
            }))
        .compose(view.bindUntilEvent(View.LifecycleEvent.DESTROY))
        .subscribe(created -> {
        }, error -> error.printStackTrace());
  }

  private void handleWriteSettingsPermissionResult() {
    view.getLifecycle()
        .filter(lifecycleEvent -> lifecycleEvent.equals(View.LifecycleEvent.CREATE))
        .flatMap(__ -> spotAndSharePermissionProvider.writeSettingsPermissionResult())
        .doOnNext(requestCode -> {

          if (requestCode == WRITE_SETTINGS_REQUEST_CODE_SEND) {
            Log.i(getClass().getName(), "GOING TO START SENDING");
            view.openAppSelectionFragment(true);
          } else if (requestCode == WRITE_SETTINGS_REQUEST_CODE_RECEIVE) {
            Log.i(getClass().getName(), "GOING TO START RECEIVING");
            view.openWaitingToReceiveFragment();
          }
        })
        .subscribe(created -> {
        }, error -> error.printStackTrace());
  }
}
