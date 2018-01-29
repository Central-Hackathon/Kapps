package gr.k_apps.test;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class TrackActivity extends AppCompatActivity {

    String playlistID;

    ArrayList<HashMap<String, String>> Item_List;
    ListAdapter adapter;
    ListView List;

    public static final String ITEM_TRACK = "TrackName";
    public static final String ITEM_ARTIST = "ArtistName";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_track);

        Item_List = new ArrayList<HashMap<String, String>>();

        List= (ListView)findViewById(R.id.listTrack);

        Intent intent = getIntent();
        playlistID = intent.getExtras().getString("playlistid");

        ReadDataFromDB(playlistID);
    }


    private void ReadDataFromDB(String pl) {

        JsonObjectRequest jreq = new JsonObjectRequest(Request.Method.GET, " http://akazoo.com/services/Test/TestMobileService.svc/playlist?playlistid="+pl, null,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        try {

                            JSONObject jobj1 = response.getJSONObject("Result");
                            JSONArray ja = jobj1.getJSONArray("Items");

                            for (int i = 0; i < ja.length(); i++) {

                                JSONObject jobj = ja.getJSONObject(i);
                                HashMap<String, String> item = new HashMap<String, String>();
                                item.put(ITEM_TRACK, jobj.getString(ITEM_TRACK));
                                item.put(ITEM_ARTIST, jobj.getString(ITEM_ARTIST));


                                Item_List.add(item);

                                String[] from = { ITEM_TRACK, ITEM_ARTIST };
                                int[] to = { R.id.textTrackName, R.id.textArtistName};

                                adapter = new SimpleAdapter(
                                        getApplicationContext().getApplicationContext(),
                                        Item_List, R.layout.track_row, from, to);

                                List.setAdapter(adapter);
                            }

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
}
