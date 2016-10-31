package srdesign.liquorpicker;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONObject;

public class DealsList extends AppCompatActivity {
    private String mDeals = null;
    private BasicQuery barQuery;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Intent intent = getIntent();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_deals_list);
        String title = intent.getStringExtra("Title");
        mDeals = intent.getStringExtra("Deals");

        listDeals();
    }


    private void listDeals(){
        TextView textView = (TextView)findViewById(R.id.textView);
        JSONArray deals = null;
        String dealArray[] = null;
        try {
            deals = new JSONArray(mDeals);
            dealArray = new String[deals.length()];
            for (int i = 0; i < deals.length(); ++i) {
                JSONObject jObject = deals.getJSONObject(i);
                dealArray[i] = jObject.getString("Deal");
            }
        }
        catch(Exception e){
            e.printStackTrace();
        }
        if(deals != null && deals.length() > 0) {
            ListView listView = (ListView)findViewById(R.id.listView);
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, android.R.id.text1, dealArray);
            listView.setAdapter(adapter);
            textView.setText("");
        }
        else{
            textView.setText("No deals here!");
        }
    }
}
