package srdesign.liquorpicker;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import org.json.JSONArray;

public class MainActivity extends AppCompatActivity {

    private QueryBars barQuery;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        barQuery = new QueryBars();
        barQuery.execute("http://cise.ufl.edu/~jnassar/liquor-picker/getBars.php");
    }

    public void sendMessage(View view) {
        Intent intent = new Intent(this, MapsActivity.class);
        String bars = null;
        do {
            bars = barQuery.getBars();
        }while(bars == null);
        intent.putExtra("BARS", bars);
        startActivity(intent);
    }
}
