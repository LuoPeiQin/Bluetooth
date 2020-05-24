package com.luo.bluetooth.customview.searchble;

import android.content.Context;
import android.graphics.Color;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.luo.bluetooth.R;
import com.luo.bluetooth.common.Constants;

import java.util.List;

/**
 * 蓝牙搜索界面适配器
 * Created by Administrator on 2016/7/23 0023.
 */
public class BluetoothDeviceAdapter extends BaseAdapter{
    private Context mContext;
    private List<BluetoothDeviceBean> mModels;

    public BluetoothDeviceAdapter(Context mContext, List<BluetoothDeviceBean> mModels) {
        this.mContext = mContext;
        this.mModels = mModels;
    }

    @Override
    public int getCount() {
        return mModels.size();
    }

    @Override
    public Object getItem(int i) {
        return mModels.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder holder=null;
        if(view==null){
            view= LayoutInflater.from(mContext).inflate(R.layout.item_bluetooth_device_list,null);
            holder=new ViewHolder();
            holder.tvName= (TextView) view.findViewById(R.id.tv_bluetooth_name);
            holder.tvAddress= (TextView) view.findViewById(R.id.tv_bluetooth_address);
            holder.svBluetooth= (SignalView) view.findViewById(R.id.svBluetooth);
            view.setTag(holder);
        }else{
            holder= (ViewHolder) view.getTag();
        }
        initView(i,holder);
        return view;
    }
    public void initView(int i,ViewHolder holder){
        BluetoothDeviceBean device=mModels.get(i);
        StringBuffer buf=new StringBuffer();
        buf.append(device.getDeviceName());
        if(!TextUtils.isEmpty(device.getNickName())){
            buf.append("【"+device.getNickName()+"】");
        }
        holder.tvName.setText(buf.toString());
        if(device.getStatus()== Constants.BLUETOOTH_STATUS_CONNECTED){
            holder.tvAddress.setTextColor(Color.BLUE);
        }else{
            holder.tvAddress.setTextColor(Color.RED);
        }
        holder.tvAddress.setText(device.getDeviceAddress());
        int intensity = (device.getRssi()+100)/12;
        holder.svBluetooth.setIntensity(intensity);
//        Log.d("LPQ3", "name:"+holder.tvAddress.getText()+" rssi:"+device.getRssi()+" intensity:"+intensity);
    }
    private class ViewHolder{
        public TextView tvName;
        public TextView tvAddress;
        public SignalView svBluetooth;
    }
}
