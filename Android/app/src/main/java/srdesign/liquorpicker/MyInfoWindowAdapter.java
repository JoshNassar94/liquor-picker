package srdesign.liquorpicker;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;

/**
 * Created by joshn on 11/21/2016.
 */
public class MyInfoWindowAdapter implements GoogleMap.InfoWindowAdapter {

    private LayoutInflater inflater=null;


    MyInfoWindowAdapter(LayoutInflater inflater) {
        this.inflater=inflater;
    }

    @Override
    public View getInfoWindow(Marker marker) {
        return null;
    }

    @Override
    public View getInfoContents(Marker marker) {
        View popup=inflater.inflate(R.layout.popup, null);
        TextView tv=(TextView)popup.findViewById(R.id.title);

        tv.setText(marker.getTitle());

        return(popup);
    }
}
