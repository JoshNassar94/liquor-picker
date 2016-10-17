package srdesign.liquorpicker;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Display;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;

public class MainActivity extends AppCompatActivity {

    private BasicQuery barQuery;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);
        ImageView iv= (ImageView)findViewById(R.id.imageView);
        iv.setImageResource(R.mipmap.main_logo);
        barQuery = new BasicQuery();
        barQuery.execute("http://cise.ufl.edu/~jnassar/liquor-picker/getBars.php");
        String bars = null;
        do {
            bars = barQuery.getContent();
        }while(bars == null);

        final Intent intent = new Intent(this, MapsActivity.class);
        intent.putExtra("BARS", bars);

        //Delay this so that logo will show
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivity(intent);
            }
        }, 1300);
    }
}
