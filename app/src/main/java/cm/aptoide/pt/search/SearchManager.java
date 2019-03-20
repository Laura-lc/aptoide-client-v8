package cm.aptoide.pt.search;

import android.content.SharedPreferences;
import cm.aptoide.accountmanager.AptoideAccountManager;
import cm.aptoide.pt.abtesting.SearchExperiment;
import cm.aptoide.pt.ads.AdsRepository;
import cm.aptoide.pt.ads.MoPubAdsManager;
import cm.aptoide.pt.database.AccessorFactory;
import cm.aptoide.pt.database.accessors.Database;
import cm.aptoide.pt.database.realm.Store;
import cm.aptoide.pt.dataprovider.interfaces.TokenInvalidator;
import cm.aptoide.pt.dataprovider.model.v7.DataList;
import cm.aptoide.pt.dataprovider.model.v7.search.ListSearchApps;
import cm.aptoide.pt.dataprovider.model.v7.search.SearchApp;
import cm.aptoide.pt.dataprovider.util.HashMapNotNull;
import cm.aptoide.pt.dataprovider.ws.BodyInterceptor;
import cm.aptoide.pt.dataprovider.ws.v7.BaseBody;
import cm.aptoide.pt.dataprovider.ws.v7.ListSearchAppsRequest;
import cm.aptoide.pt.search.model.SearchAdResult;
import cm.aptoide.pt.search.model.SearchAppResult;
import cm.aptoide.pt.store.StoreUtils;
import java.util.List;
import okhttp3.OkHttpClient;
import retrofit2.Converter;
import rx.Observable;
import rx.Single;

@SuppressWarnings("Convert2MethodRef") public class SearchManager {

  private final SharedPreferences sharedPreferences;
  private final TokenInvalidator tokenInvalidator;
  private final BodyInterceptor<BaseBody> bodyInterceptor;
  private final OkHttpClient httpClient;
  private final Converter.Factory converterFactory;
  private final HashMapNotNull<String, List<String>> subscribedStoresAuthMap;
  private final AdsRepository adsRepository;
  private final Database database;
  private final AptoideAccountManager accountManager;
  private final MoPubAdsManager moPubAdsManager;
  private final SearchExperiment searchExperiment;
  private SearchExperiment.SearchExperimentResult cachedExperimentResult;

  public SearchManager(SharedPreferences sharedPreferences, TokenInvalidator tokenInvalidator,
      BodyInterceptor<BaseBody> bodyInterceptor, OkHttpClient httpClient,
      Converter.Factory converterFactory,
      HashMapNotNull<String, List<String>> subscribedStoresAuthMap, AdsRepository adsRepository,
      Database database, AptoideAccountManager accountManager, MoPubAdsManager moPubAdsManager,
      SearchExperiment searchExperiment) {
    this.sharedPreferences = sharedPreferences;
    this.tokenInvalidator = tokenInvalidator;
    this.bodyInterceptor = bodyInterceptor;
    this.httpClient = httpClient;
    this.converterFactory = converterFactory;
    this.subscribedStoresAuthMap = subscribedStoresAuthMap;
    this.adsRepository = adsRepository;
    this.database = database;
    this.accountManager = accountManager;
    this.moPubAdsManager = moPubAdsManager;
    this.searchExperiment = searchExperiment;
  }

  public Observable<SearchAdResult> getAdsForQuery(String query) {
    return adsRepository.getAdsFromSearch(query)
        .map(minimalAd -> new SearchAdResult(minimalAd));
  }

  public Single<List<SearchAppResult>> searchInNonFollowedStores(String query,
      boolean onlyTrustedApps, int offset) {
    return accountManager.enabled()
        .first()
        .flatMap(enabled -> searchExperiment.loadExperiment()
            .flatMap(experimentResult -> {
              cachedExperimentResult = experimentResult;
              return ListSearchAppsRequest.of(query, offset, false, onlyTrustedApps,
                  StoreUtils.getSubscribedStoresIds(
                      AccessorFactory.getAccessorFor(database, Store.class)), bodyInterceptor,
                  httpClient, converterFactory, tokenInvalidator, sharedPreferences, enabled,
                  experimentResult.getExperimentId(), experimentResult.getExperimentGroup())
                  .observe(true);
            }))
        .filter(listSearchApps -> hasResults(listSearchApps))
        .map(data -> data.getDataList()
            .getList())
        .flatMapIterable(list -> list)
        .map(searchApp -> new SearchAppResult(searchApp))
        .toList()
        .first()
        .toSingle();
  }

  public Single<List<SearchAppResult>> searchInFollowedStores(String query, boolean onlyTrustedApps,
      int offset) {
    return accountManager.enabled()
        .first()
        .flatMap(enabled -> searchExperiment.loadExperiment()
            .flatMap(
                experimentResult -> ListSearchAppsRequest.of(query, offset, true, onlyTrustedApps,
                    StoreUtils.getSubscribedStoresIds(
                        AccessorFactory.getAccessorFor(database, Store.class)), bodyInterceptor,
                    httpClient, converterFactory, tokenInvalidator, sharedPreferences, enabled,
                    experimentResult.getExperimentId(), experimentResult.getExperimentGroup())
                    .observe(true)))
        .filter(listSearchApps -> hasResults(listSearchApps))
        .map(data -> data.getDataList()
            .getList())
        .flatMapIterable(list -> list)
        .map(searchApp -> new SearchAppResult(searchApp))
        .toList()
        .first()
        .toSingle();
  }

  public Single<List<SearchAppResult>> searchInStore(String query, String storeName, int offset) {
    return ListSearchAppsRequest.of(query, storeName, offset, subscribedStoresAuthMap,
        bodyInterceptor, httpClient, converterFactory, tokenInvalidator, sharedPreferences)
        .observe(true)
        .filter(listSearchApps -> hasResults(listSearchApps))
        .map(data -> data.getDataList()
            .getList())
        .flatMapIterable(list -> list)
        .map(searchApp -> new SearchAppResult(searchApp))
        .toList()
        .first()
        .toSingle();
  }

  public Observable<SearchExperiment.SearchExperimentResult> getExperimentResult() {
    if (cachedExperimentResult != null) {
      return Observable.just(cachedExperimentResult);
    } else {
      return Observable.just(null);
    }
  }

  public Observable<Boolean> recordAbTestImpression() {
    return searchExperiment.recordImpression();
  }

  public Observable<Boolean> recordAbTestAction(int position) {
    return searchExperiment.recordAction(position);
  }

  private boolean hasResults(ListSearchApps listSearchApps) {
    DataList<SearchApp> dataList = listSearchApps.getDataList();
    return dataList != null
        && dataList.getList() != null
        && dataList.getList()
        .size() > 0;
  }

  public Single<Boolean> shouldLoadBannerAd() {
    return moPubAdsManager.shouldLoadBannerAd();
  }

  public Single<Boolean> shouldLoadNativeAds() {
    return moPubAdsManager.shouldLoadNativeAds();
  }
}
