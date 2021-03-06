package cm.aptoide.pt.notification;

public class NotificationIdsMapper {
  public NotificationIdsMapper() {
  }

  int getNotificationId(@AptoideNotification.NotificationType int notificationType)
      throws RuntimeException {
    switch (notificationType) {
      case AptoideNotification.CAMPAIGN:
        return 0;
      case AptoideNotification.COMMENT:
      case AptoideNotification.LIKE:
      case AptoideNotification.NEW_FOLLOWER:
      case AptoideNotification.NEW_SHARE:
      case AptoideNotification.NEW_ACTIVITY:
        return 1;
      case AptoideNotification.POPULAR:
        return 2;
      case AptoideNotification.APPC_PROMOTION:
        return 3;
      case AptoideNotification.NEW_FEATURE:
        return 4;
      default:
        throw new IllegalArgumentException("unknown notification type " + notificationType);
    }
  }

  @AptoideNotification.NotificationType Integer[] getNotificationType(int notificationId)
      throws RuntimeException {
    switch (notificationId) {
      case 0:
        return new Integer[] {
            AptoideNotification.CAMPAIGN
        };
      case 1:
        return new Integer[] {
            AptoideNotification.LIKE, AptoideNotification.COMMENT, AptoideNotification.NEW_SHARE,
            AptoideNotification.NEW_ACTIVITY, AptoideNotification.NEW_FOLLOWER
        };
      case 2:
        return new Integer[] {
            AptoideNotification.POPULAR,
        };
      case 3:
        return new Integer[] {
            AptoideNotification.APPC_PROMOTION
        };
      case 4:
        return new Integer[] {
            AptoideNotification.NEW_FEATURE
        };
      default:
        throw new IllegalArgumentException("unknown notification notificationId " + notificationId);
    }
  }
}