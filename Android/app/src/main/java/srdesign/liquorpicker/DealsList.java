package srdesign.liquorpicker;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Random;

public class DealsList extends Activity {
    private String mDeals = null;

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
        ListView lv=(ListView) findViewById(R.id.listView);
        Random rand = new Random();
        JSONArray deals = null;
        String dealArray[] = null;
        String upArray[] = null;
        String downArray[] = null;
        String dealID[] = null;
        try {
            deals = new JSONArray(mDeals);
            dealArray = new String[deals.length()];
            upArray = new String[deals.length()];
            downArray = new String[deals.length()];
            dealID = new String[deals.length()];
            for (int i = 0; i < deals.length(); ++i) {
                JSONObject jObject = deals.getJSONObject(i);
                dealArray[i] = jObject.getString("Deal");
                dealID[i] = jObject.getString("id");
                upArray[i] = jObject.getString("UpVotes");
                downArray[i] = jObject.getString("DownVotes");
            }
        }
        catch(Exception e){
            e.printStackTrace();
        }
        if(deals != null && deals.length() > 0) {
            lv.setAdapter(new CustomAdapter(this, dealArray, upArray, downArray, dealID));
        }
    }
}
