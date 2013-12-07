package com.herp.derp;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.lang.reflect.Constructor;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * Created by nathan on 11/26/13.
 */
public class AccountActivity extends FragmentActivity {

//    private String[] mPlanetTitles;
    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;

    ArrayList<Action> actions = new ArrayList<Action>(2);


    public AccountActivity() {
        super();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_account);

        actions.add(Action.ID_ACCOUNT, new Action(Action.ID_ACCOUNT, "Account", new AccountFragment()));
        actions.add(Action.ID_TRADE, new Action(Action.ID_TRADE, "Buy/Sell", new TradeFragment()));

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.content_frame, actions.get(Action.ID_ACCOUNT).fragmentInstance)
                    .commit();
        }

        getActionBar().setTitle(actions.get(Action.ID_ACCOUNT).title);


//        selectAction(Action.ID_ACCOUNT);

        initDrawer();

    }

    private void initDrawer() {

//        mPlanetTitles = getResources().getStringArray(R.array.planets_array);
//        mPlanetTitles = new String[] {"Account", "Transactions", "Something Else"};

//        ArrayList<String> menuItems = new ArrayList<String>();
//        menuItems.add(0, "Account");
//        menuItems.add(1, "Ticker");





//        actions.add(Action.ID_TRANSACTIONS, new Action(Action.ID_TRANSACTIONS, "Transactions", AccountFragment.class));
//        actions.add(Action.ID_OPEN_ORDERS, new Action(Action.ID_OPEN_ORDERS, "Open Orders", AccountFragment.class));

        ArrayList<String> menuItems = new ArrayList<String>();
        Iterator<Action> it = actions.iterator();
        while(it.hasNext()) {
            Action a = it.next();
            menuItems.add(a.id, a.title);
        }

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mDrawerList = (ListView) findViewById(R.id.left_drawer);

        // Set the adapter for the list view
        mDrawerList.setAdapter(new ArrayAdapter<String>(this,
                R.layout.drawer_list_item, menuItems));

        // Set the list's click listener
        mDrawerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                selectAction(i);
            }
        }); 
    }


    private void selectAction(int position) {
        Action selectedAction = actions.get(position);
        mDrawerList.setItemChecked(position, true);
        getActionBar().setTitle(selectedAction.title);

        try {
            // Assume there's one constructor with no params
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.content_frame, selectedAction.fragmentInstance)
                    .commit();
        } catch (Exception e) {
            e.printStackTrace();
        }

        mDrawerLayout.closeDrawer(mDrawerList);
    }


    public class Action {
        public static final int ID_ACCOUNT = 0;
        public static final int ID_TRADE = 1;
//        public static final int ID_TRANSACTIONS = 1;
//        public static final int ID_OPEN_ORDERS = 2;
//        public static final int ID_TRADE = 3;

        public int id;
        public String title;
        public Fragment fragmentInstance;

        public Action(int id, String title, Fragment fragmentInstance) {
            this.id = id;
            this.title = title;
            this.fragmentInstance = fragmentInstance;
        }
    }
}
