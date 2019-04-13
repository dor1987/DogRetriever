package dtg.dogretriever.Presenter;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TaskStackBuilder;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
//import android.support.v4.app.NotificationCompat;
//import android.support.v4.app.NotificationManagerCompat;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import androidx.core.app.NotificationCompat;
import androidx.preference.PreferenceManager;
import dtg.dogretriever.Model.FirebaseAdapter;
import dtg.dogretriever.R;

public class MyMessagingService extends FirebaseMessagingService {
    public static final String SHARED_PREFS = "sharedPrefs";

    private FirebaseAdapter firebaseAdapter;
    private String token;
    int notificationId = createID();
    String channelId = "channel-id";
    String channelName = "Channel Name";

    public MyMessagingService() {
        firebaseAdapter = firebaseAdapter.getInstanceOfFireBaseAdapter();
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
        boolean isNotificationOn = sharedPreferences.getBoolean("notification_pre",true);


        if(isNotificationOn) {
            showNotification(remoteMessage.getNotification().getTitle(), remoteMessage.getNotification().getBody());
        }
    }

    @Override
    public void onNewToken(String s) {
        super.onNewToken(s);
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS,MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("token",s+"");
        editor.commit();


    }


    public void showNotification(String title,String message){
        NotificationManager notificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(channelId, channelName ,
                    NotificationManager.IMPORTANCE_HIGH);
            channel.setDescription("Default Notification Channel");
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(channel);
            }
        }

            NotificationCompat.Builder builder = new NotificationCompat.Builder(this, channelId)
                    .setContentTitle(title)
                    .setSmallIcon(R.drawable.ic_launcher_background)
                    .setAutoCancel(true)
                    .setContentText(message);

            TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
            stackBuilder.addNextIntent(new Intent(this, MainActivity.class));
            PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0,PendingIntent.FLAG_UPDATE_CURRENT);
            builder.setContentIntent(resultPendingIntent);

            notificationManager.notify(notificationId, builder.build());



            /*
            NotificationManagerCompat manager = NotificationManagerCompat.from(this);
            manager.notify(999, builder.build());
*/


    }
    public int createID() {
        Date now = new Date();
        int id = Integer.parseInt(new SimpleDateFormat("ddHHmmss",Locale.ITALY).format(now));
        return id;
    }
}
