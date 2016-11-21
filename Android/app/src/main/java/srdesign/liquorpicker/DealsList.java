package srdesign.liquorpicker;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.MultiAutoCompleteTextView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

public class DealsList extends Activity {
    private String mDeals = null;
    private String mComments = null;
    private String currentBarID = null;
    ListView lv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Intent intent = getIntent();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_deals_list);
        mDeals = intent.getStringExtra("Deals");
        mComments = intent.getStringExtra("Comments");
        currentBarID = intent.getStringExtra("BarID");
        lv = (ListView) findViewById(R.id.listView);
        listDeals();
    }


    private void listDeals(){
        View b = findViewById(R.id.commentButton);
        b.setVisibility(View.GONE);

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
            lv.setAdapter(new DealListAdapter(this, dealArray, upArray, downArray, dealID));
        }

        Switch sw = (Switch) findViewById(R.id.switch1);
        sw.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                switchView(isChecked);
            }
        });
    }

    private void listComments(){
        View b = findViewById(R.id.commentButton);
        b.setVisibility(View.VISIBLE);
        JSONArray comments = null;
        String commentArray[] = null;
        try {
            comments = new JSONArray(mComments);
            commentArray = new String[comments.length()];
            if(comments.length() < 1){
                commentArray[0] = "No comments yet! How about you write one yourself?";
            }
            else {
                for (int i = 0; i < comments.length(); ++i) {
                    JSONObject jObject = comments.getJSONObject(i);
                    commentArray[i] = jObject.getString("Comment");
                }
            }
        }
        catch(Exception e){
            e.printStackTrace();
        }
        lv.setAdapter(new CommentListAdapter(this, commentArray));
    }

    private void switchView(boolean showComments) {
        if(showComments){
            listComments();
        }
        else{
            listDeals();
        }
    }

    public void addComment(View view){
        setContentView(R.layout.add_comment);
        MultiAutoCompleteTextView textBox = (MultiAutoCompleteTextView) findViewById(R.id.commentTextBox);
        textBox.requestFocus();
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(textBox, InputMethodManager.SHOW_IMPLICIT);
    }

    public void submitComment(View view){
        MultiAutoCompleteTextView textBox = (MultiAutoCompleteTextView) findViewById(R.id.commentTextBox);
        String comment = textBox.getText().toString();
        try {
            comment = URLEncoder.encode(comment, "UTF-8");
            BasicQuery commentQuery = new BasicQuery();
            String query = "http://cise.ufl.edu/~jnassar/liquor-picker/addComment.php?id=" + currentBarID + "&comment=" + comment;
            commentQuery.execute(query);
            Toast.makeText(getApplicationContext(), "Posted new comment!",
                    Toast.LENGTH_SHORT).show();
            finish();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
            Toast.makeText(getApplicationContext(), "Error posting comment!",
                    Toast.LENGTH_SHORT).show();
        }
    }
}
