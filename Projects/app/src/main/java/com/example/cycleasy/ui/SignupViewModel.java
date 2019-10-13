package com.example.cycleasy.ui;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;

public class SignupViewModel extends ViewModel {

    public SignupViewModel(Context context) {

    }

    void createUser(String email, String password) {

    }

    public static class Factory implements ViewModelProvider.Factory {

        private final Context context;

        public Factory(Context context) {
            this.context = context.getApplicationContext();
        }

        @NonNull
        @Override
        public <T extends ViewModel> T create (@NonNull Class<T> modelClass) {
            return ((T)new SignupViewModel(context));
        }
    }

}
