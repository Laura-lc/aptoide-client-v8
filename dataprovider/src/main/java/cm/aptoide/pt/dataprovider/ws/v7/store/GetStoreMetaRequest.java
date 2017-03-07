/*
 * Copyright (c) 2016.
 * Modified by Neurophobic Animal on 24/05/2016.
 */

package cm.aptoide.pt.dataprovider.ws.v7.store;

import cm.aptoide.pt.dataprovider.ws.v7.BaseBodyWithStore;
import cm.aptoide.pt.dataprovider.ws.v7.BaseRequestWithStore;
import cm.aptoide.pt.dataprovider.ws.v7.BodyInterceptor;
import cm.aptoide.pt.dataprovider.ws.v7.V7Url;
import cm.aptoide.pt.model.v7.store.GetStoreMeta;
import lombok.Data;
import lombok.EqualsAndHashCode;
import okhttp3.OkHttpClient;
import retrofit2.Converter;
import rx.Observable;

/**
 * Created by neuro on 19-04-2016.
 */
@Data @EqualsAndHashCode(callSuper = true) public class GetStoreMetaRequest
    extends BaseRequestWithStore<GetStoreMeta, GetStoreMetaRequest.Body> {

  private String url;

  private GetStoreMetaRequest(String baseHost, Body body) {
    super(body, baseHost);
  }

  private GetStoreMetaRequest(String url, Body body, String baseHost) {
    super(body, baseHost);
    this.url = url;
  }

  private GetStoreMetaRequest(OkHttpClient httpClient, Converter.Factory converterFactory,
      String baseHost, Body body) {
    super(body, httpClient, converterFactory, baseHost);
  }

  private GetStoreMetaRequest(String url, Body body, OkHttpClient httpClient,
      Converter.Factory converterFactory, String baseHost) {
    super(body, httpClient, converterFactory, baseHost);
    this.url = url;
  }

  public static GetStoreMetaRequest ofAction(String url, StoreCredentials storeCredentials,
      BodyInterceptor bodyInterceptor) {

    return new GetStoreMetaRequest(new V7Url(url).remove("getStoreMeta").get(),
        (Body) bodyInterceptor.intercept(new Body(storeCredentials)), BASE_HOST);
  }

  public static GetStoreMetaRequest of(StoreCredentials storeCredentials, String accessToken,
      BodyInterceptor bodyInterceptor) {

    return new GetStoreMetaRequest(BASE_HOST,
        (Body) bodyInterceptor.intercept(new Body(storeCredentials)));
  }

  @Override protected Observable<GetStoreMeta> loadDataFromNetwork(Interfaces interfaces,
      boolean bypassCache) {
    return interfaces.getStoreMeta(url != null ? url : "", body, bypassCache);
  }

  @EqualsAndHashCode(callSuper = true) public static class Body extends BaseBodyWithStore {

    public Body(StoreCredentials storeCredentials) {
      super(storeCredentials);
    }
  }
}
