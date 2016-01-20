package com.belatrix.habilidadessociolaborales.ui;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.belatrix.habilidadessociolaborales.R;
import com.belatrix.habilidadessociolaborales.datamodel.Log;
import com.belatrix.habilidadessociolaborales.datamodel.Session;
import com.belatrix.habilidadessociolaborales.datamodel.User;
import com.belatrix.habilidadessociolaborales.datamodelmanagers.LogManager;
import com.belatrix.habilidadessociolaborales.datamodelmanagers.SessionManager;
import com.belatrix.habilidadessociolaborales.datamodelmanagers.UserManager;
import com.belatrix.habilidadessociolaborales.ui.adapters.LogAdapter;

import java.text.SimpleDateFormat;
import java.util.List;

public class LogsActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_logs);

        long mSessionId = getIntent().getExtras().getLong("sessionid");
        final Session session = SessionManager.getInstance(this).fetchSessionById(mSessionId);

        final User user = UserManager.getInstance(this).getUser(session.getUserId());

        TextView mTitleTextView = (TextView) findViewById(R.id.titleTextView);
        mTitleTextView.setText(String.format("Sesión del %s",new SimpleDateFormat("dd-MM-yyyy").format(session.getStartDate())));
        mTitleTextView.setText(String.format("%s. Alumno: %s %s",mTitleTextView.getText(),user.getName(),user.getLastname()));

        final List<Log> logList = LogManager.getInstance(this).fetchLogsBySession(mSessionId);

        ListView mLogsListView = (ListView) findViewById(R.id.logsListView);
        mLogsListView.addHeaderView(getLayoutInflater().inflate(R.layout.item_header_log,null));
        mLogsListView.setAdapter(new LogAdapter(this, logList));
        View empty = getLayoutInflater().inflate(R.layout.empty_log, null, false);
        addContentView(empty,new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        mLogsListView.setEmptyView(empty);
    }
}
