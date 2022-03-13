package com.luo.bluetooth.customview.searchble;

import android.Manifest;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.stag.bluetooth.BluetoothController;
import com.stag.bluetooth.OnBluetoothScanListener;
import com.luo.bluetooth.R;
import com.luo.bluetooth.base.BaseActivity;
import com.luo.bluetooth.common.Constants;
import com.luo.bluetooth.utils.DialogUtils;
import com.luo.bluetooth.utils.LogUtils;

import java.util.LinkedList;
import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;

import static android.content.pm.PackageManager.PERMISSION_GRANTED;

/**
 * 搜索蓝牙设备界面
 * Created by Administrator on 2016/7/22 0022.
 */
public class DeviceListActivity extends BaseActivity implements View.OnClickListener, AdapterView.OnItemClickListener, AdapterView.OnItemLongClickListener, OnBluetoothScanListener {
    private static final String TAG = "DeviceListActivity";
    public static final String KEY_SCAN_DEVICE = "KEY_SCAN_DEVICE";
    private Button btnSearch, btnHistory, btnCancel;
    private ListView lvDevices;
    private TextView tvTitle, tvNoKey;
    private ProgressBar pbScanning;
    private BluetoothDeviceAdapter adapter;
    private List<BluetoothDeviceBean> mDeviceModes = new LinkedList<BluetoothDeviceBean>();
    private BluetoothController mBluetoothController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        LogUtils.d(TAG + ": lpq", "onCreate: ");
        setContentView(R.layout.activity_search_device_list);
        findViews();
        initData();
        bindEvent();
        this.setFinishOnTouchOutside(false);
    }

    @Override
    public void finish() {
        LogUtils.d(TAG + ": lpq", "finish: ");
        super.finish();
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
    }

    @Override
    protected void onDestroy() {
        LogUtils.d(TAG + ": lpq", "onDestroy: ");
        mBluetoothController.stopScan();
        super.onDestroy();
    }

    protected void findViews() {
        lvDevices = (ListView) findViewById(R.id.lv_devices);
        tvTitle = (TextView) findViewById(R.id.tvTitle);
        tvNoKey = (TextView) findViewById(R.id.tv_no_key);
        pbScanning = (ProgressBar) findViewById(R.id.pbScanning);
        btnSearch = (Button) findViewById(R.id.btn_search);
        btnHistory = (Button) findViewById(R.id.btn_history);
        btnCancel = (Button) findViewById(R.id.btn_cancel);
    }

    public void initData() {
        mBluetoothController = BluetoothController.getController(this);
        tvTitle.setText("设备列表");
        lvDevices.setEmptyView(tvNoKey);
        adapter = new BluetoothDeviceAdapter(this, mDeviceModes);
        lvDevices.setAdapter(adapter);
        startSearchKey();
        getPermissions();
    }

    /**
     * 获取权限
     */
    private void getPermissions() {
        if (ContextCompat.checkSelfPermission(DeviceListActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            //请求权限
            ActivityCompat.requestPermissions(DeviceListActivity.this,
                    new String[]{Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            //判断是否需要 向用户解释，为什么要申请该权限
            ActivityCompat.shouldShowRequestPermissionRationale(DeviceListActivity.this,
                    Manifest.permission.READ_CONTACTS);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (grantResults != null && grantResults.length > 0) {
            if (grantResults[0] == PERMISSION_GRANTED) {
                startSearchKey();
            } else {
                DialogUtils.showErrorDialog(mContext, "没有权限无法使用蓝牙", new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sweetAlertDialog) {
                        DialogUtils.dismissDialog();
                        finish();
                    }
                });
            }
        }
    }

    private void startSearchKey() {
        LogUtils.d(TAG + ": lpq", "startSearchKey: ");
        tvTitle.setText(R.string.title_device_list_search_key);
        showProgressBar();

        mDeviceModes.clear();
        adapter.notifyDataSetChanged();
        mBluetoothController.startScan(this);
    }

    private void stopSearchKey() {
        LogUtils.d(TAG + ": lpq", "stopSearchKey: ");
        tvTitle.setText(R.string.title_device_list_stop_search);
        hideProgressBar();
        mBluetoothController.stopScan();
    }

    private void showHistory() {
        stopSearchKey();
        tvTitle.setText(R.string.title_device_list_history);
        hideProgressBar();
        mDeviceModes.clear();
        adapter.notifyDataSetChanged();
    }

    protected void bindEvent() {
        btnSearch.setOnClickListener(this);
        btnCancel.setOnClickListener(this);
        btnHistory.setOnClickListener(this);
        lvDevices.setOnItemClickListener(this);
        lvDevices.setOnItemLongClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_search:
                startSearchKey();
                break;
            case R.id.btn_history:
                showHistory();
                break;
            case R.id.btn_cancel:
                stopSearchKey();
                finish();
                break;
            default:
                break;
        }
    }

    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, final int i, long l) {
        BluetoothDeviceBean model = mDeviceModes.get(i);
        model.setStatus(Constants.BLUETOOTH_STATUS_CONNECTED);
        //连接成功之后再保存会好一点
//        model.saveOrUpdate("deviceAddress = ?", model.getDeviceAddress());
        Intent intent = new Intent();
        intent.putExtra(KEY_SCAN_DEVICE, model);
        setResult(RESULT_OK, intent);
        stopSearchKey();
        finish();
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
        showSetNickNameDialog(this, mDeviceModes.get(i));
        return true;
    }

    public void showSetNickNameDialog(Context context, final BluetoothDeviceBean model) {
        final EditText editText = new EditText(this);
        editText.setText(model.getNickName());
        final NiftyDialogBuilder dialogBuilder = NiftyDialogBuilder.getInstance(context);
        dialogBuilder.withTitle(getResources().getString(R.string.dialog_set_note_name))//.withTitle(null)  no title
                .withMessage(null)
                .withEffect(Effectstype.values()[9])
                .withTitleColor("#FFFFFF")                                  //def
                .withDividerColor("#11000000")                              //def
                .withMessageColor("#FFFFFFFF")                              //def  | withMessageColor(int resid)
                .withDialogColor(getResources().getColor(R.color.colorPrimary))                               //def  | withDialogColor(int resid)
//                .withIcon(context.getResources().getDrawable(R.mipmap.logo))
                .withDuration(700)                                          //def
                //   .withEffect(effect)                                         //def Effectstype.Slidetop
                .withButton1Text(context.getString(R.string.btn_ok))                                    //def gone
                .isCancelableOnTouchOutside(false)                           //def    | isCancelable(true)
                //  .setCustomView(R.layout.custom_view,v.getContext())         //.setCustomView(View or ResId,context)
                .setCustomView(editText, this)
                .setButton1Click(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        model.setNickName(editText.getText().toString());
//                        model.saveOrUpdate("deviceAddress = ?", model.getDeviceAddress());
                        adapter.notifyDataSetChanged();
                        dialogBuilder.dismiss();
                    }
                }).withButton2Text(context.getString(R.string.btn_cancel))
                .setButton2Click(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialogBuilder.dismiss();
                    }
                })
                .show();
    }

    @Override
    public void onBluetoothScanFindDevice(BluetoothDevice device, int rssi, boolean isBle) {
        for (BluetoothDeviceBean m : mDeviceModes) {
            if (m.getDeviceAddress().equals(device.getAddress())) {
                return;
            }
        }
        Log.i("lpq", "onBluetoothScanFindDevice: device.name = " + device.getName() + "device.mac = " + device.getAddress());
//        List<BluetoothDeviceBean> deviceBeans = LitePal.where("deviceAddress = ?", device.getAddress()).find(BluetoothDeviceBean.class);
//        BluetoothDeviceBean model = deviceBeans.size() == 0 ? null : deviceBeans.get(0);
        BluetoothDeviceBean model = null;
        if (model == null) {
            model = new BluetoothDeviceBean();
            model.setDeviceAddress(device.getAddress());
            model.setDeviceName(device.getName());
            model.setStatus(Constants.BLUETOOTH_STATUS_INIT);
            model.setBle(isBle);
        } else {
            model.setDeviceName(device.getName());
            //感觉这句话没有什么用
//            model.saveOrUpdate("deviceAddress = ?", model.getDeviceAddress());
//            BluetoothDeviceUtil.saveBluetoothDevice(mContext, model);
        }
        model.setRssi(rssi);
        int index = -1;
        for (int i=0;i<mDeviceModes.size();i++){
            if (mDeviceModes.get(i).getRssi() < rssi) {
                index = i;
                break;
            }
        }
        if (index==-1)
            mDeviceModes.add(model);
        else
            mDeviceModes.add(index, model);
        adapter.notifyDataSetChanged();
    }

    private void showProgressBar(){
        pbScanning.setVisibility(View.VISIBLE);
    }

    private void hideProgressBar(){
        pbScanning.setVisibility(View.GONE);
    }

    @Override
    public void onBluetoothScanFinish() {
        tvTitle.setText(R.string.title_device_list_select_key);
    }
}

