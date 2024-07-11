package com.ahmet.androidwebsocket.log;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * @author Ahmet TOPAK
 * @version 1.0
 * @since 7/11/2024
 */

public class LogMessageAdapter extends ArrayAdapter<LogMessage> {
    private Context context;
    private List<LogMessage> logMessages;

    public LogMessageAdapter(Context context, List<LogMessage> logMessages) {
        super(context, 0, logMessages);
        this.context = context;
        this.logMessages = logMessages;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LogMessage logMessage = getItem(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(android.R.layout.simple_list_item_1, parent, false);
        }

        TextView textView = convertView.findViewById(android.R.id.text1);
        textView.setText(logMessage.getMessage());
        textView.setTextColor(logMessage.getType().getColor());

        return convertView;
    }
}
