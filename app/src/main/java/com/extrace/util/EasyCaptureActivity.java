/*
 * Copyright (C) 2018 Jenly Yu
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.extrace.util;

import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.extrace.ui.R;
import com.extrace.ui.main.MainActivity;
import com.king.zxing.CaptureActivity;
import com.king.zxing.DecodeFormatManager;

/**
 * @author Jenly <a href="mailto:jenly1314@gmail.com">Jenly</a>
 * https://github.com/jenly1314/ZXingLite/blob/master/README.md
 */
public class EasyCaptureActivity extends CaptureActivity {


    @Override
    public int getLayoutId() {
        return R.layout.easy_capture_activity;
    }

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        Toolbar toolbar = findViewById(R.id.toolbar);
        StatusBarUtils.immersiveStatusBar(this,toolbar,0.2f);
        TextView tvTitle = findViewById(R.id.tvTitle);
        tvTitle.setText(getIntent().getStringExtra(MainActivity.KEY_TITLE));
        DecodeFormatManager.QR_CODE_FORMATS.addAll(DecodeFormatManager.ONE_D_FORMATS);//仅支持一维码
    }

    public void OnClick(View v){
        switch (v.getId()){
            case R.id.ivLeft:
                onBackPressed();
                break;
        }
    }
}
