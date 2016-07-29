package com.belatrix.habilidadessociolaborales.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.belatrix.habilidadessociolaborales.R;
import com.belatrix.habilidadessociolaborales.datamodel.Session;
import com.belatrix.habilidadessociolaborales.datamodel.User;
import com.belatrix.habilidadessociolaborales.datamodelmanagers.SessionManager;
import com.belatrix.habilidadessociolaborales.datamodelmanagers.UserManager;
import com.belatrix.habilidadessociolaborales.ui.adapters.SessionAdapter;

import java.util.List;

public class StatisticsActivity extends Activity {

    private long userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistics);

        userId = getIntent().getExtras().getLong("userid");

        User user = UserManager.getInstance(this).getUser(userId);

        TextView mTitleTextView = (TextView) findViewById(R.id.titleTextView);
        mTitleTextView.setText(String.format("Sesiones del usuario %s %s",user.getName(),user.getLastname()));

        Button mAllButton = (Button) findViewById(R.id.allLogsButton);
        mAllButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(StatisticsActivity.this, AllLogsActivity.class);
                intent.putExtra("userid", userId);
                startActivity(intent);
            }
        });

        final List<Session> sessions = SessionManager.getInstance(this).fetchSessionByUser(userId);

        ListView list = (ListView) findViewById(R.id.sessionListView);
        list.addHeaderView(getLayoutInflater().inflate(R.layout.item_header_session,null));
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if (i > 0) {
                    Intent intent = new Intent(StatisticsActivity.this, LogsActivity.class);
                    intent.putExtra("sessionid", sessions.get(i - 1).getId());
                    startActivity(intent);
                }
            }
        });
        list.setAdapter(new SessionAdapter(this, sessions));
    }
}
