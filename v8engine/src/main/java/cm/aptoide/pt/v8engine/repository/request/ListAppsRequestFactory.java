package cm.aptoide.pt.v8engine.repository.request;

import cm.aptoide.pt.dataprovider.ws.v7.BodyInterceptor;
import cm.aptoide.pt.dataprovider.ws.v7.ListAppsRequest;
import cm.aptoide.pt.v8engine.interfaces.StoreCredentialsProvider;
import cm.aptoide.pt.v8engine.util.StoreCredentialsProviderImpl;

/**
 * Created by neuro on 03-01-2017.
 */
class ListAppsRequestFactory {

  private final StoreCredentialsProvider storeCredentialsProvider;
  private final BodyInterceptor bodyInterceptor;

  public ListAppsRequestFactory(BodyInterceptor bodyInterceptor,
      StoreCredentialsProvider storeCredentialsProvider) {
    this.storeCredentialsProvider = storeCredentialsProvider;
    this.bodyInterceptor = bodyInterceptor;
  }

  public ListAppsRequest newListAppsRequest(String url) {
    return ListAppsRequest.ofAction(url, storeCredentialsProvider.fromUrl(url), bodyInterceptor);
  }
}
