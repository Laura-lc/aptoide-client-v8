package cm.aptoide.pt.dataprovider.ws.v7.billing;

import android.content.SharedPreferences;
import cm.aptoide.pt.dataprovider.BuildConfig;
import cm.aptoide.pt.dataprovider.interfaces.TokenInvalidator;
import cm.aptoide.pt.dataprovider.model.v7.BaseV7Response;
import cm.aptoide.pt.dataprovider.ws.BodyInterceptor;
import cm.aptoide.pt.dataprovider.ws.RefreshBody;
import cm.aptoide.pt.dataprovider.ws.v7.BaseBody;
import cm.aptoide.pt.dataprovider.ws.v7.V7;
import cm.aptoide.pt.preferences.toolbox.ToolboxManager;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import okhttp3.OkHttpClient;
import retrofit2.Converter;
import retrofit2.Response;
import rx.Observable;

public class GetAuthorizationsRequest
    extends V7<Response<GetAuthorizationsRequest.ResponseBody>, RefreshBody> {

  private final String accessToken;
  private final String customerId;

  public GetAuthorizationsRequest(RefreshBody body, String baseHost, OkHttpClient httpClient,
      Converter.Factory converterFactory, BodyInterceptor bodyInterceptor,
      TokenInvalidator tokenInvalidator, String accessToken, String customerId) {
    super(body, baseHost, httpClient, converterFactory, bodyInterceptor, tokenInvalidator);
    this.accessToken = accessToken;
    this.customerId = customerId;
  }

  public static String getHost(SharedPreferences sharedPreferences) {
    return (ToolboxManager.isToolboxEnableHttpScheme(sharedPreferences) ? "http"
        : BuildConfig.APTOIDE_WEB_SERVICES_SCHEME)
        + "://"
        + BuildConfig.APTOIDE_WEB_SERVICES_WRITE_V7_HOST
        + "/api/7/";
  }

  public static GetAuthorizationsRequest of(SharedPreferences sharedPreferences,
      OkHttpClient httpClient, Converter.Factory converterFactory,
      BodyInterceptor<BaseBody> bodyInterceptor, TokenInvalidator tokenInvalidator,
      String accessToken, String customerId) {
    return new GetAuthorizationsRequest(null, getHost(sharedPreferences), httpClient,
        converterFactory, bodyInterceptor, tokenInvalidator, accessToken, customerId);
  }

  @Override
  protected Observable<Response<GetAuthorizationsRequest.ResponseBody>> loadDataFromNetwork(
      Interfaces interfaces, boolean bypassCache) {
    return interfaces.getBillingAuthorizations("Bearer " + accessToken, customerId);
  }

  public static class ResponseBody extends BaseV7Response {

    @JsonProperty("datalist") private Data data;

    public Data getData() {
      return data;
    }

    public void setData(Data data) {
      this.data = data;
    }

    public static class Data {

      private List<Authorization> list;

      public List<Authorization> getList() {
        return list;
      }

      public void setList(List<Authorization> list) {
        this.list = list;
      }
    }

    public static class Authorization {

      private long id;
      private String type;
      @JsonProperty("service_id") private long serviceId;
      private Price price;
      private User user;
      private String status;
      private Metadata data;
      private String icon;
      private String name;
      private String description;
      @JsonProperty("is_default") private boolean defaultAuthorization;

      public long getId() {
        return id;
      }

      public void setId(long id) {
        this.id = id;
      }

      public String getType() {
        return type;
      }

      public void setType(String type) {
        this.type = type;
      }

      public long getServiceId() {
        return serviceId;
      }

      public void setServiceId(long serviceId) {
        this.serviceId = serviceId;
      }

      public Price getPrice() {
        return price;
      }

      public void setPrice(Price price) {
        this.price = price;
      }

      public User getUser() {
        return user;
      }

      public void setUser(User user) {
        this.user = user;
      }

      public String getStatus() {
        return status;
      }

      public void setStatus(String status) {
        this.status = status;
      }

      public Metadata getData() {
        return data;
      }

      public void setData(Metadata data) {
        this.data = data;
      }

      public String getIcon() {
        return icon;
      }

      public void setIcon(String icon) {
        this.icon = icon;
      }

      public void setName(String name) {
        this.name = name;
      }

      public String getName() {
        return name;
      }

      public String getDescription() {
        return description;
      }

      public void setDescription(String description) {
        this.description = description;
      }

      public boolean isDefaultAuthorization() {
        return defaultAuthorization;
      }

      public void setDefaultAuthorization(boolean defaultAuthorization) {
        this.defaultAuthorization = defaultAuthorization;
      }

      public static class User {

        private long id;

        public long getId() {
          return id;
        }

        public void setId(long id) {
          this.id = id;
        }
      }

      public static class Metadata {

        private String description;
        private String session;

        public String getDescription() {
          return description;
        }

        public void setDescription(String description) {
          this.description = description;
        }

        public String getSession() {
          return session;
        }

        public void setSession(String session) {
          this.session = session;
        }
      }

      public static class Price {

        private double amount;
        private String currency;
        private String currencySymbol;

        public double getAmount() {
          return amount;
        }

        public void setAmount(double amount) {
          this.amount = amount;
        }

        public String getCurrency() {
          return currency;
        }

        public void setCurrency(String currency) {
          this.currency = currency;
        }

        public String getCurrencySymbol() {
          return currencySymbol;
        }

        public void setCurrencySymbol(String currencySymbol) {
          this.currencySymbol = currencySymbol;
        }
      }
    }
  }
}