package com.shelper.overlay

import android.app.Activity
import android.os.Bundle
import android.widget.EditText

class SecondsPopup : Activity() {
    var editText = null;
    var secondButton = null;

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_second_popup);
        editText = findViewById(R.id.secondtext);
        secondButton = findViewById(R.id.secondButton);

    }
}