package com.phonebook;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.phonebook.databinding.ActivityHomeBinding;


public class Home extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityHomeBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_home);

        binding.loader.setOnClickListener(this);
        binding.viewModel.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.loader:
                loadContacts(MainActivity.TYPE_LOADER);
                break;
            case R.id.viewModel:
                loadContacts(MainActivity.TYPE_VIEW_MODEL);
                break;
        }
    }

    /**
     * load the contacts
     */
    private void loadContacts(String type) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("type", type);
        startActivity(intent);
    }
}
