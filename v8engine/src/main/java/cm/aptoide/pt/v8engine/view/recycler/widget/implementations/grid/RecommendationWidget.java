/*
 * Copyright (c) 2016.
 * Modified by Marcelo Benites on 08/07/2016.
 */

package cm.aptoide.pt.v8engine.view.recycler.widget.implementations.grid;

import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import cm.aptoide.pt.imageloader.ImageLoader;
import cm.aptoide.pt.v8engine.R;
import cm.aptoide.pt.v8engine.fragment.implementations.AppViewFragment;
import cm.aptoide.pt.v8engine.interfaces.FragmentShower;
import cm.aptoide.pt.v8engine.view.recycler.displayable.Displayable;
import cm.aptoide.pt.v8engine.view.recycler.displayable.implementations.RecommendationDisplayable;
import cm.aptoide.pt.v8engine.view.recycler.widget.Widget;

/**
 * Created by marcelobenites on 7/8/16.
 */
public class RecommendationWidget extends Widget<RecommendationDisplayable> {

	private TextView title;
	private TextView subtitle;
	private ImageView image;
	private ImageView appIcon;
	private TextView appName;
	private TextView similarApps;
	private Button getAppButton;

	public RecommendationWidget(View itemView) {
		super(itemView);
	}

	@Override
	protected void assignViews(View itemView) {
		title = (TextView)itemView.findViewById(R.id.displayable_social_timeline_recommendation_card_title);
		subtitle = (TextView)itemView.findViewById(R.id.displayable_social_timeline_recommendation_card_subtitle);
		image = (ImageView) itemView.findViewById(R.id.displayable_social_timeline_recommendation_card_icon);
		appIcon = (ImageView) itemView.findViewById(R.id.displayable_social_timeline_recommendation_icon);
		appName = (TextView) itemView.findViewById(R.id.displayable_social_timeline_recommendation_name);
		similarApps = (TextView) itemView.findViewById(R.id.displayable_social_timeline_recommendation_similar_apps);
		getAppButton = (Button) itemView.findViewById(R.id.displayable_social_timeline_recommendation_get_app_button);

	}

	@Override
	public void bindView(RecommendationDisplayable displayable) {

		title.setText(displayable.getTitle(getContext()));
		subtitle.setText(displayable.getHoursSinceLastUpdate(getContext()));

		ImageLoader.loadWithShadowCircleTransform(displayable.getAvatarResource(), image);

		ImageLoader.load(displayable.getAppIcon(), appIcon);

		appName.setText(displayable.getAppName());

		similarApps.setText(displayable.getSimilarAppsText(getContext()));

		getAppButton.setVisibility(View.VISIBLE);
		getAppButton.setText(displayable.getAppText(getContext()));
		getAppButton.setOnClickListener(view -> ((FragmentShower) getContext()).pushFragmentV4(AppViewFragment.newInstance(displayable.getAppId())));
	}

	@Override
	public void onViewAttached() {

	}

	@Override
	public void onViewDetached() {

	}
}
