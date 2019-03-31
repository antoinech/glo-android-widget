package com.antoinedevblog.www.widget;


import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.view.View;
import android.widget.ListView;
import android.widget.RemoteViews;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.List;

import static android.content.Context.MODE_PRIVATE;

/**
 * Implementation of App Widget functionality.
 */
public class GloWidget extends AppWidgetProvider {
    static private final String ACTION_REFRESH = "ActionRefresh";
    static private final String ACTION_REFRESH_STACK = "ActionRefreshStack";
    static private final String ACTION_PREV = "ActionPrev";
    static private final String ACTION_NEXT = "ActionNext";
    static private String columnName = "No Column Yet";
    static private List<String> columnNames;
    static private int currColumnId = 0;
    static private int maxColumnId = 3;
    ListView listView;
    private int currColumnId1;
    private static SharedPreferences sharedPref;
    private static JSONArray Columns = new JSONArray();
    private static boolean somethingHasChanged = true;

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId) {
        sharedPref = context.getSharedPreferences("glo-app", MODE_PRIVATE);

        String columnsString = sharedPref.getString("columns", "null");
        if(!columnsString.equals("null")) {
            try {
                Columns = new JSONArray(columnsString);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        if(Columns != null){
            maxColumnId = Columns.length();
            if(currColumnId >= maxColumnId){
                currColumnId = 0;
            }
            try {
                columnName = Columns.getJSONObject(currColumnId).getString("name");
                String columnId = Columns.getJSONObject(currColumnId).getString("id");
                sharedPref.edit().putString("columnid",columnId).commit();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        Intent refreshIntent = new Intent(context, GloWidget.class);
        refreshIntent.setAction(ACTION_REFRESH);
        refreshIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
        PendingIntent refreshPendingIntent = PendingIntent.getBroadcast(context,
                0, refreshIntent, 0);

        Intent prevIntent = new Intent(context, GloWidget.class);
        prevIntent.setAction(ACTION_PREV);
        prevIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
        PendingIntent prevPendingIntent = PendingIntent.getBroadcast(context,
                0, prevIntent, 0);

        Intent nextIntent = new Intent(context, GloWidget.class);
        nextIntent.setAction(ACTION_NEXT);
        nextIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
        PendingIntent nextPendingIntent = PendingIntent.getBroadcast(context,
                0, nextIntent, 0);


        Intent serviceIntent = new Intent(context, GloWidgetService.class);
        serviceIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
        serviceIntent.setData(Uri.parse(serviceIntent.toUri(Intent.URI_INTENT_SCHEME)));

        // Construct the RemoteViews object
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.glo_widget);

        views.setTextViewText(R.id.appwidget_text, "GitKraken Glo");//
        views.setTextViewText(R.id.column_label, columnName);
        views.setRemoteAdapter(R.id.stackview, serviceIntent);
        views.setOnClickPendingIntent(R.id.refresh_button, refreshPendingIntent);
        views.setOnClickPendingIntent(R.id.button_prev, prevPendingIntent);
        views.setOnClickPendingIntent(R.id.button_next, nextPendingIntent);

        if(currColumnId == 0) {
            views.setViewVisibility(R.id.button_prev,View.INVISIBLE);
        }else{
            views.setViewVisibility(R.id.button_prev,View.VISIBLE);
        }
        if(currColumnId == (maxColumnId-1)) {
            views.setViewVisibility(R.id.button_next,View.INVISIBLE);
        }else{
            views.setViewVisibility(R.id.button_next,View.VISIBLE);
        }

        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views);
        if(somethingHasChanged) {
            appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetId, R.id.stackview);
            somethingHasChanged = false;
        }
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created

    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }

    @Override
    public void onReceive(Context context, Intent intent) {


        if (ACTION_NEXT.equals(intent.getAction())) {
            currColumnId += 1;
            CallUpdate(context);
        }
        if (ACTION_PREV.equals(intent.getAction())) {
            currColumnId -= 1;
            CallUpdate(context);
        }
        if (ACTION_REFRESH.equals(intent.getAction())) {
            somethingHasChanged = true;
            CallUpdate(context);
        }
        if(ACTION_REFRESH_STACK.equals(intent.getAction())){
            somethingHasChanged = true;
            CallUpdate(context);
        }

        super.onReceive(context, intent);
    }


    public void CallUpdate(Context context){
        Intent intent = new Intent(context,GloWidget.class);
        intent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
        int[] ids = AppWidgetManager.getInstance(context).getAppWidgetIds(new ComponentName(context,GloWidget.class));
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, ids);
        context.sendBroadcast(intent);
    }

}

