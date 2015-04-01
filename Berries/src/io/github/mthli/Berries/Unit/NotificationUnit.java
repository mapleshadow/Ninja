package io.github.mthli.Berries.Unit;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import io.github.mthli.Berries.Browser.BerryContainer;
import io.github.mthli.Berries.Browser.Berry;
import io.github.mthli.Berries.R;
import io.github.mthli.Berries.Service.HolderService;

public class NotificationUnit {
    public static final int ID = 0x65536;

    public static Notification.Builder getBuilder(Context context) {
        SharedPreferences sp = context.getSharedPreferences(context.getString(R.string.sp_name), Context.MODE_PRIVATE);
        int priority = sp.getInt(context.getString(R.string.sp_notification_priority), Notification.PRIORITY_DEFAULT);
        boolean sound = sp.getBoolean(context.getString(R.string.sp_notification_sound), true);

        int done = 0;
        for (Berry view : BerryContainer.list()) {
            if (view.isFinish()) {
                done++;
            }
        }
        int total = BerryContainer.size();

        Notification.Builder builder = new Notification.Builder(context);
        builder.setNumber(total);
        builder.setPriority(priority);

        builder.setContentTitle(context.getString(R.string.app_name));
        if (done < total) {
            builder.setSmallIcon(R.drawable.ic_notification_berries);
            builder.setContentText(context.getString(R.string.notification_content_loading) + done);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                builder.setCategory(Notification.CATEGORY_STATUS);
            }
        } else {
            builder.setSmallIcon(R.drawable.ic_notification_done_all);
            builder.setContentText(context.getString(R.string.notification_content_done_all));

            if (sound) {
                builder.setDefaults(Notification.DEFAULT_SOUND);
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                builder.setCategory(Notification.CATEGORY_MESSAGE);
            }
        }

        // TODO: SDK
        Intent toService = new Intent(context, HolderService.class);
        toService.putExtra(IntentUnit.QUIT, true);
        PendingIntent quit = PendingIntent.getService(context, 0, toService, 0);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            builder.addAction(R.drawable.ic_action_quit_dark, context.getString(R.string.notification_action_quit), quit);
        } else {
            builder.addAction(R.drawable.ic_action_quit_light, context.getString(R.string.notification_action_quit), quit);
        }

        // TODO: SDK
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            builder.addAction(R.drawable.ic_action_list_dark, context.getString(R.string.notification_action_list), null);
        } else {
            builder.addAction(R.drawable.ic_action_list_light, context.getString(R.string.notification_action_list), null);
        }

        return builder;
    }

    public static void show(Context context, Notification.Builder builder, int flags) {
        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        Notification notification = builder.build();
        notification.flags = flags;
        manager.notify(ID, notification);
    }

    public static void cancel(Context context) {
        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        manager.cancel(ID);
    }
}