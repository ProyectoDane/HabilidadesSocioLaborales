package com.belatrix.habilidadessociolaborales.ui;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.belatrix.habilidadessociolaborales.R;
import com.belatrix.habilidadessociolaborales.datamodel.Log;
import com.belatrix.habilidadessociolaborales.datamodel.User;
import com.belatrix.habilidadessociolaborales.datamodelmanagers.LogManager;
import com.belatrix.habilidadessociolaborales.datamodelmanagers.UserManager;
import com.belatrix.habilidadessociolaborales.ui.adapters.LogAdapter;

import java.util.List;

public class AllLogsActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_questions);

        long mUserId = getIntent().getExtras().getLong("userid");
        User user = UserManager.getInstance(this).getUser(mUserId);

        TextView mTitleTextView = (TextView) findViewById(R.id.titleTextView);
        mTitleTextView.setText(String.format("%s %s %s",getResources().getString(R.string.user_answers),user.getName(),user.getLastname()));

        View header = getLayoutInflater().inflate(R.layout.item_header_log,null);

        final List<Log> logList = LogManager.getInstance(this).fetchLogsByUser(mUserId);

        ListView mLogsListView = (ListView) findViewById(R.id.logsListView);
        mLogsListView.addHeaderView(header);
        mLogsListView.setAdapter(new LogAdapter(this, logList));
    }
}
