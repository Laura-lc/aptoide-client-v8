package cm.aptoide.pt.home;

import cm.aptoide.pt.R;

/**
 * Created by D01 on 16/03/18.
 */

public class BottomNavigationMapper {

  public BottomNavigationItem mapItemClicked(Integer menuItemId) {
    BottomNavigationItem bottomNavigationItem = null;
    switch (menuItemId) {
      case R.id.action_home:
        bottomNavigationItem = BottomNavigationItem.HOME;
        break;
      case R.id.action_search:
        bottomNavigationItem = BottomNavigationItem.SEARCH;
        break;
      case R.id.action_stores:
        bottomNavigationItem = BottomNavigationItem.STORES;
        break;
      case R.id.action_apps:
        bottomNavigationItem = BottomNavigationItem.APPS;
        break;
    }
    if (bottomNavigationItem == null) {
      throw new IllegalStateException("The selected menuItem is not supported");
    }
    return bottomNavigationItem;
  }
}