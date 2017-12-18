package cm.aptoide.pt.social.commentslist;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import cm.aptoide.pt.AptoideApplication;
import cm.aptoide.pt.R;
import cm.aptoide.pt.analytics.ScreenTagHistory;
import cm.aptoide.pt.crashreports.CrashReport;
import cm.aptoide.pt.dataprovider.WebService;
import cm.aptoide.pt.dataprovider.interfaces.TokenInvalidator;
import cm.aptoide.pt.dataprovider.model.v7.Comment;
import cm.aptoide.pt.dataprovider.ws.BodyInterceptor;
import cm.aptoide.pt.dataprovider.ws.v7.BaseBody;
import cm.aptoide.pt.view.fragment.BaseToolbarFragment;
import com.jakewharton.rxbinding.support.v4.widget.RxSwipeRefreshLayout;
import com.jakewharton.rxbinding.support.v7.widget.RxRecyclerView;
import java.util.ArrayList;
import java.util.List;
import okhttp3.OkHttpClient;
import retrofit2.Converter;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.subjects.PublishSubject;

/**
 * Created by jdandrade on 28/09/2017.
 */

public class PostCommentsFragment extends BaseToolbarFragment implements PostCommentsView {
  public static final String POST_ID_KEY = "POST_ID_KEY";
  /**
   * The minimum number of items to have below your current scroll position before loading more.
   */
  private final int visibleThreshold = 5;
  private RecyclerView list;
  private PostCommentsAdapter adapter;
  private FloatingActionButton floatingActionButton;

  private BodyInterceptor<BaseBody> bodyInterceptor;
  private OkHttpClient httpClient;
  private Converter.Factory converterFactory;
  private TokenInvalidator tokenInvalidator;
  private SharedPreferences sharedPreferences;

  private PublishSubject<Long> replyEventPublishSubject;
  private SwipeRefreshLayout swipeRefreshLayout;
  private LinearLayoutManager layoutManager;
  private ProgressBar progressBar;
  private View genericError;

  public static Fragment newInstance(String postId) {
    Fragment fragment = new PostCommentsFragment();
    final Bundle args = new Bundle();
    args.putString(POST_ID_KEY, postId);
    fragment.setArguments(args);
    return fragment;
  }

  @Override public int getContentViewId() {
    return R.layout.post_comments_fragment;
  }

  @Override public void onCreate(@Nullable Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    bodyInterceptor =
        ((AptoideApplication) getContext().getApplicationContext()).getAccountSettingsBodyInterceptorPoolV7();
    httpClient = ((AptoideApplication) getContext().getApplicationContext()).getDefaultClient();
    converterFactory = WebService.getDefaultConverter();
    tokenInvalidator =
        ((AptoideApplication) getContext().getApplicationContext()).getTokenInvalidator();
    sharedPreferences =
        ((AptoideApplication) getContext().getApplicationContext()).getDefaultSharedPreferences();
    replyEventPublishSubject = PublishSubject.create();
    adapter =
        new PostCommentsAdapter(new ArrayList<>(), new ProgressComment(), replyEventPublishSubject);
  }

  @Override public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
    super.onViewCreated(view, savedInstanceState);
    list = (RecyclerView) view.findViewById(R.id.recycler_view);
    progressBar = (ProgressBar) view.findViewById(R.id.progress_bar);
    genericError = view.findViewById(R.id.generic_error);
    list.setAdapter(adapter);
    list.addItemDecoration(new ItemDividerDecoration(this));
    layoutManager = new LinearLayoutManager(getContext());
    list.setLayoutManager(layoutManager);
    swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_container);
    floatingActionButton = (FloatingActionButton) view.findViewById(R.id.fabAdd);
    setHasOptionsMenu(true);
    attachPresenter(new PostCommentsPresenter(this, new Comments(
        new PostCommentsRepository(10, 0, Integer.MAX_VALUE, bodyInterceptor, httpClient,
            converterFactory, tokenInvalidator, sharedPreferences)),
        new CommentsNavigator(getFragmentNavigator(), getActivity().getSupportFragmentManager()),
        AndroidSchedulers.mainThread(), CrashReport.getInstance(),
        getArguments().containsKey(POST_ID_KEY) ? getArguments().getString(POST_ID_KEY) : null));
  }

  @Nullable @Override
  public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
      @Nullable Bundle savedInstanceState) {
    return super.onCreateView(inflater, container, savedInstanceState);
  }

  @Override public Observable<Object> reachesBottom() {
    return RxRecyclerView.scrollEvents(list)
        .distinctUntilChanged()
        .filter(scroll -> isEndReached())
        .cast(Object.class);
  }

  @Override public Observable<Void> refreshes() {
    return RxSwipeRefreshLayout.refreshes(swipeRefreshLayout);
  }

  @Override public Observable<Long> replies() {
    return replyEventPublishSubject;
  }

  @Override public void showLoadMoreProgressIndicator() {
    adapter.addLoadMoreProgress();
  }

  @Override public void hideLoadMoreProgressIndicator() {
    adapter.removeLoadMoreProgress();
  }

  @Override public void showComments(List<Comment> comments) {
    adapter.updateComments(comments);
  }

  @Override public void hideRefresh() {
    swipeRefreshLayout.setRefreshing(false);
  }

  @Override public void showMoreComments(List<Comment> comments) {
    adapter.addComments(comments);
  }

  @Override public void showLoading() {
    list.setVisibility(View.GONE);
    genericError.setVisibility(View.GONE);
    progressBar.setVisibility(View.VISIBLE);
  }

  @Override public void hideLoading() {
    list.setVisibility(View.VISIBLE);
    genericError.setVisibility(View.GONE);
    progressBar.setVisibility(View.GONE);
  }

  @Override public void onDestroyView() {
    super.onDestroyView();
    list = null;
    adapter = null;
    progressBar = null;
    genericError = null;
    layoutManager = null;
    swipeRefreshLayout = null;
    floatingActionButton = null;
  }

  @Override protected boolean displayHomeUpAsEnabled() {
    return true;
  }

  @Override public void setupToolbarDetails(Toolbar toolbar) {
    toolbar.setTitle(R.string.comments_title_comments);
  }

  private boolean isEndReached() {
    return layoutManager.getItemCount() - layoutManager.findLastVisibleItemPosition()
        <= visibleThreshold;
  }

  @Override public ScreenTagHistory getHistoryTracker() {
    return ScreenTagHistory.Builder.build(this.getClass()
        .getSimpleName());
  }
}