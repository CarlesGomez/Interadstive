package com.somcrea.smartads.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.TextView;
import com.manuelpeinado.multichoiceadapter.CheckableImageView;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.somcrea.smartads.R;
import com.somcrea.smartads.models.Interest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by Ruben.
 */
public class InterestsAdapter extends ArrayAdapter<Map<String, List<Interest>>>{

    //region ATRIBUTS
    List<Map<String, List<Interest>>> items = new ArrayList<Map<String, List<Interest>>>();
    private int numberOfCols;
    private List<String> headerPositions = new ArrayList<String>();
    private Map<String, String> itemTypePositionsMap = new LinkedHashMap<String, String>();
    private Map<String, Integer> offsetForItemTypeMap = new LinkedHashMap<String, Integer>();
    LayoutInflater layoutInflater;
    View.OnClickListener mItemClickListener;
    private ArrayList<String> checkedInterests = new ArrayList<String>();
    public static ImageLoader imgLoader=ImageLoader.getInstance();
    //endregion

    //Constructor:
    public InterestsAdapter(Context context, int textViewResourceId, List<Map<String, List<Interest>>> items,
                            int numberOfCols, View.OnClickListener mItemClickListener, ArrayList<String> checkedInterests)
    {
        super(context, textViewResourceId, items);
        this.checkedInterests = checkedInterests;
        this.items = items;
        this.numberOfCols = numberOfCols;
        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.mItemClickListener = mItemClickListener;
    }

    //region METODES
    public String getItemTypeAtPosition(int position){
        String itemType = "Unknown";
        Set<String> set = itemTypePositionsMap.keySet();

        for(String key : set){
            String[] bounds = itemTypePositionsMap.get(key).split(",");
            int lowerBound = Integer.valueOf(bounds[0]);
            int upperBoundary = Integer.valueOf(bounds[1]);
            if (position >= lowerBound && position <= upperBoundary){
                itemType = key;
                break;
            }
        }
        return itemType;
    }

    public int getOffsetForItemType(String itemType){
        return offsetForItemTypeMap.get(itemType);
    }

    public boolean isHeaderPosition(int position){
        return headerPositions.contains(String.valueOf(position));
    }

    private String getHeaderForSection(String section){
        return section;
    }

    //Torna a assignar l'array de interessos checkejats.
    public void setCheckedInterests(ArrayList<String> checkedInterests)
    {
        this.checkedInterests = checkedInterests;
    }
    //endregion

    //region EVENTS
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        convertView = layoutInflater.inflate(R.layout.interest_item, null);

        if(isHeaderPosition(position)){
            View v = convertView.findViewById(R.id.listItemLayout);
            v.setVisibility(View.GONE);

            TextView headerText = (TextView)convertView.findViewById(R.id.headerText);
            String section = getItemTypeAtPosition(position);
            headerText.setText(getHeaderForSection(section));
            return convertView;
        }

        //hide the header
        View v = convertView.findViewById(R.id.headerLayout);
        v.setVisibility(View.GONE);

        Map<String, List<Interest>> map = getItem(position);
        List<Interest> list = map.get(getItemTypeAtPosition(position));

        try {
            for (int i = 0; i <= numberOfCols; i++) {
                FrameLayout grid = (FrameLayout) convertView.findViewWithTag(String.valueOf(i + 1));
                CheckableImageView imageView;
                if (i < list.size()) {
                    Interest model = (Interest) list.get(i);
                    if (grid != null) {

                        //Setting model attributes to graphic elements.
                        imageView = (CheckableImageView) grid.findViewWithTag("image");
                        imgLoader.displayImage(model.getImageUrl(), imageView);
                        imageView.setChecked(this.checkedInterests.contains(model.getId()));

                        TextView textView = (TextView) grid.findViewWithTag("subHeader");
                        textView.setText("#" + model.getName().toLowerCase().replaceAll("\\s+", ""));

                        grid.setTag(R.id.row, position);
                        grid.setTag(R.id.col, i);
                        grid.setOnClickListener(mItemClickListener);
                    }
                } else {
                    if (grid != null) {
                        grid.setVisibility(View.INVISIBLE);
                    }
                }

            }
        }catch (Exception e){e.printStackTrace();}
        //set hooks for click listener
        return convertView;
    }

    @Override
    public int getCount() {
        int totalItems = 0;
        for (Map<String, List<Interest>> map : items){
            Set<String> set = map.keySet();
            for(String key : set){
                //calculate the number of rows each set homogeneous grid would occupy
                List<Interest> l = map.get(key);
                int rows = l.size() % numberOfCols == 0 ? l.size() / numberOfCols : (l.size() / numberOfCols) + 1;

                // insert the header position
                if (rows > 0){
                    headerPositions.add(String.valueOf(totalItems));
                    offsetForItemTypeMap.put(key, totalItems);

                    itemTypePositionsMap.put(key, totalItems + "," + (totalItems + rows) );
                    totalItems += 1; // header view takes up one position
                }
                totalItems+= rows;
            }
        }
        return totalItems;
    }

    @Override
    public Map<String, List<Interest>> getItem(int position) {
        if (!isHeaderPosition(position)){
            String itemType = getItemTypeAtPosition(position);
            List<Interest> list = null;
            for (Map<String, List<Interest>> map : items) {
                if (map.containsKey(itemType)){
                    list = map.get(itemType);
                    break;
                }
            }
            if (list != null){
                int offset = position - getOffsetForItemType(itemType);
                //remove header position
                offset -= 1;
                int low = offset * numberOfCols;
                int high = low + numberOfCols  < list.size() ? (low + numberOfCols) : list.size();
                List<Interest> subList = list.subList(low, high);
                Map<String, List<Interest>> subListMap = new HashMap<String, List<Interest>>();
                subListMap.put(itemType, subList);
                return subListMap;
            }
        }
        return null;
    }
    //endregion
}
