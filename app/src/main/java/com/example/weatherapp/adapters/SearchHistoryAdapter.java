package com.example.weatherapp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.weatherapp.R;
import com.example.weatherapp.activities.MainActivity;
import com.example.weatherapp.models.SearchHistory;
import com.example.weatherapp.network.CheckConnection;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;

public class SearchHistoryAdapter extends BaseAdapter implements Filterable {

    private static final int MAX_ROW_DISPLAY = 5;
    private MainActivity mainActivity;
    private int layout;
    private ArrayList<SearchHistory> searchHistories;
    private ArrayList<SearchHistory> searchHistoryListFull;

    public SearchHistoryAdapter(MainActivity mainActivity, int layout, ArrayList<SearchHistory> searchHistories) {
        this.mainActivity = mainActivity;
        this.layout = layout;
        this.searchHistories = searchHistories;
        searchHistoryListFull = new ArrayList<>(searchHistories);
    }

    @Override
    public int getCount() {
        if (searchHistories == null) {
            return 0;
        }
        return Math.min(MAX_ROW_DISPLAY, searchHistories.size());
    }

    @Override
    public Object getItem(int position) {
        return searchHistories.get(position).getHitoryCity().toString();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    private class ViewHodler {
        TextView txt_history;
        ImageButton imb_deleteHistory;
    }

    @Override
    public View getView(final int position, View view, ViewGroup parent) {

        ViewHodler hodler;

        if (view == null) {
            hodler = new ViewHodler();
            LayoutInflater inflater = (LayoutInflater) mainActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(layout, null);

            hodler.txt_history = view.findViewById(R.id.txt_history);
            hodler.imb_deleteHistory = view.findViewById(R.id.imb_deleteHistory);

            view.setTag(hodler);
        } else {
            hodler = (ViewHodler) view.getTag();
        }

        hodler.txt_history.setText(searchHistories.get(position).getHitoryCity());

        hodler.imb_deleteHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchHistories.remove(position);
                notifyDataSetChanged();

                try {

                    File file = new File(mainActivity.getFilesDir(), "historySearch.txt");
                    if (file.exists()) {

                        file.delete(); // xóa dữ liệu trong file

                        FileOutputStream fs_out = mainActivity.openFileOutput("historySearch.txt", Context.MODE_APPEND);
                        OutputStreamWriter os = new OutputStreamWriter(fs_out);

                        for (int i = searchHistories.size() - 1; i >= 0; i--) { // ghi dữ liệu mới gồm 5 item vào file
                            os.write(searchHistories.get(i).getHitoryCity() + "\n" + "#\n");
                        }
                        os.close();
                    }
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        });

        hodler.txt_history.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (CheckConnection.haveNetworkConnection(mainActivity.getApplicationContext())) {

                    String boKyTuDacBiet = searchHistories.get(position).getHitoryCity().replace(" ", "").trim().replaceAll("[^\\p{L}\\p{Z}]", "");
                    String citySearch = boKyTuDacBiet.substring(0, 1).toUpperCase() + boKyTuDacBiet.substring(1).toLowerCase();

                    mainActivity.getAllDataByFragments(citySearch);
                    mainActivity.saveFileHSearchistory(mainActivity, citySearch);

                    mainActivity.searchView.closeSearch();
                    mainActivity.toolbarRoot.setTitle(citySearch);
                } else {
                    mainActivity.dialogUtil.showDialogNoUpdateData(mainActivity);
                }
            }
        });

        return view;
    }

    @Override
    public Filter getFilter() {
        return myFilter;
    }

    private Filter myFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            List<SearchHistory> filterList = new ArrayList<>();
            if (constraint == null || constraint.length() == 0) {
                filterList.addAll(searchHistoryListFull);
            } else {
                String filterPattern = constraint.toString().toLowerCase().trim();

                for (SearchHistory item : searchHistoryListFull) {
                    if (item.getHitoryCity().toLowerCase().contains(filterPattern)) {
                        filterList.add(item);
                    }
                }
            }

            FilterResults results = new FilterResults();
            results.values = filterList;

            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            searchHistories.clear();
            searchHistories.addAll((List) results.values);
            notifyDataSetChanged();
        }
    };
}
