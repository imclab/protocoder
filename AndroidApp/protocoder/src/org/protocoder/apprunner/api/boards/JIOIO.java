/*
 * Protocoder 
 * A prototyping platform for Android devices 
 * 
 * 
 * Copyright (C) 2013 Motorola Mobility LLC
 *
 * Permission is hereby granted, free of charge, to any person obtaining
 * a copy of this software and associated documentation files (the "Software"),
 * to deal in the Software without restriction, including without limitation
 * the rights to use, copy, modify, merge, publish, distribute, sublicense,
 * and/or sell copies of the Software, and to permit persons to whom the Software
 * is furnished to do so, subject to the following conditions: 
 * 
 * The above copyright notice and this permission notice shall be included in all 
 * copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR 
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL
 * THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN 
 * THE SOFTWARE.
 * 
 */

package org.protocoder.apprunner.api.boards;

import ioio.lib.api.AnalogInput;
import ioio.lib.api.DigitalInput;
import ioio.lib.api.DigitalOutput;
import ioio.lib.api.IOIO;
import ioio.lib.api.PwmOutput;
import ioio.lib.api.exception.ConnectionLostException;

import org.protocoder.apidoc.annotation.APIMethod;
import org.protocoder.apprunner.JInterface;
import org.protocoder.apprunner.JavascriptInterface;
import org.protocoder.hardware.HardwareCallback;
import org.protocoder.hardware.IOIOBoard;
import org.protocoder.sensors.WhatIsRunning;

import android.app.Activity;
import android.util.Log;

public class JIOIO extends JInterface implements HardwareCallback {

    private String TAG = "JIOIO";

    private IOIOBoard board;

    boolean isStarted = false;

    private IOIO ioio;

    private DigitalOutput led;

    private String moiocallbackfn;

    public JIOIO(Activity a) {
	super(a);
    }

    @JavascriptInterface
    @APIMethod(description = "initializes ioio board", example = "ioio.start();")
    public void start(String callbackfn) {
	moiocallbackfn = callbackfn;
	if (!isStarted) {
	    this.board = new IOIOBoard(a.get(), this);
	    board.powerOn();
	    WhatIsRunning.getInstance().add(board);

	}
    }

    public IOIO get() {
	return ioio;
    }

    @JavascriptInterface
    @APIMethod(description = "clean up and poweroff makr board", example = "ioio.stop();")
    public void stop() {
	isStarted = false;
	board.powerOff();
	board = null;
    }

    @JavascriptInterface
    @APIMethod(description = "", example = "")
    public DigitalOutput openDigitalOutput(int pinNum) throws ConnectionLostException {
	return ioio.openDigitalOutput(pinNum, false); // start with the on board
						      // LED off

    }

    @JavascriptInterface
    @APIMethod(description = "", example = "")
    public DigitalInput openDigitalInput(int pinNum) throws ConnectionLostException {
	return ioio.openDigitalInput(pinNum, DigitalInput.Spec.Mode.PULL_UP);

    }

    @JavascriptInterface
    @APIMethod(description = "", example = "")
    public AnalogInput openAnalogInput(int pinNum) throws ConnectionLostException {
	return ioio.openAnalogInput(pinNum);

    }

    @JavascriptInterface
    @APIMethod(description = "", example = "")
    public PwmOutput openPWMOutput(int pinNum, int freq) throws ConnectionLostException {
	return ioio.openPwmOutput(pinNum, freq);
    }

    @JavascriptInterface
    @APIMethod(description = "", example = "")
    public void setDigitalPin(int num, boolean status) throws ConnectionLostException {
	led.write(status);

    }

    @JavascriptInterface
    @APIMethod(description = "", example = "")
    public void resume() {
	// makr.resume();
    }

    @JavascriptInterface
    @APIMethod(description = "", example = "")
    public void pause() {
	// makr.pause();
    }

    @Override
    public void onConnect(Object obj) {
	this.ioio = (IOIO) obj;
	Log.d(TAG, "MOIO Connected");
	callback(moiocallbackfn);

	isStarted = true;
	this.a.get().runOnUiThread(new Runnable() {

	    @Override
	    public void run() {
	    }
	});
    }

    @Override
    public void setup() {
    }

    @Override
    public void loop() {
    }

    @Override
    public void onComplete() {
	this.a.get().finish();
    }

}