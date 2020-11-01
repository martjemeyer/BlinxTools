package com.dummycoding.mycrypto.calculator;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;

import androidx.appcompat.app.ActionBar;
import androidx.lifecycle.ViewModelProvider;

import com.dummycoding.mycrypto.common.BaseActivity;
import com.dummycoding.mycrypto.databinding.ActivityCalculatorBinding;
import com.google.android.material.textfield.TextInputEditText;

import org.jetbrains.annotations.NotNull;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;
import timber.log.Timber;

public class CalculatorActivity extends BaseActivity {

    CalculatorViewModel mViewModel;
    private ActivityCalculatorBinding mBinding;
    private TextInputEditText mActiveEditText;
    private CompositeDisposable mDisposeBag = new CompositeDisposable();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mViewModel = new ViewModelProvider(this).get(CalculatorViewModel.class);
        mBinding = ActivityCalculatorBinding.inflate(getLayoutInflater());
        setContentView(mBinding.getRoot());

        setSupportActionBar(mBinding.toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        mActiveEditText = mBinding.topInput;
        disableSoftKeyboard();
        registerClickListeners();

        mBinding.fab.setOnClickListener(v -> mViewModel.getLatestData());

        populateTokenPairs();
        mBinding.bottomInputDetail.setText(getCompositionRoot().getRepository().getPreferredCurrency());
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }


    @NotNull
    private AdapterView.OnItemSelectedListener getListener() {
        return new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (parent.getId() == mBinding.topDropDown.getId()) {
                    mViewModel.setTopSelectedIndex(position);
                } else if (parent.getId() == mBinding.middleDropDown.getId()) {
                    mViewModel.setMiddleSelectedIndex(position);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // ignored
            }
        };
    }

    @Override
    protected void onResume() {
        super.onResume();
        subscribeToOutputStream();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mDisposeBag.clear();
    }

    public void showTokenPair(boolean show) {
        mBinding.pairConversionOutput.setVisibility(show ? View.VISIBLE : View.GONE);
        mBinding.pairCombination.setVisibility(show ? View.VISIBLE : View.GONE);
    }


    @SuppressLint("ClickableViewAccessibility")
    private void disableSoftKeyboard() {
        mBinding.topInput.setOnTouchListener((v, event) -> {
            mViewModel.setSelectedInputBox(CalculatorViewModel.SelectedInputBox.TOP);
            return onTouch(v, event);
        });
        mBinding.middleInput.setOnTouchListener((v, event) -> {
            mViewModel.setSelectedInputBox(CalculatorViewModel.SelectedInputBox.MIDDLE);
            return onTouch(v, event);
        });
        mBinding.bottomInput.setOnTouchListener((v, event) -> {
            mViewModel.setSelectedInputBox(CalculatorViewModel.SelectedInputBox.BOTTOM);
            return onTouch(v, event);
        });
    }

    private boolean onTouch(View v, MotionEvent event) {
        v.onTouchEvent(event);
        InputMethodManager imm = (InputMethodManager)v.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
        }
        mActiveEditText = (TextInputEditText) v;
        return true;
    }

    private void registerClickListeners() {
        mBinding.button0.setOnClickListener(v -> mViewModel.buttonClicked(getActiveText(), "0", mActiveEditText.getSelectionStart()));
        mBinding.button1.setOnClickListener(v -> mViewModel.buttonClicked(getActiveText(), "1", mActiveEditText.getSelectionStart()));
        mBinding.button2.setOnClickListener(v -> mViewModel.buttonClicked(getActiveText(), "2", mActiveEditText.getSelectionStart()));
        mBinding.button3.setOnClickListener(v -> mViewModel.buttonClicked(getActiveText(), "3", mActiveEditText.getSelectionStart()));
        mBinding.button4.setOnClickListener(v -> mViewModel.buttonClicked(getActiveText(), "4", mActiveEditText.getSelectionStart()));
        mBinding.button5.setOnClickListener(v -> mViewModel.buttonClicked(getActiveText(), "5", mActiveEditText.getSelectionStart()));
        mBinding.button6.setOnClickListener(v -> mViewModel.buttonClicked(getActiveText(), "6", mActiveEditText.getSelectionStart()));
        mBinding.button7.setOnClickListener(v -> mViewModel.buttonClicked(getActiveText(), "7", mActiveEditText.getSelectionStart()));
        mBinding.button8.setOnClickListener(v -> mViewModel.buttonClicked(getActiveText(), "8", mActiveEditText.getSelectionStart()));
        mBinding.button9.setOnClickListener(v -> mViewModel.buttonClicked(getActiveText(), "9", mActiveEditText.getSelectionStart()));
        mBinding.buttonDot.setOnClickListener(v -> mViewModel.buttonClicked(getActiveText(), ".", mActiveEditText.getSelectionStart()));
        mBinding.buttonClear.setOnClickListener(v -> mViewModel.clearClicked());
    }

    private String getActiveText() {
        Editable text = mActiveEditText.getText();
        return text != null && text.length() != 0 ? text.toString() : "";
    }

    private void subscribeToOutputStream() {
        mDisposeBag.add(
        mViewModel.getOutputStream()
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(result -> {
                    mActiveEditText.setText(result.getOutput());
                    mActiveEditText.setSelection(result.getNewCursorPosition());
                }, throwable -> Timber.e(throwable.toString(), "subscribeToOutputStream: "))
        );

        mDisposeBag.add(
                mViewModel.getTopStream()
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(result -> {
                            mBinding.topInput.setText(result);
                        }, throwable -> Timber.e(throwable.toString(), "subscribeToOutputStream: "))
        );

        mDisposeBag.add(
                mViewModel.getMiddleStream()
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(result -> {
                            mBinding.middleInput.setText(result);
                        }, throwable -> Timber.e(throwable.toString(), "subscribeToOutputStream: "))
        );

        mDisposeBag.add(
                mViewModel.getBottomStream()
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(result -> {
                            mBinding.bottomInput.setText(result);
                        }, throwable -> Timber.e(throwable.toString(), "subscribeToOutputStream: "))
        );

        mDisposeBag.add(
                mViewModel.getProgressStream()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(result -> {
                    mBinding.progress.setVisibility(result ? View.VISIBLE : View.INVISIBLE);
                }, throwable -> Timber.e(throwable.toString(), "subscribeToOutputStream: "))
        );

        mDisposeBag.add(
                mViewModel.getDirectPairIdentifierStream()
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(result -> {
                            mBinding.pairConversionOutput.setVisibility(!result.equals("") ? View.VISIBLE : View.GONE);
                            mBinding.pairCombination.setVisibility(!result.equals("") ? View.VISIBLE : View.GONE);
                            mBinding.pairCombination.setText(result);
                        }, throwable -> Timber.e(throwable.toString(), "subscribeToOutputStream: "))
        );

        mDisposeBag.add(
                mViewModel.getDirectPairOutputStream()
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(result -> {
                            mBinding.pairConversionOutput.setText(result);
                        }, throwable -> Timber.e(throwable.toString(), "subscribeToOutputStream: "))
        );
    }

    private void populateTokenPairs() {

        mDisposeBag.add(
        mViewModel.getBtcPairs()
                .subscribe(result -> {
                    ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                            android.R.layout.simple_spinner_item, result);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    mBinding.topDropDown.setAdapter(adapter);
                    mBinding.topDropDown.setSelection(0);
                    mBinding.middleDropDown.setAdapter(adapter);
                    mBinding.middleDropDown.setSelection(1);

                    mBinding.topDropDown.setOnItemSelectedListener(getListener());
                    mBinding.middleDropDown.setOnItemSelectedListener(getListener());
                }, throwable -> Timber.e(throwable.toString(), "populateTokenPairs, "))
        );
    }
}