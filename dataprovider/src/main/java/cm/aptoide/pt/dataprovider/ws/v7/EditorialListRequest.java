package cm.aptoide.pt.dataprovider.ws.v7;

import android.content.SharedPreferences;
import cm.aptoide.pt.dataprovider.BuildConfig;
import cm.aptoide.pt.dataprovider.interfaces.TokenInvalidator;
import cm.aptoide.pt.dataprovider.ws.BodyInterceptor;
import cm.aptoide.pt.preferences.toolbox.ToolboxManager;
import okhttp3.OkHttpClient;
import retrofit2.Converter;
import rx.Observable;

public class EditorialListRequest extends V7<EditorialListResponse, EditorialListRequest.Body> {

  private final int limit;

  protected EditorialListRequest(Body body, String baseHost, OkHttpClient httpClient,
      Converter.Factory converterFactory, BodyInterceptor bodyInterceptor,
      TokenInvalidator tokenInvalidator, int limit) {
    super(body, baseHost, httpClient, converterFactory, bodyInterceptor, tokenInvalidator);
    this.limit = limit;
  }

  public static String getHost(SharedPreferences sharedPreferences) {
    return (ToolboxManager.isToolboxEnableHttpScheme(sharedPreferences) ? "http"
        : BuildConfig.APTOIDE_WEB_SERVICES_SCHEME)
        + "://"
        + BuildConfig.APTOIDE_WEB_SERVICES_V7_HOST
        + "/api/7.20181019/";
  }

  public static EditorialListRequest of(BodyInterceptor<BaseBody> bodyInterceptor,
      OkHttpClient httpClient, Converter.Factory converterFactory,
      TokenInvalidator tokenInvalidator, SharedPreferences sharedPreferences, int limit,
      int offset) {
    Body body = new Body(limit, offset);
    return new EditorialListRequest(body, getHost(sharedPreferences), httpClient, converterFactory,
        bodyInterceptor, tokenInvalidator, limit);
  }

  @Override protected Observable<EditorialListResponse> loadDataFromNetwork(Interfaces interfaces,
      boolean bypassCache) {
    return interfaces.getEditorialList(limit, body);
  }

  public static class Body extends BaseBody implements Endless {
    private int offset;
    private int limit;

    public Body(int limit, int offset) {
      this.limit = limit;
      this.offset = offset;
    }

    @Override public int getOffset() {
      return offset;
    }

    @Override public void setOffset(int offset) {
      this.offset = offset;
    }

    @Override public Integer getLimit() {
      return limit;
    }
  }
}
