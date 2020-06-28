package com.app.erldriver.callback;

import androidx.lifecycle.ViewModel;

import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;

public class BasePresenter<T> extends ViewModel implements Presenter<T> {

    public T view = null;
    CompositeDisposable compositeDisposable = new CompositeDisposable();

    @Override
    public void createView(T v) {
        this.view = v;
    }
     void bindToLifecycle(Disposable disposable) {
        compositeDisposable.add(disposable);
    }

    @Override
    public void destroyView() {
        compositeDisposable.clear();
        view = null;
    }
}
