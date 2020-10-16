package com.app.erladmin.util;

import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.app.erladmin.viewModel.UserAuthenticationViewModel;

public class LoginViewModelFactory extends ViewModelProvider.NewInstanceFactory {
    private ResourceProvider resourceProvider;


    public LoginViewModelFactory(ResourceProvider resourceProvider) {
        this.resourceProvider = resourceProvider;
    }


    @Override
    public <T extends ViewModel> T create(Class<T> modelClass) {
        if (modelClass.isAssignableFrom(UserAuthenticationViewModel.class)) {
            return (T) new UserAuthenticationViewModel(resourceProvider);
        } 
        throw new IllegalArgumentException("Unknown ViewModel class");
    }
}