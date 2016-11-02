package srdesign.liquorpicker;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.TextView;

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
        try {
            deals = new JSONArray(mDeals);
            dealArray = new String[deals.length()];
            upArray = new String[deals.length()];
            downArray = new String[deals.length()];
            for (int i = 0; i < deals.length(); ++i) {
                JSONObject jObject = deals.getJSONObject(i);
                dealArray[i] = jObject.getString("Deal");
                upArray[i] = Integer.toString(rand.nextInt(201));
                downArray[i] = Integer.toString(rand.nextInt(201));
            }
        }
        catch(Exception e){
            e.printStackTrace();
        }
        if(deals != null && deals.length() > 0) {
            //ListView listView = (ListView)findViewById(R.id.listView);
            //ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, android.R.id.text1, dealArray);
            //listView.setAdapter(adapter);
            //textView.setText("");
            lv.setAdapter(new CustomAdapter(this, dealArray, upArray, downArray));
        }
        else{
            //textView.setText("No deals here!");
        }
    }
}
