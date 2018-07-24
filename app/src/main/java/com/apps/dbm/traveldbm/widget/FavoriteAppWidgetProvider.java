package com.apps.dbm.traveldbm.widget;


import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;

import android.content.Context;
import android.content.Intent;

import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.TaskStackBuilder;
import android.widget.RemoteViews;

import com.apps.dbm.traveldbm.R;
import com.apps.dbm.traveldbm.SearchActivity;


public class FavoriteAppWidgetProvider extends AppWidgetProvider {


    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        updateWidget(context,appWidgetManager,appWidgetIds);
    }

    @Override
    public void onAppWidgetOptionsChanged(Context context, AppWidgetManager appWidgetManager, int appWidgetId, Bundle newOptions) {
        super.onAppWidgetOptionsChanged(context, appWidgetManager, appWidgetId, newOptions);
    }

    @Override
    public void onEnabled(Context context) {
        super.onEnabled(context);
    }

    @Override
    public void onDeleted(Context context, int[] appWidgetIds) {
        super.onDeleted(context, appWidgetIds);
    }

    public static void updateWidget(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds){


        for(int i=0;i<appWidgetIds.length;i++) {

            Intent svcIntent = new Intent(context, WidgetService.class);
            svcIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetIds[i]);
            svcIntent.setData(Uri.parse(svcIntent.toUri(Intent.URI_INTENT_SCHEME)));

            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_layout);
            views.setTextViewText(R.id.widget_title_text_view,context.getString(R.string.widget_title));
            views.setRemoteAdapter(appWidgetIds[i], R.id.widget_list_view, svcIntent);
            views.setEmptyView(R.id.widget_list_view, R.id.widget_empty_text_view);

            Intent activityIntent = new Intent(context, SearchActivity.class);

            PendingIntent pendingIntent = TaskStackBuilder.create(context)
                    .addNextIntentWithParentStack(activityIntent)
                    .getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

            views.setPendingIntentTemplate(R.id.widget_list_view,pendingIntent);

            appWidgetManager.updateAppWidget(appWidgetIds[i], views);

        }

    }
}
