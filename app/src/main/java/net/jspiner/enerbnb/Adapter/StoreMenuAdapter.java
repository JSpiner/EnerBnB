package net.jspiner.enerbnb.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import net.jspiner.enerbnb.Model.Menu;
import net.jspiner.enerbnb.Model.SubMenu;
import net.jspiner.enerbnb.R;
import net.jspiner.enerbnb.View.AnimatedExpandableListView;

import java.util.List;

/**
 * Created by JSpiner on 2015. 8. 26..
 */
public class StoreMenuAdapter extends AnimatedExpandableListView.AnimatedExpandableListAdapter {

    private LayoutInflater inflater;

    private List<Menu> items;
    Context context;

    public StoreMenuAdapter(Context context) {
        inflater = LayoutInflater.from(context);
        this.context = context;
    }

    public void setData(List<Menu> items) {
        this.items = items;
    }

    @Override
    public SubMenu getChild(int groupPosition, int childPosition) {
        return items.get(groupPosition).items.get(childPosition);
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public View getRealChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        //ChildHolder holder;
        SubMenu item = getChild(groupPosition, childPosition);
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.item_menu_child,null);
        }
        ((TextView)convertView.findViewById(R.id.tv_submenu_title)).setText(item.menu);
        ((TextView)convertView.findViewById(R.id.tv_submenu_price)).setText(String.valueOf(item.price));
        final TextView tvNum = ((TextView)convertView.findViewById(R.id.tv_submenu_num));
        ((ImageView)convertView.findViewById(R.id.iv_submenu_remove)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tvNum.setText(String.valueOf(Integer.parseInt(tvNum.getText().toString()) - 1));
            }
        });
        ((ImageView)convertView.findViewById(R.id.iv_submenu_add)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                tvNum.setText(String.valueOf(Integer.parseInt(tvNum.getText().toString()) + 1));
            }
        });

        return convertView;
    }

    @Override
    public int getRealChildrenCount(int groupPosition) {
        return items.get(groupPosition).items.size();
    }

    @Override
    public Menu getGroup(int groupPosition) {
        return items.get(groupPosition);
    }

    @Override
    public int getGroupCount() {
        return items.size();
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        //GroupHolder holder;
        Menu item = getGroup(groupPosition);
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.item_menu_raw, parent, false);
        }
        ((TextView)convertView.findViewById(R.id.tv_menu_raw)).setText(item.menu);

        Picasso.with(context)
                .load(item.res)
                .resize(400, 400)
                .centerCrop()
                .into(((ImageView)convertView.findViewById(R.id.iv_menu_img)));


        return convertView;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public boolean isChildSelectable(int arg0, int arg1) {
        return true;
    }


}
