package com.app.erladmin.viewModel;

import android.os.Handler;

import com.app.erladmin.callback.BasePresenter;
import com.app.erladmin.callback.ViewListener;

/**
 * Base class for all ViewModels.  Reason for this class is that all viewModels need a
 * reference
 * to the UI Thread.
 */
abstract class BaseViewModel extends BasePresenter<ViewListener> {
    protected Handler mUiThreadHandler;

    public void onCreate(Handler handler) {
        mUiThreadHandler = handler;
    }

    public void onDestroy() {
        mUiThreadHandler = null;
    }

}
