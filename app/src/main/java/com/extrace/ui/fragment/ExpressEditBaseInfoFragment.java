package com.extrace.ui.fragment;


import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bigkoo.pickerview.adapter.ArrayWheelAdapter;
import com.bigkoo.pickerview.builder.OptionsPickerBuilder;
import com.bigkoo.pickerview.listener.CustomListener;
import com.bigkoo.pickerview.listener.OnOptionsSelectListener;
import com.bigkoo.pickerview.view.OptionsPickerView;
import com.contrarywind.listener.OnItemSelectedListener;
import com.contrarywind.view.WheelView;
import com.extrace.ui.R;
import com.extrace.ui.main.CustomerManageActivity;
import com.extrace.ui.service.EditDialog;
import com.google.zxing.BarcodeFormat;
import com.king.zxing.util.CodeUtils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static android.app.Activity.RESULT_OK;
import static android.content.Context.CLIPBOARD_SERVICE;
import static com.extrace.ui.fragment.ExpressTaskFragment.REQUEST_LANSHOU;

public class ExpressEditBaseInfoFragment extends Fragment implements View.OnClickListener{

    private TextView expressId;
    private ImageView copy_exId;
    private ImageView ivCode;
    private ImageView sendInfo,revInfo; //获取收发件人信息
    private TextView expressRcvName,expressRcvTel,expressRcvDpt,expressRcvAddr,expressRcvRegion,expressRcvID;
    private TextView expressSndName,expressSndTel,expressSndDpt,expressSndAddr,expressSndRegion,expressSndID;
    private TextView expressType,expressWeight,expressFee,expressInsureFee;

    private static final String TAG = "ExpressEditBaseInfoFrag";
    public ExpressEditBaseInfoFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.express_edit_info_base,container, false);
        expressId = view.findViewById(R.id.expressId);

        copy_exId = view.findViewById(R.id.action_ex_capture_icon);
        ivCode = view.findViewById(R.id.ivCode);
        sendInfo = view.findViewById(R.id.action_ex_snd_icon);
        revInfo = view.findViewById(R.id.action_ex_rcv_icon);

        expressRcvName = view.findViewById(R.id.expressRcvName);
        expressRcvTel = view.findViewById(R.id.expressRcvTel);
        expressRcvDpt = view.findViewById(R.id.expressRcvDpt);
        expressRcvAddr = view.findViewById(R.id.expressRcvAddr);
        expressRcvRegion = view.findViewById(R.id.expressRcvRegion);
        expressRcvID = view.findViewById(R.id.expressRcvID);

        expressSndName = view.findViewById(R.id.expressSndName);
        expressSndTel = view.findViewById(R.id.expressSndTel);
        expressSndDpt = view.findViewById(R.id.expressSndDpt);
        expressSndAddr = view.findViewById(R.id.expressSndAddr);
        expressSndRegion = view.findViewById(R.id.expressSndRegion);
        expressSndID = view.findViewById(R.id.expressSndID);

        expressType = view.findViewById(R.id.expressType);
        expressInsureFee = view.findViewById(R.id.expressInsureFee);
        expressFee = view.findViewById(R.id.expressFee);
        expressWeight = view.findViewById(R.id.expressWeight);

        bindView(view);
        return view;
    }
    private static final int REQUESTCODE_SEND = 111;      //发件人请求码
    private static final int REQUESTCODE_REV = 100;      //收件人请求码
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUESTCODE_SEND && resultCode == RESULT_OK) {
            Bundle bundle = data.getExtras();
            expressSndID.setText(bundle.getString("id"));
            expressSndName.setText(bundle.getString("name"));
            expressSndTel.setText(bundle.getString("telcode"));
            expressSndDpt.setText(bundle.getString("dpt"));
            expressSndAddr.setText(bundle.getString("addr"));
            expressSndRegion.setText(bundle.getString("postcode"));
            Toast.makeText(getContext(), "发件人信息已获取", Toast.LENGTH_SHORT).show();

        }else if (requestCode == REQUESTCODE_REV && resultCode == RESULT_OK){
            Bundle bundle = data.getExtras();
            expressRcvID.setText(bundle.getString("id"));
            expressRcvName.setText(bundle.getString("name"));
            expressRcvTel.setText(bundle.getString("telcode"));
            expressRcvDpt.setText(bundle.getString("dpt"));
            expressRcvAddr.setText(bundle.getString("addr"));
            expressRcvRegion.setText(bundle.getString("postcode"));
            Toast.makeText(getContext(), "收件人信息已获取", Toast.LENGTH_SHORT).show();
        }else {
            Toast.makeText(getContext(), "客户信息未获取", Toast.LENGTH_SHORT).show();
        }
    }


    private void bindView(View view) {
        Intent intent = getActivity().getIntent();
        String mExpressId = intent.getStringExtra("EXPRESS_ID");


        Bundle bundle = intent.getExtras();
        int sn = bundle.getInt("sn",-1);
        if (sn != -1) {
            Log.i(TAG, "bindView: 揽件处理！");
            mExpressId = bundle.getString("expressId");
            expressId.setText(mExpressId);
            expressSndID.setText(bundle.getString("senderId"));
            expressRcvID.setText(bundle.getString("receiverId"));
            expressSndName.setText(bundle.getString("sendername"));
            expressRcvName.setText(bundle.getString("receivername"));

            expressSndAddr.setText(bundle.getString("senderprovince"));
            expressSndDpt.setText(bundle.getString("senderaddressdetail"));
            expressSndTel.setText(bundle.getString("sendertelephonenumber"));

            expressRcvAddr.setText(bundle.getString("receiverprovince"));
            expressRcvDpt.setText(bundle.getString("receiveraddressdetail"));
            expressRcvTel.setText(bundle.getString("receivertelephonenumber"));
        }else {
            Log.i(TAG, "bindView: sn未得到 非揽件！");
        }

        //Log.e("lalal","快递编辑show"+mExpressId);
        if (mExpressId != null){
            expressId.setText(mExpressId);
            createBarCode(mExpressId);
        }else {
            Toast.makeText(getContext(), "未成功识别条形码，请返回重写", Toast.LENGTH_SHORT).show();
            showEditDialog(view);
        }

        copy_exId.setOnClickListener(this);
        ivCode.setOnClickListener(this);
        sendInfo.setOnClickListener(this);
        revInfo.setOnClickListener(this);
        expressType.setOnClickListener(this);
        expressType.setFocusable(false);
        expressType.setFocusableInTouchMode(false);
        showTypePicker();

    }

    private void showEditDialog(View view) {
        //点击弹出对话框
        final EditDialog editDialog = new EditDialog(getActivity());
        editDialog.setTitle("请输入快递单号");
        editDialog.setYesOnclickListener("确定", new EditDialog.onYesOnclickListener() {
            @Override
            public void onYesClick(String phone) {
                if (TextUtils.isEmpty(phone)) {
                    Toast.makeText(getContext(), "不可为空", Toast.LENGTH_SHORT).show();
                    //ToastUtils.showShort(getActivity(), "请输入电话号码");
                } else {
//                    editors.putString("phone", phone);
//                    editors.commit();
                    expressId.setText(phone);
                    createBarCode(phone);
                    editDialog.dismiss();
                    //让软键盘隐藏
                    InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(getView().getApplicationWindowToken(), 0);
                }
            }
        });
        editDialog.setNoOnclickListener("取消", new EditDialog.onNoOnclickListener() {
            @Override
            public void onNoClick() {
                editDialog.dismiss();
            }
        });
        editDialog.show();
    }

    /**
     * 生成条形码
     * @param mExpressId
     */
    private void createBarCode(String mExpressId) {
        //生成条形码最好放子线程生成防止阻塞UI，这里只是演示
        Bitmap bitmap = CodeUtils.createBarCode(mExpressId, BarcodeFormat.CODE_128,760,160,null,true);
        //显示条形码
        ivCode.setImageBitmap(bitmap);
    }
    @Override
    public void onClick(View v) {
        Intent intent;
        switch (v.getId()){
            case R.id.action_ex_capture_icon:
                showEditDialog(v);
                break;
            case R.id.ivCode:
                /**
                 * 将id放到剪切板上
                 */
                if (expressId.getText().toString().isEmpty()){
                    showEditDialog(v);
                }else {
                    String id = expressId.getText().toString();
                    ClipboardManager cmb = (ClipboardManager) getContext().getSystemService(CLIPBOARD_SERVICE);
                    cmb.setText(id);

                    Snackbar.make(v, "快件id已复制", Snackbar.LENGTH_SHORT)
                            .setAction("Action", null).show();
                }
                break;
            case R.id.action_ex_snd_icon://获取发件人信息
                intent = new Intent(getActivity(), CustomerManageActivity.class);
                intent.putExtra("REQUEST_TYPE",true);
                startActivityForResult(intent, REQUESTCODE_SEND);
                break;
            case R.id.action_ex_rcv_icon://获取收件人信息
                intent = new Intent(getActivity(), CustomerManageActivity.class);
                intent.putExtra("REQUEST_TYPE",true);
                startActivityForResult(intent, REQUESTCODE_REV);
                break;

            case R.id.expressType:
                if (pvCustomOptions != null) {
                    pvCustomOptions.show(); //弹出自定义条件选择器
                }
                break;

        }
    }

    private OptionsPickerView pvOptions, pvCustomOptions, pvNoLinkOptions;
    private List<String> cardItem = new ArrayList<>();

    private void showTypePicker() {
        /**
         * @description
         *
         * 注意事项：
         * 自定义布局中，id为 optionspicker 或者 timepicker 的布局以及其子控件必须要有，否则会报空指针。
         * 具体可参考demo 里面的两个自定义layout布局。
         */
        getCardData();
        pvCustomOptions = new OptionsPickerBuilder(getContext(), new OnOptionsSelectListener() {
            @Override
            public void onOptionsSelect(int options1, int option2, int options3, View v) {
                //返回的分别是三个级别的选中位置
                String tx = cardItem.get(options1);//.toString();//.getPickerViewText();
                expressType.setText(tx);        //设置物品类型
            }
        })
                .setLayoutRes(R.layout.pickerview_custom_options, new CustomListener() {
                    @Override
                    public void customLayout(View v) {
                        final TextView tvSubmit = (TextView) v.findViewById(R.id.tv_finish);
                        final TextView tvAdd = (TextView) v.findViewById(R.id.tv_add);
                        tvSubmit.setText("+自定义类型");
                        tvAdd.setText("完成");
                        ImageView ivCancel = (ImageView) v.findViewById(R.id.iv_cancel);
                        tvSubmit.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                pvCustomOptions.dismiss();
                                showEditDialog(0,"物品类型-自定义","请输入物品类型");
                            }
                        });

                        ivCancel.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                pvCustomOptions.dismiss();
                            }
                        });

                        tvAdd.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                pvCustomOptions.returnData();
                                pvCustomOptions.dismiss();
                                //pvCustomOptions.setPicker(cardItem);
                            }
                        });

                    }
                })
                .isDialog(true)
                .setOutSideCancelable(false)
                .build();

        pvCustomOptions.setPicker(cardItem);//添加数据
//        WheelView wheelView = getView().findViewById(R.id.wheelview);
//
//        wheelView.setCyclic(false);
//
//        final List<String> mOptionsItems = new ArrayList<>();
//        mOptionsItems.add("item0");
//        mOptionsItems.add("item1");
//        mOptionsItems.add("item2");
//
//        wheelView.setAdapter(new ArrayWheelAdapter(mOptionsItems));
//        wheelView.setOnItemSelectedListener(new OnItemSelectedListener() {
//            @Override
//            public void onItemSelected(int index) {
//                Toast.makeText(getContext(), "" + mOptionsItems.get(index), Toast.LENGTH_SHORT).show();
//            }
//        });
    }

    private void getCardData() {
        cardItem.add("0、液体类物品");
        cardItem.add("1、服饰，毛线，布匹等");
        cardItem.add("2、易碎物品（陶瓷，玻璃等）");
        cardItem.add("3、文件，画册等");
        cardItem.add("4、五金配件，纽扣等易散落物品");
        cardItem.add("5、时令特产，果蔬等");
        cardItem.add("6、精密仪器，贵重类");
        cardItem.add("7、不规则、超大超长类");


    }
    private void showEditDialog(final int i, String title, String hint) {
        //点击弹出对话框
        final EditDialog editDialog = new EditDialog(getContext());
        editDialog.setTitle(title);
        editDialog.setHintStr(hint);
        editDialog.setInputType(InputType.TYPE_TEXT_VARIATION_PERSON_NAME);

        editDialog.setYesOnclickListener("确定", new EditDialog.onYesOnclickListener() {
            @Override
            public void onYesClick(String phone) {
                if (TextUtils.isEmpty(phone)) {
                    Toast.makeText(getContext(), "不可为空", Toast.LENGTH_SHORT).show();
                } else {
//                    createBarCode(phone);
                    expressType.setText(cardItem.size()+"、"+phone);
                    //cardItem.add(cardItem.size()+""+phone);
                    editDialog.dismiss();
                    //让软键盘隐藏
                    InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(getView().getApplicationWindowToken(), 0);
                }
            }
        });
        editDialog.setNoOnclickListener("取消", new EditDialog.onNoOnclickListener() {
            @Override
            public void onNoClick() {
                editDialog.dismiss();
            }
        });
        editDialog.show();
    }

}
