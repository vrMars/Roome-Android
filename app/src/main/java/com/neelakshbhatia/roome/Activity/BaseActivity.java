package com.neelakshbhatia.roome.Activity;

import android.app.ProgressDialog;
import android.support.annotation.VisibleForTesting;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;

public class BaseActivity extends FragmentActivity {

    @VisibleForTesting
    public ProgressDialog mProgressDialog;

    public void showProgressDialog() {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(this);
            mProgressDialog.setMessage("loading");
            mProgressDialog.setIndeterminate(true);
        }
        //Show
        mProgressDialog.show();
    }

    public void hideProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        hideProgressDialog();
    }

}
