package com.example.memoraapp;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.AudioAttributes;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;

import androidx.core.app.NotificationCompat;

import java.util.Calendar;

public class ReminderReceiver extends BroadcastReceiver {

    private static final String CHANNEL_ID = "ReminderChannel_SystemSound";

    @SuppressLint("ScheduleExactAlarm")
    @Override
    public void onReceive(Context context, Intent intent) {
        String medicinename = intent.getStringExtra("medicinename");
        String selectdate = intent.getStringExtra("selectdate");
        String caregivername = intent.getStringExtra("caregivername");
        boolean isDaily = intent.getBooleanExtra("is_daily", false);
        int requestCode = intent.getIntExtra("request_code", 0);
        int hour = intent.getIntExtra("time_hour", -1);
        int minute = intent.getIntExtra("time_minute", -1);

        // 1. Show the Notification
        showNotification(context, medicinename, selectdate, caregivername, isDaily, requestCode);

        // 2. RESCHEDULE if it is a Daily Alarm
        if (isDaily && hour != -1) {
            Calendar nextAlarm = Calendar.getInstance();
            nextAlarm.set(Calendar.HOUR_OF_DAY, hour);
            nextAlarm.set(Calendar.MINUTE, minute);
            nextAlarm.set(Calendar.SECOND, 0);

            // Add 1 day to current time
            nextAlarm.add(Calendar.DAY_OF_MONTH, 1);

            Intent newIntent = new Intent(context, ReminderReceiver.class);
            newIntent.putExtras(intent); // Copy all extras (name, id, is_daily, etc)

            PendingIntent pendingIntent = PendingIntent.getBroadcast(
                    context,
                    requestCode, // Keep same ID to maintain the chain
                    newIntent,
                    PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT
            );

            AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            if (alarmManager != null) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, nextAlarm.getTimeInMillis(), pendingIntent);
                } else {
                    alarmManager.setExact(AlarmManager.RTC_WAKEUP, nextAlarm.getTimeInMillis(), pendingIntent);
                }
            }
        }
    }

    private void showNotification(Context context, String medName, String date, String caregiver, boolean isDaily, int reqCode) {
        Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
        if (alarmSound == null) {
            alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);
        }

        createNotificationChannel(context, alarmSound);

        Intent notificationIntent = new Intent(context, Home.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(
                context,
                reqCode,
                notificationIntent,
                PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT
        );

        String title = "Reminder" + (isDaily ? " (Daily)" : "");
        String content = "From: " + caregiver + "\n" + medName;

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.logo)
                .setContentTitle(title)
                .setContentText(content)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(content))
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .setSound(alarmSound);

        Notification notification = builder.build();
        notification.flags |= Notification.FLAG_INSISTENT;

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (notificationManager != null) {
            notificationManager.notify(reqCode, notification);
        }
    }

    private void createNotificationChannel(Context context, Uri soundUri) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationManager manager = context.getSystemService(NotificationManager.class);
            // Check if channel exists before creating to avoid resetting sound
            if (manager != null && manager.getNotificationChannel(CHANNEL_ID) == null) {
                NotificationChannel channel = new NotificationChannel(CHANNEL_ID, "Medical Reminders", NotificationManager.IMPORTANCE_HIGH);
                channel.setDescription("Channel for high priority medical reminders");

                AudioAttributes audioAttributes = new AudioAttributes.Builder()
                        .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                        .setUsage(AudioAttributes.USAGE_ALARM)
                        .build();

                channel.setSound(soundUri, audioAttributes);
                channel.enableVibration(true);
                manager.createNotificationChannel(channel);
            }
        }
    }
}