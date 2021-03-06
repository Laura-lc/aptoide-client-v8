package cm.aptoide.pt.view.rx;

import android.content.Context;
import android.content.DialogInterface;
import android.view.ContextThemeWrapper;
import android.view.View;
import android.widget.TextView;
import androidx.annotation.StringRes;
import androidx.appcompat.app.AlertDialog;
import cm.aptoide.pt.R;
import cm.aptoide.pt.themes.ThemeManager;
import com.jakewharton.rxrelay.PublishRelay;
import rx.Completable;
import rx.Observable;
import rx.Single;

/**
 * Created by marcelobenites on 08/03/17.
 */

public class RxAlertDialog implements DialogInterface {

  private final AlertDialog dialog;
  private final DialogClick negativeClick;
  private final View view;
  private final DialogClick positiveClick;
  private final CancelEvent cancelEvent;
  private final DismissEvent dismissEvent;

  protected RxAlertDialog(AlertDialog dialog, View view, DialogClick positiveClick,
      DialogClick negativeClick, CancelEvent cancelEvent, DismissEvent dismissEvent) {
    this.dialog = dialog;
    this.view = view;
    this.positiveClick = positiveClick;
    this.negativeClick = negativeClick;
    this.cancelEvent = cancelEvent;
    this.dismissEvent = dismissEvent;
  }

  public View getDialogView() {
    return view;
  }

  public int getCheckedItem() {
    return dialog.getListView()
        .getCheckedItemPosition();
  }

  public void show() {
    dialog.show();
  }

  public boolean isShowing() {
    return dialog.isShowing();
  }

  @Override public void cancel() {
    dialog.cancel();
  }

  @Override public void dismiss() {
    dialog.dismiss();
  }

  public Observable<DialogInterface> positiveClicks() {
    if (positiveClick != null) {
      return positiveClick.clicks()
          .map(click -> this);
    }
    return Observable.empty();
  }

  public Observable<DialogInterface> negativeClicks() {
    if (negativeClick != null) {
      return negativeClick.clicks()
          .map(click -> this);
    }
    return Observable.empty();
  }

  public Observable<DialogInterface> cancels() {
    return cancelEvent.cancels()
        .map(click -> this);
  }

  public Observable<DialogInterface> dismisses() {
    return dismissEvent.dismisses()
        .map(click -> this);
  }

  public Single<Result> showWithResult() {
    return Completable.fromAction(dialog::show)
        .andThen(Observable.merge(cancelEvent.cancels()
            .map(__ -> Result.CANCEL), dismissEvent.dismisses()
            .map(__ -> Result.DISMISS), positiveClick.clicks()
            .map(__ -> Result.POSITIVE), negativeClick.clicks()
            .map(__ -> Result.NEGATIVE)))
        .first()
        .toSingle();
  }

  public enum Result {
    CANCEL, DISMISS, POSITIVE, NEGATIVE
  }

  public static class Builder {

    private final AlertDialog.Builder builder;

    private DialogClick positiveClick;
    private DialogClick negativeClick;
    private View view;

    public Builder(Context context, ThemeManager themeManager) {
      this.builder = new AlertDialog.Builder(new ContextThemeWrapper(context,
          themeManager.getAttributeForTheme(R.attr.dialogsTheme).resourceId));
    }

    public Builder setView(View view) {
      this.view = view;
      builder.setView(view);
      return this;
    }

    public Builder setTitle(@StringRes int titleId) {
      builder.setTitle(titleId);
      return this;
    }

    public Builder setTitleSmall(@StringRes int titleId) {
      TextView textView = new TextView(builder.getContext());
      textView.setTextSize(12f);
      textView.setTextColor(textView.getResources()
          .getColor(R.color.secondary_text_color));
      textView.setText(titleId);

      int paddingTop = (int) (20 * textView.getResources()
          .getDisplayMetrics().density + 0.5f);
      int paddingLeft = (int) (25 * textView.getResources()
          .getDisplayMetrics().density + 0.5f);
      int paddingBottom = (int) (10 * textView.getResources()
          .getDisplayMetrics().density + 0.5f);
      textView.setPaddingRelative(paddingLeft, paddingTop, 0, paddingBottom);
      builder.setCustomTitle(textView);
      return this;
    }

    public Builder setMessage(@StringRes int messageId) {
      builder.setMessage(messageId);
      return this;
    }

    public Builder setPositiveButton(@StringRes int textId) {
      positiveClick = new DialogClick(DialogInterface.BUTTON_POSITIVE, PublishRelay.create());
      builder.setPositiveButton(textId, positiveClick);
      return this;
    }

    public Builder setSingleChoiceItems(CharSequence[] items, int selectedItem) {
      builder.setSingleChoiceItems(items, selectedItem, null);
      return this;
    }

    public Builder setNegativeButton(@StringRes int textId) {
      negativeClick = new DialogClick(DialogInterface.BUTTON_NEGATIVE, PublishRelay.create());
      builder.setNegativeButton(textId, negativeClick);
      return this;
    }

    public RxAlertDialog build() {
      final AlertDialog dialog = builder.create();
      final CancelEvent cancelEvent = new CancelEvent(PublishRelay.create());
      final DismissEvent dismissEvent = new DismissEvent(PublishRelay.create());
      dialog.setOnCancelListener(cancelEvent);
      dialog.setOnDismissListener(dismissEvent);
      return new RxAlertDialog(dialog, view, positiveClick, negativeClick, cancelEvent,
          dismissEvent);
    }
  }

  protected static class DismissEvent implements DialogInterface.OnDismissListener {

    private final PublishRelay<Void> subject;

    public DismissEvent(PublishRelay<Void> subject) {
      this.subject = subject;
    }

    @Override public void onDismiss(DialogInterface dialog) {
      subject.call(null);
    }

    public Observable<Void> dismisses() {
      return subject;
    }
  }

  protected static class CancelEvent implements DialogInterface.OnCancelListener {

    private final PublishRelay<Void> subject;

    public CancelEvent(PublishRelay<Void> subject) {
      this.subject = subject;
    }

    @Override public void onCancel(DialogInterface dialog) {
      subject.call(null);
    }

    public Observable<Void> cancels() {
      return subject;
    }
  }

  protected static class DialogClick implements DialogInterface.OnClickListener {

    private final int which;
    private final PublishRelay<Void> subject;

    public DialogClick(int which, PublishRelay<Void> subject) {
      this.which = which;
      this.subject = subject;
    }

    @Override public void onClick(DialogInterface dialog, int which) {
      if (this.which == which) {
        subject.call(null);
      }
    }

    public Observable<Void> clicks() {
      return subject;
    }
  }
}
