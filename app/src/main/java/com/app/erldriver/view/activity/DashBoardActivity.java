package com.app.erldriver.view.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.app.erldriver.ERLApp;
import com.app.erldriver.R;
import com.app.erldriver.adapter.NavigationItemsListAdapter;
import com.app.erldriver.adapter.ViewPagerAdapter;
import com.app.erldriver.callback.SelectINavigationItemListener;
import com.app.erldriver.callback.SelectItemListener;
import com.app.erldriver.databinding.ActivityDashboardBinding;
import com.app.erldriver.databinding.NavHeaderDashboardBinding;
import com.app.erldriver.model.entity.response.BaseResponse;
import com.app.erldriver.model.entity.response.User;
import com.app.erldriver.util.AppConstant;
import com.app.erldriver.util.AppUtils;
import com.app.erldriver.util.LoginViewModelFactory;
import com.app.erldriver.util.ResourceProvider;
import com.app.erldriver.util.ViewPagerDisableSwipe;
import com.app.erldriver.view.fragment.HomeFragment;
import com.app.erldriver.view.fragment.MyOrderFragment;
import com.app.erldriver.view.fragment.PriceListFragment;
import com.app.erldriver.viewModel.UserAuthenticationViewModel;
import com.app.utilities.callbacks.DialogButtonClickListener;
import com.app.utilities.utils.AlertDialogHelper;
import com.app.utilities.utils.Constant;
import com.app.utilities.utils.GlideUtil;
import com.app.utilities.utils.StringHelper;
import com.google.firebase.iid.FirebaseInstanceId;

public class DashBoardActivity extends BaseActivity implements View.OnClickListener, SelectINavigationItemListener, DialogButtonClickListener, SelectItemListener {
    private ActivityDashboardBinding binding;
    private NavHeaderDashboardBinding bindingNavHeader;
    private UserAuthenticationViewModel userAuthenticationViewModel;
    private Context mContext;

    private ActionBarDrawerToggle toggle;
    private ViewPagerAdapter pagerAdapter;
    private int selectedTabIndex = 0;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_dashboard);
//        bindingNavHeader = DataBindingUtil.bind(binding.navView.getHeaderView(0));
        mContext = this;
        userAuthenticationViewModel = ViewModelProviders.of(this, new LoginViewModelFactory(new ResourceProvider(getResources()))).get(UserAuthenticationViewModel.class);
        userAuthenticationViewModel.createView(this);
        userAuthenticationViewModel.mBaseResponse()
                .observe(this, getBaseResponse());

        setSupportActionBar(binding.appBarLayout.toolbar);
        setupToolbar(getString(R.string.lbl_dashboard), false);

        toggle = new ActionBarDrawerToggle(
                this, binding.drawerLayout, binding.appBarLayout.toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        binding.drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        setNavigationItemAdapter();
        setUserDetails();

        binding.appBarLayout.routHomeTab.setOnClickListener(this);
        binding.appBarLayout.routPriceListTab.setOnClickListener(this);
        binding.appBarLayout.routMyOrderTab.setOnClickListener(this);
        binding.appBarLayout.routWalletTab.setOnClickListener(this);

        setupViewPager(binding.appBarLayout.viewPager);

        getFcmToken();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.routHomeTab:
                setupTab(0);
                break;
            case R.id.routPriceListTab:
                setupTab(1);
                break;
            case R.id.routMyOrderTab:
                setupTab(2);
                break;
            case R.id.routWalletTab:
                setupTab(3);
                break;
        }
    }

    private void setNavigationItemAdapter() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false);
        binding.rvNavigationItems.setLayoutManager(linearLayoutManager);
        binding.rvNavigationItems.setHasFixedSize(true);
        NavigationItemsListAdapter adapter = new NavigationItemsListAdapter(mContext, this);
        binding.rvNavigationItems.setAdapter(adapter);
    }

    private void setDashboardAdapter() {
//        if (getDashBoardData() != null && getDashBoardData().getInfo() != null && getDashBoardData().getInfo().size() > 0) {
//            binding.appBarLayout.rvDashBoardItems.setVisibility(View.VISIBLE);
//            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false);
//            binding.appBarLayout.rvDashBoardItems.setLayoutManager(linearLayoutManager);
//            binding.appBarLayout.rvDashBoardItems.setHasFixedSize(true);
//            ClientDashBoardAdapter adapter = new ClientDashBoardAdapter(mContext, getDashBoardData().getInfo(), this);
//            binding.appBarLayout.rvDashBoardItems.setAdapter(adapter);
//        } else {
//            binding.appBarLayout.rvDashBoardItems.setVisibility(View.GONE);
//        }
    }

    @Override
    public void onSelectItem(int position, String item) {
        Bundle bundle = new Bundle();
        if (item.equals(getString(R.string.my_profile))) {
            moveActivityForResult(mContext, MyProfileActivity.class, false, false, AppConstant.IntentKey.VIEW_PROFILE, null);
        } else if (item.equals(getString(R.string.my_order))) {
            moveActivity(mContext, MyOrderListActivity.class, false, false, null);
        } else if (item.equals(getString(R.string.lbl_services))) {
            moveActivity(mContext, OurServicesActivity.class, false, false, null);
        } else if (item.equals(getString(R.string.terms_and_conditions))) {
            bundle.putInt(AppConstant.IntentKey.TYPE, AppConstant.Type.TERMS_CONDITIONS);
            moveActivity(mContext, PrivacyPolicyActivity.class, false, false, bundle);
        } else if (item.equals(getString(R.string.privacy_policy))) {
            bundle.putInt(AppConstant.IntentKey.TYPE, AppConstant.Type.PRIVACY_POLICY);
            moveActivity(mContext, PrivacyPolicyActivity.class, false, false, bundle);
        } else if (item.equals(getString(R.string.logout))) {
            AlertDialogHelper.showDialog(mContext, "", getString(R.string.logout_msg), getString(R.string.yes), getString(R.string.no), false, this, AppConstant.DialogIdentifier.LOGOUT);
        }
        binding.drawerLayout.closeDrawer(GravityCompat.START);
    }

    public void moveOrderItemsList(int position) {
//        Bundle bundle = new Bundle();
//        bundle.putInt(AppConstant.IntentKey.POSITION, position);
//        bundle.putParcelable(AppConstant.IntentKey.DASHBOARD_DATA, Parcels.wrap(getDashBoardData()));
//        moveActivity(mContext, SelectOrderItemsActivity.class, false, false, bundle);
    }

    public void refreshTotalItemPrice() {
        for (int i = 0; i < pagerAdapter.getmFragmentList().size(); i++) {
            if (pagerAdapter.getmFragmentList().get(i) instanceof PriceListFragment) {
                ((PriceListFragment) (pagerAdapter.getmFragmentList().get(i))).setTotalItemPrice();
            }
        }
    }

    @Override
    public void onPositiveButtonClicked(int dialogIdentifier) {
        if (dialogIdentifier == AppConstant.DialogIdentifier.LOGOUT) {
            ERLApp.get().clearData();
            moveActivity(mContext, LoginActivity.class, true, true, null);
        }
    }

    @Override
    public void onNegativeButtonClicked(int dialogIdentifier) {

    }

    @Override
    public void onSelectItem(int position, int action) {

    }

    public void setUserDetails() {
        User user = AppUtils.getUserPrefrence(mContext);
        if (user != null) {
            binding.txtUserName.setText(user.getName());
            if (!StringHelper.isEmpty(user.getImage())) {
                GlideUtil.loadImageUsingGlideTransformation(user.getImage(), binding.imgUser, Constant.TransformationType.CIRCLECROP_TRANSFORM, null, null, Constant.ImageScaleType.CENTER_CROP, 0, 0, "", 0, null);
            }
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case AppConstant.IntentKey.VIEW_PROFILE:
                if (resultCode == 1)
                    setUserDetails();
                break;
            default:
                break;
        }
    }

    private void setupViewPager(ViewPagerDisableSwipe viewPager) {
        viewPager.setPagingEnabled(false);
        pagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());
        pagerAdapter.addFrag(HomeFragment.newInstance(), "");
        pagerAdapter.addFrag(PriceListFragment.newInstance(), "");
        pagerAdapter.addFrag(MyOrderFragment.newInstance(), "");
        pagerAdapter.addFrag(MyOrderFragment.newInstance(), "");

        viewPager.setAdapter(pagerAdapter);
        setupTab(selectedTabIndex);
        viewPager.setOffscreenPageLimit(4);
    }

    public void setupTab(int position) {
        selectedTabIndex = position;
        switch (position) {
            case 0:
                binding.appBarLayout.viewPager.setCurrentItem(position);
                setupHomeButton(false, getString(R.string.lbl_dashboard), false);
                break;
            case 1:
                binding.appBarLayout.viewPager.setCurrentItem(position);
                setupHomeButton(false, getString(R.string.lbl_price_list), false);
                break;
            case 2:
                binding.appBarLayout.viewPager.setCurrentItem(position);
                setupHomeButton(false, getString(R.string.my_order), false);
                break;
            case 3:
                binding.appBarLayout.viewPager.setCurrentItem(position);
                setupHomeButton(false, getString(R.string.lbl_wallet), false);
                break;
        }
    }

    public void setupHomeButton(boolean isBackEnable, String title, boolean isClearTitle) {
        if (isBackEnable) {
            Toolbar toolbar = findViewById(R.id.toolbar);
            toolbar.setNavigationIcon(R.drawable.ic_arrow_back);
            toolbar.setNavigationOnClickListener(v -> onBackPressed());
        } else {
            toggle = new ActionBarDrawerToggle(
                    this, binding.drawerLayout, binding.appBarLayout.toolbar
                    , R.string.navigation_drawer_open, R.string.navigation_drawer_close);
            toggle.setDrawerIndicatorEnabled(false);

            toggle.setToolbarNavigationClickListener(view -> binding.drawerLayout.openDrawer(GravityCompat.START));
            toggle.setHomeAsUpIndicator(R.drawable.ic_navigation_menu);
            binding.drawerLayout.addDrawerListener(toggle);
            toggle.syncState();
        }

        TextView txtTitle = findViewById(R.id.toolBarNavigation);
        if (txtTitle != null) {
            if (!StringHelper.isEmpty(title))
                txtTitle.setText(title);
            else if (isClearTitle)
                txtTitle.setText("");
        }
    }

    public Observer getBaseResponse() {
        return (Observer<BaseResponse>) response -> {
            try {
                if (response == null) {
                    AlertDialogHelper.showDialog(mContext, null,
                            mContext.getString(R.string.error_unknown), mContext.getString(R.string.ok),
                            null, false, null, 0);
                    return;
                }
                if (response.isSuccess()) {

                } else {
                    AppUtils.handleUnauthorized(mContext, response);
                }
            } catch (Exception e) {
            }
        };
    }

    public void getFcmToken() {
        FirebaseInstanceId.getInstance().getInstanceId()
                .addOnCompleteListener(task -> {
                    if (!task.isSuccessful()) {
                        return;
                    }
//                    User user = AppUtils.getUserPrefrence(mContext);
                    String fcmToken = task.getResult().getToken();
                    if (!StringHelper.isEmpty(fcmToken)) {
                        userAuthenticationViewModel.registerFcmRequest(fcmToken);
                    }
                });
    }

}
