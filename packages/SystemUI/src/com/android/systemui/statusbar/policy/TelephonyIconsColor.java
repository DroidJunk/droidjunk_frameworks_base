/*
 * Copyright (C) 2008 The Android Open Source Project
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

package com.android.systemui.statusbar.policy;

import com.android.systemui.R;

class TelephonyIconsColor {
    //***** Signal strength icons

    //GSM/UMTS
    static final int[][] TELEPHONY_SIGNAL_STRENGTH = {
        { R.drawable.stat_sys_signal_trans,
          R.drawable.stat_sys_signal_trans,
          R.drawable.stat_sys_signal_trans,
          R.drawable.stat_sys_signal_trans,
          R.drawable.stat_sys_signal_trans },
        { R.drawable.stat_sys_signal_trans,
          R.drawable.stat_sys_signal_1_fully_color,
          R.drawable.stat_sys_signal_2_fully_color,
          R.drawable.stat_sys_signal_3_fully_color,
          R.drawable.stat_sys_signal_4_fully_color }
    };

    static final int[][] TELEPHONY_SIGNAL_STRENGTH_ROAMING = {
        { R.drawable.stat_sys_signal_trans,
          R.drawable.stat_sys_signal_trans,
          R.drawable.stat_sys_signal_trans,
          R.drawable.stat_sys_signal_trans,
          R.drawable.stat_sys_signal_trans },
        { R.drawable.stat_sys_signal_trans,
          R.drawable.stat_sys_signal_1_fully_color,
          R.drawable.stat_sys_signal_2_fully_color,
          R.drawable.stat_sys_signal_3_fully_color,
          R.drawable.stat_sys_signal_4_fully_color }
    };

    static final int[][] DATA_SIGNAL_STRENGTH = TELEPHONY_SIGNAL_STRENGTH;




}

