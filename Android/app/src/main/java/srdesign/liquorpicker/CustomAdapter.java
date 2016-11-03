package srdesign.liquorpicker;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class CustomAdapter extends BaseAdapter{
    String [] result;
    String [] upText;
    String [] downText;
    String [] dealID;
    Context context;
    private static LayoutInflater inflater=null;

    public CustomAdapter(Activity mainActivity, String[] prgmNameList, String[] upTextList, String[] downTextList, String[] id) {
        result=prgmNameList;
        upText = upTextList;
        downText = downTextList;
        dealID = id;
        context=mainActivity;
        inflater = ( LayoutInflater )context.
                getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }
    @Override
    public int getCount() {
        return result.length;
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public class Holder
    {
        TextView tv;
        ImageView img;
    }
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final Holder holder=new Holder();
        final View rowView;
        rowView = inflater.inflate(R.layout.deals_layout, null);
        holder.tv=(TextView) rowView.findViewById(R.id.dealView);
        holder.tv.setText(result[position]);
        holder.tv =(TextView) rowView.findViewById(R.id.upTextView);
        holder.tv.setText(upText[position]);
        holder.tv =(TextView) rowView.findViewById(R.id.downTextView);
        holder.tv.setText(downText[position]);

        ImageView img = (ImageView) rowView.findViewById(R.id.thumbsUpView);
        img.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                ViewGroup viewGroup=(ViewGroup)v.getParent();
                TextView tv = (TextView)viewGroup.findViewById(R.id.upTextView);
                int votes = Integer.parseInt(upText[position]) + 1;
                upText[position] = Integer.toString(votes);
                tv.setText(Integer.toString(votes));
                BasicQuery barQuery;
                barQuery = new BasicQuery();
                String query = "http://cise.ufl.edu/~jnassar/liquor-picker/upvote.php?id=" + dealID[position];
                barQuery.execute(query);
            }
        });

        img = (ImageView) rowView.findViewById(R.id.thumbsDownView);
        img.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                ViewGroup viewGroup=(ViewGroup)v.getParent();
                TextView tv = (TextView)viewGroup.findViewById(R.id.downTextView);
                int votes = Integer.parseInt(downText[position]) + 1;
                downText[position] = Integer.toString(votes);
                tv.setText(Integer.toString(votes));
                BasicQuery barQuery;
                barQuery = new BasicQuery();
                String query = "http://cise.ufl.edu/~jnassar/liquor-picker/downvote.php?id=" + dealID[position];
                barQuery.execute(query);
            }
        });

        return rowView;
    }

}