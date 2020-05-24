package com.luo.bluetooth.customview.searchble;


import com.luo.bluetooth.customview.searchble.effects.BaseEffects;
import com.luo.bluetooth.customview.searchble.effects.FadeIn;
import com.luo.bluetooth.customview.searchble.effects.Fall;
import com.luo.bluetooth.customview.searchble.effects.FlipH;
import com.luo.bluetooth.customview.searchble.effects.FlipV;
import com.luo.bluetooth.customview.searchble.effects.NewsPaper;
import com.luo.bluetooth.customview.searchble.effects.RotateBottom;
import com.luo.bluetooth.customview.searchble.effects.RotateLeft;
import com.luo.bluetooth.customview.searchble.effects.Shake;
import com.luo.bluetooth.customview.searchble.effects.SideFall;
import com.luo.bluetooth.customview.searchble.effects.SlideBottom;
import com.luo.bluetooth.customview.searchble.effects.SlideLeft;
import com.luo.bluetooth.customview.searchble.effects.SlideRight;
import com.luo.bluetooth.customview.searchble.effects.SlideTop;
import com.luo.bluetooth.customview.searchble.effects.Slit;

/*
 * Copyright 2014 litao
 * https://github.com/sd6352051/NiftyDialogEffects
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
public enum Effectstype {

    fadein(FadeIn.class),
    slideleft(SlideLeft.class),
    slidetop(SlideTop.class),
    slidebottom(SlideBottom.class),
    slideright(SlideRight.class),
    fall(Fall.class),
    newspager(NewsPaper.class),
    fliph(FlipH.class),
    flipv(FlipV.class),
    rotateBottom(RotateBottom.class),
    rotateLeft(RotateLeft.class),
    slit(Slit.class),
    shake(Shake.class),
    sidefill(SideFall.class);

    private Class<? extends BaseEffects> effectsClazz;

    private Effectstype(Class<? extends BaseEffects> mclass) {
        effectsClazz = mclass;
    }

    public BaseEffects getAnimator() {
        BaseEffects bEffects=null;
	try {
		bEffects = effectsClazz.newInstance();
	} catch (ClassCastException e) {
		throw new Error("Can not init animatorClazz instance");
	} catch (InstantiationException e) {
		// TODO Auto-generated catch block
		throw new Error("Can not init animatorClazz instance");
	} catch (IllegalAccessException e) {
		// TODO Auto-generated catch block
		throw new Error("Can not init animatorClazz instance");
	}
	return bEffects;
    }
}
