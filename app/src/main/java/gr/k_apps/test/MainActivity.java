package gr.k_apps.test;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {


    ListView list;
    Button btn;

    private Cursor c;
    private SQLiteAdapter DbHelper;

    private Boolean exit = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        list = (ListView)findViewById(R.id.list);

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position,
                                    long id) {
                c = DbHelper.fetchNote(id);
                startManagingCursor(c);
                String plId = c.getString(c.getColumnIndexOrThrow(SQLiteAdapter.KEY_TRACKID));
                Intent i = new Intent(getApplicationContext(),
                        TrackActivity.class);
                i.putExtra("playlistid", plId);
                startActivity(i);

                }
            });

        btn = (Button) findViewById(R.id.button);

        DbHelper = new SQLiteAdapter(this);
        DbHelper.open();

        if (DbHelper.chkDB()){ //check if table has data if yes show data from sqlite
            fillData();
            registerForContextMenu(list);
        }else{ //if no, take data and save it on sqlite and then show it
            ReadDataFromDB();
        }

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                DbHelper.deleteTable();

                Intent i = new Intent(getApplicationContext(),
                        MainActivity.class);
                startActivity(i);

            }
        });


        if (!isInternetOn()){
            Toast.makeText(getApplicationContext(), "No internet connection!",
                    Toast.LENGTH_SHORT).show();
        }


    }



    private void fillData() {

        // Get all of the notes from the database and create the item list
        Cursor notesCursor = DbHelper.fetchAllNotes();
        startManagingCursor(notesCursor);


        String[] from = new String[] { SQLiteAdapter.KEY_NAME ,SQLiteAdapter.KEY_COUNT};
        int[] to = new int[] { R.id.textName ,R.id.textTracksNum};

        // Now create an array adapter and set it to display using our row
        SimpleCursorAdapter notes =
                new SimpleCursorAdapter(this, R.layout.row_playlist, notesCursor, from, to);
        list.setAdapter(notes);
    }

    public void onDestroy() {

        super.onDestroy();

        DbHelper.deleteTable();
    }

    public void onBackPressed() {

            //super.onBackPressed();
            if (exit) {
                Intent intent = new Intent();//exit to phone
                intent.setAction(Intent.ACTION_MAIN);
                intent.addCategory(Intent.CATEGORY_HOME);
                startActivity(intent);
            } else {
                Toast.makeText(this, "Press Back again to Exit.",
                        Toast.LENGTH_SHORT).show();
                exit = true;
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        exit = false;
                    }
                }, 3 * 1000);

            }

    }



    private void ReadDataFromDB() {


        JsonObjectRequest jreq = new JsonObjectRequest(Request.Method.GET, "http://akazoo.com/services/Test/TestMobileService.svc/playlists", null,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        try {

                            JSONArray ja = response.getJSONArray("Result");

                            for (int i = 0; i < ja.length(); i++) {

                                JSONObject jobj = ja.getJSONObject(i);


                                String name = jobj.getString("Name");
                                String itemCount = jobj.getString("ItemCount");
                                String itemId = jobj.getString("PlaylistId");

                                //Save data in sqlite DB
                                DbHelper.createItem(name, itemCount, itemId );


                            }
                            fillData();
                            registerForContextMenu(list);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(jreq);

    }



    public final boolean isInternetOn() {

        ConnectivityManager con = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = con.getActiveNetworkInfo();
        // ARE WE CONNECTED TO THE NET
        if (networkInfo != null) {
            // MESSAGE TO SCREEN FOR TESTING (IF REQ)
            //Toast.makeText(this, connectionType + ” connected”, Toast.LENGTH_SHORT).show();
            return true;
        } else {
            return false;
        }
    }

}
