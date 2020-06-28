package com.app.erldriver.view.fragment;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.app.erldriver.R;
import com.app.erldriver.adapter.ServiceItemsListAdapter;
import com.app.erldriver.callback.SelectItemListener;
import com.app.erldriver.callback.SelectedServiceItemListener;
import com.app.erldriver.databinding.FragmentOrderItemsBinding;
import com.app.erldriver.model.entity.info.ItemInfo;
import com.app.erldriver.model.entity.info.ServiceItemInfo;
import com.app.erldriver.util.AppConstant;
import com.app.erldriver.view.activity.DashBoardActivity;
import com.app.erldriver.view.dialog.AddItemsToCardBottomSheetDialog;

import org.parceler.Parcels;

import java.util.List;


public class OrderItemsTabFragment extends BaseFragment implements View.OnClickListener, SelectItemListener, SelectedServiceItemListener {
    private final int LAYOUT_ACTIVITY = R.layout.fragment_order_items;
    private FragmentOrderItemsBinding binding;
    private Context mContext;
    private ServiceItemsListAdapter adapter;

    public static final OrderItemsTabFragment newInstance(Bundle bundle) {
        OrderItemsTabFragment fragment = new OrderItemsTabFragment();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, LAYOUT_ACTIVITY, container, false);
        mContext = getActivity();
        getBundleData();
        return binding.getRoot();
    }

    private void getBundleData() {
        if (getArguments() != null) {
            if (Parcels.unwrap(getArguments().getParcelable(AppConstant.IntentKey.SERVICE_ITEMS_DATA)) != null) {
                List<ItemInfo> list = Parcels.unwrap(getArguments().getParcelable(AppConstant.IntentKey.SERVICE_ITEMS_DATA));
                Log.e("test", "Fragment Size:" + list.size());
                setAdapter(list);
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

        }
    }

    private void setAdapter(List<ItemInfo> list) {
        if (list != null && list.size() > 0) {
            binding.rvOrderItemsList.setVisibility(View.VISIBLE);
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false);
            binding.rvOrderItemsList.setLayoutManager(linearLayoutManager);
            binding.rvOrderItemsList.setHasFixedSize(true);
            adapter = new ServiceItemsListAdapter(mContext, list, this);
            binding.rvOrderItemsList.setAdapter(adapter);
        } else {
            binding.rvOrderItemsList.setVisibility(View.GONE);
        }
    }

    @Override
    public void onSelectItem(int position, int action) {
        showAddItemsToCardBottomSheetDialog(position, adapter.getList().get(position).getServiceList(), adapter.getList().get(position).getImage());
    }

    @Override
    public void onSelectServiceItem(int rootPosition, int itemPosition, int quantity) {
        if (getActivity() != null && adapter != null) {
            adapter.getList().get(rootPosition).getServiceList().get(itemPosition).setQuantity(quantity);
            ((DashBoardActivity) getActivity()).refreshTotalItemPrice();
        }


        Log.e("test", "rootPosition:" + rootPosition);
        Log.e("test", "itemPosition:" + itemPosition);
    }

    public void showAddItemsToCardBottomSheetDialog(int rootPosition, List<ServiceItemInfo> listItems, String itemImage) {
        AddItemsToCardBottomSheetDialog addItemsToCardBottomSheetDialog = AddItemsToCardBottomSheetDialog.newInstance(mContext, rootPosition, listItems, itemImage, this);
        addItemsToCardBottomSheetDialog.show();
    }

    public ServiceItemsListAdapter getAdapter() {
        return adapter;
    }
}
