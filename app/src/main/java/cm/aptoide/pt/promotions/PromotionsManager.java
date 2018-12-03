package cm.aptoide.pt.promotions;

import java.util.ArrayList;
import java.util.List;
import rx.Observable;

public class PromotionsManager {

  public Observable<List<PromotionApp>> getPromotionApps() {
    return Observable.just(getListOfPromotionApps());
  }

  private List<PromotionApp> getListOfPromotionApps() {
    List<PromotionApp> promotionAppList = new ArrayList<>();

    promotionAppList.add(new PromotionApp("Ana's app", "cm.aptoide.pt.ana", 123,
        "http://pool.apk.aptoide.com/lordballiwns/com-facebook-orca-132958908-42161891-bfb0e8f4a51fcbaa16f1840322eb232a.apk",
        "http://pool.apk.aptoide.com/lordballiwns/alt/Y29tLWZhY2Vib29rLW9yY2EtMTMyOTU4OTA4LTQyMTYxODkxLWJmYjBlOGY0YTUxZmNiYWExNmYxODQwMzIyZWIyMzJh.apk",
        "http://pool.img.aptoide.com/lordballiwns/76e0376928b8393227a150fbed5d6b4a_icon.png"));
    promotionAppList.add(new PromotionApp("Joao's app", "cm.aptoide.pt.joao", 123,
        "http://pool.apk.aptoide.com/bds-store/nzt-metal-shooter-commando-47-41200964-0e13c87fc172d3fa7ac0392ec12e72df.apk",
        "http://pool.apk.aptoide.com/bds-store/alt/bnp0LW1ldGFsLXNob290ZXItY29tbWFuZG8tNDctNDEyMDA5NjQtMGUxM2M4N2ZjMTcyZDNmYTdhYzAzOTJlYzEyZTcyZGY.apk",
        "http://pool.img.aptoide.com/bds-store/8335ae2d104ce4dcbfec66fc07c1e7ce_icon.png"));

    return promotionAppList;
  }
}