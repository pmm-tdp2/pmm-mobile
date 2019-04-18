/*package com.uberpets.library.view;

import android.arch.core.util.Function;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.databinding.BindingAdapter;
import android.support.v7.widget.AppCompatButton;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.comprame.R;
import com.comprame.library.fun.Provider;

import java.util.Date;

public class Bindings {


    @BindingAdapter("validator")
    public static void editTextValidation(EditText editText, Function<CharSequence, String> validator) {
        editText.addTextChangedListener(
                new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    }

                    @Override
                    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    }

                    @Override
                    public void afterTextChanged(Editable editable) {
                        editText.setError(validator.apply(editable));
                    }
                }
        );
    }

    @BindingAdapter("validation")
    public static void editTextValidation(EditText editText, Provider<String> validator) {
        editText.addTextChangedListener(
                new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    }

                    @Override
                    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                    }

                    @Override
                    public void afterTextChanged(Editable editable) {
                        editText.setError(validator.get());
                    }
                }
        );
    }

    @BindingAdapter({"btn_enable"})
    public static void enableByObservable(Button button
            , LiveData<Boolean> liveData) {
        liveData.observeForever(button::setEnabled);
    }

    @BindingAdapter({"btn_enable"})
    public static void enableByObservable(AppCompatButton button
            , LiveData<Boolean> liveData) {
        liveData.observeForever(button::setEnabled);
    }

    @BindingAdapter("imageUrl")
    public static void loadImage(ImageView imageView, String url) {
        Glide.with(imageView.getContext())
                .load(url)
                .placeholder(R.drawable.logo)
                .error(R.drawable.logo)
                .centerCrop()
                .fitCenter()
                .into(imageView);
    }

    @BindingAdapter({"read"})
    public static <T> void readWriteText(TextView editText, MutableLiveData<T> readIn) {
        if (readIn != null)
            readIn.observeForever(data -> {

                        if (data instanceof Date) {
                            editText.setText(Format.human((Date) data));
                        } else {
                            editText.setText(data == null ? null : data.toString());
                        }
                    }
            );
    }

    @BindingAdapter({"read"})
    public static <T> void readButton(AppCompatButton button, MutableLiveData<T> readIn) {
        if (readIn != null)
            readIn.observeForever(data -> {
                        if (data instanceof Date) {
                            button.setText(Format.human((Date) data));
                        } else {
                            button.setText(data == null ? null : data.toString());
                        }
                    }
            );
    }

    @BindingAdapter({"check"})
    public static void checkValue(CheckBox checkBox, MutableLiveData<Boolean> readIn) {
        if (readIn != null)
            readIn.observeForever(checkBox::setChecked);
    }

    @BindingAdapter("onItemSelected")
    public static void setItemSelectedListener(Spinner spinner, AdapterView.OnItemSelectedListener itemSelectedListener) {
        spinner.setOnItemSelectedListener(itemSelectedListener);
    }

    @BindingAdapter({"hide"})
    public static void hide(View view, MutableLiveData<Boolean> readIn) {
        if (readIn != null)
            readIn.observeForever(data -> {
                if (data)
                    view.setVisibility(View.VISIBLE);
                else
                    view.setVisibility(View.INVISIBLE);

            });
    }

}
*/