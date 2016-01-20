package com.belatrix.habilidadessociolaborales.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import com.belatrix.habilidadessociolaborales.R;
import com.belatrix.habilidadessociolaborales.common.BaseActivity;
import com.belatrix.habilidadessociolaborales.datamodel.Session;
import com.belatrix.habilidadessociolaborales.datamodel.User;
import com.belatrix.habilidadessociolaborales.datamodelmanagers.SessionManager;
import com.belatrix.habilidadessociolaborales.datamodelmanagers.UserManager;
import com.belatrix.habilidadessociolaborales.ui.adapters.ListViewUsersAdapter;

import java.util.Calendar;
import java.util.List;

public class LoginActivity extends BaseActivity {

    private ListView mUsersListView;
    private ListViewUsersAdapter mListViewUsersAdapter;
    private static final int EXIT_TIME_INTERVAL = 2000; // # milliseconds, desired time passed between two back presses.
    private long mBackPressed;

    //private TextView mLoginTitleTextView;
    private ImageButton mCreateUserButton;

    private List<User> mUsersList;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        initializeUI();

        populateData();

        mUsersListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                long sessionId;
                if (SessionManager.getInstance(LoginActivity.this).hasOpenedSessions(mUsersList.get(i).getId())) {
                    sessionId = SessionManager.getInstance(LoginActivity.this).getLastSessionId(mUsersList.get(i).getId());
                } else {
                    Session session = new Session();
                    session.setClosed(false);
                    session.setStartDate(Calendar.getInstance().getTime());
                    session.setUser(mUsersList.get(i));
                    sessionId = SessionManager.getInstance(LoginActivity.this).saveSession(session);
                }
                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                intent.putExtra("userid", mUsersList.get(i).getId());
                intent.putExtra("sessionid", sessionId);
                startActivity(intent);
            }
        });

        mCreateUserButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, UserEditActivity.class);
                intent.putExtra("SourceActivity", "LoginActivity");
                startActivity(intent);
            }
        });
    }

    private void initializeUI() {
        mUsersListView = (ListView) findViewById(R.id.usersListView);
        View empty = getLayoutInflater().inflate(R.layout.empty_list_item, null, false);
        addContentView(empty, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        mUsersListView.setEmptyView(empty);

        //mLoginTitleTextView = (TextView) findViewById(R.id.loginTitleTextView);
        mCreateUserButton = (ImageButton) findViewById(R.id.createUserButton);
    }

    private void populateData() {
        List<User> userList;

        userList = UserManager.getInstance(LoginActivity.this).getUsersList();

        mUsersList = userList;

        mListViewUsersAdapter = new ListViewUsersAdapter(LoginActivity.this, mUsersList);

        mUsersListView.setAdapter(mListViewUsersAdapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        // This code should be improved because it's the same as above.
        List<User> userList;
        userList = UserManager.getInstance(LoginActivity.this).getUsersList();

        mUsersList = userList;

        mListViewUsersAdapter = new ListViewUsersAdapter(LoginActivity.this, mUsersList);

        mUsersListView.setAdapter(mListViewUsersAdapter);
    }

    @Override
    public void onBackPressed() {
        if (mBackPressed + EXIT_TIME_INTERVAL > System.currentTimeMillis()) {
            super.onBackPressed();
            return;
        } else {
            Toast.makeText(getBaseContext(), R.string.tap_back_again_to_exit, Toast.LENGTH_SHORT).show();
        }

        mBackPressed = System.currentTimeMillis();
    }
}
