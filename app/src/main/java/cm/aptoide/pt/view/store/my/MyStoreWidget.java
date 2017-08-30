package cm.aptoide.pt.view.store.my;

import android.content.Context;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.ColorInt;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import cm.aptoide.pt.AptoideApplication;
import cm.aptoide.pt.R;
import cm.aptoide.pt.analytics.Analytics;
import cm.aptoide.pt.dataprovider.model.v7.store.Store;
import cm.aptoide.pt.networking.image.ImageLoader;
import cm.aptoide.pt.store.StoreAnalytics;
import cm.aptoide.pt.store.StoreTheme;
import cm.aptoide.pt.view.store.MetaStoresBaseWidget;
import com.facebook.appevents.AppEventsLogger;
import com.jakewharton.rxbinding.view.RxView;

/**
 * Created by trinkes on 05/12/2016.
 */

public class MyStoreWidget extends MetaStoresBaseWidget<MyStoreDisplayable> {

  private LinearLayout widgetLayout;
  private LinearLayout socialChannelsLayout;
  private ImageView storeIcon;
  private TextView storeName;
  private Button exploreButton;
  private TextView suggestionMessage;
  private TextView createStoreText;
  private StoreAnalytics storeAnalytics;

  public MyStoreWidget(View itemView) {
    super(itemView);
    storeAnalytics =
        new StoreAnalytics(AppEventsLogger.newLogger(getContext()), Analytics.getInstance());
  }

  @Override protected void assignViews(View itemView) {
    widgetLayout = (LinearLayout) itemView.findViewById(R.id.widgetLayout);
    socialChannelsLayout = (LinearLayout) itemView.findViewById(R.id.social_channels);
    storeIcon = (ImageView) itemView.findViewById(R.id.store_icon);
    storeName = (TextView) itemView.findViewById(R.id.store_name);
    suggestionMessage = (TextView) itemView.findViewById(R.id.create_store_text);
    createStoreText = (TextView) itemView.findViewById(R.id.created_store_text);
    exploreButton = (Button) itemView.findViewById(R.id.explore_button);
  }

  @Override public void bindView(MyStoreDisplayable displayable) {

    final FragmentActivity context = getContext();
    Store store = displayable.getMeta()
        .getData()
        .getStore();
    suggestionMessage.setText(displayable.getSuggestionMessage(context));
    createStoreText.setText(displayable.getCreateStoreText());
    createStoreText.setVisibility(displayable.getCreateStoreTextViewVisibility());
    exploreButton.setText(displayable.getExploreButtonText());
    String storeTheme = store.getAppearance()
        .getTheme();
    @ColorInt int color = getColorOrDefault(StoreTheme.get(storeTheme), context);
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
      Drawable d = context.getDrawable(R.drawable.dialog_bg_2);
      d.setColorFilter(color, PorterDuff.Mode.SRC_IN);
      widgetLayout.setBackground(d);
    } else {
      Drawable d = context.getResources()
          .getDrawable(R.drawable.dialog_bg_2);
      d.setColorFilter(color, PorterDuff.Mode.SRC_IN);
      widgetLayout.setBackgroundDrawable(d);
    }
    exploreButton.setTextColor(color);

    ImageLoader.with(context)
        .loadWithShadowCircleTransform(store.getAvatar(), storeIcon);

    storeName.setText(store.getName());
    compositeSubscription.add(RxView.clicks(exploreButton)
        .subscribe(click -> {
          getFragmentNavigator().navigateTo(AptoideApplication.getFragmentProvider()
              .newStoreFragment(store.getName(), storeTheme));
          storeAnalytics.sendStoreTabInteractEvent("View Store");
          storeAnalytics.sendStoreOpenEvent("View Own Store", store.getName());
        }));
    setupSocialLinks(displayable.getSocialChannels(), socialChannelsLayout);
  }

  private int getColorOrDefault(StoreTheme theme, Context context) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
      return context.getResources()
          .getColor(theme.getPrimaryColor(), context.getTheme());
    } else {
      return context.getResources()
          .getColor(theme.getPrimaryColor());
    }
  }
}
