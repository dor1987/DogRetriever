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

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;

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
            Map<String, String> data = remoteMessage.getData();
            if(data!=null){
                String temp1 = data.get("lat");
                String temp2 = data.get("long");
                showNotification(remoteMessage.getNotification().getTitle(), remoteMessage.getNotification().getBody(),temp1,temp2);
            }
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


    public void showNotification(String title,String message,String latitude, String logitude){
        NotificationManager notificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(channelId, channelName ,
                    NotificationManager.IMPORTANCE_HIGH);
            channel.setDescription("Default Notification Channel");
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(channel);
            }
        }


        //breaking msg to lat and long

        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);

        intent.putExtra("lat",latitude)
                .putExtra("long",logitude);

        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT );

            NotificationCompat.Builder builder = new NotificationCompat.Builder(this, channelId)
                    .setContentTitle(title)
                    .setSmallIcon(R.drawable.asset21h)
                    .setAutoCancel(true)
                    .setContentText(message)
                    .setContentIntent(pendingIntent);
            notificationManager.notify(notificationId, builder.build());
    }
    public int createID() {
        Date now = new Date();
        int id = Integer.parseInt(new SimpleDateFormat("ddHHmmss",Locale.ITALY).format(now));
        return id;
    }
}
