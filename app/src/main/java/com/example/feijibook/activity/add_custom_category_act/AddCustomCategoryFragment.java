package com.example.feijibook.activity.add_custom_category_act;


import android.app.Activity;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.feijibook.R;
import com.example.feijibook.adapter.AddCustomTypeRVAdapter;
import com.example.feijibook.app.MyApplication;
import com.example.feijibook.entity.record_type_bean.CustomType;
import com.example.feijibook.util.KeyBoardUtil;
import com.example.feijibook.util.NoticeUpdateUtils;
import com.example.feijibook.util.SoundShakeUtil;
import com.example.feijibook.widget.MyToast;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import io.realm.Realm;
import io.realm.RealmAsyncTask;

import static android.content.Context.INPUT_METHOD_SERVICE;

/**
 * A simple {@link Fragment} subclass.
 */
public class AddCustomCategoryFragment extends Fragment implements ACCContract.View {
    ACCContract.Presenter mPresenter;
    Activity mActivity;
    @BindView(R.id.iv_add_expend_category_back)
    ImageView ivAddExpendCategoryBack;
    @BindView(R.id.tv_add_expend_category_back)
    TextView tvAddExpendCategoryBack;
    @BindView(R.id.tv_finish_custom_category)
    TextView tvFinishCustomCategory;
    @BindView(R.id.ck_choose_type)
    CheckBox ckChooseType;
    @BindView(R.id.et_custom_type_name)
    EditText etCustomTypeName;
    @BindView(R.id.iv_del_custom_type_name)
    ImageView ivDelCustomTypeName;
    @BindView(R.id.rv_add_expend_category)
    RecyclerView rvAddExpendCategory;
    @BindView(R.id.tv_add_custom_category_title)
    TextView tvAddCustomCategoryTitle;
    Unbinder unbinder;
    View view;
    AddCustomTypeRVAdapter adapter;
    private boolean isKeyBoardShow = false;
    //选择的自定义类型图标
    private CustomType mCustomType;
    // 添加的大类别(支出或收入)
    private String category;

    public AddCustomCategoryFragment() {
        // Required empty public constructor
    }

    public static AddCustomCategoryFragment getInstance() {
        return new AddCustomCategoryFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_add_custom_cateory, container, false);
        unbinder = ButterKnife.bind(this, view);

        return view;
    }

    @Override
    public void finishAct() {
        mActivity.finish();
    }

    @Override
    public void initListener() {
        onViewClicked(view);
        adapter.setChooseType(new AddCustomTypeRVAdapter.ChooseType() {
            @Override
            public void chooseListener(CustomType customType) {
                SoundShakeUtil.playSound(SoundShakeUtil.TAP2_SOUND);
                ckChooseType.setButtonDrawable(customType.getTypeIconUrl());
                ckChooseType.setSelected(true);
                mCustomType = customType;
            }
        });

        KeyBoardUtil.SoftKeyBoardListener listener
                = new KeyBoardUtil.SoftKeyBoardListener(mActivity);
        listener.setListener(new KeyBoardUtil.SoftKeyBoardListener.OnSoftBoardChangeListener() {
            @Override
            public void keyBoardShow(int height) {
                isKeyBoardShow = true;
                etCustomTypeName.setCursorVisible(true);
            }

            @Override
            public void keyboardHide(int height) {
                isKeyBoardShow = false;
                etCustomTypeName.setCursorVisible(false);
            }
        });

        etCustomTypeName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String content = String.valueOf(s);
                if (content.length() > 0) {
                    ivDelCustomTypeName.setVisibility(View.VISIBLE);
                } else {
                    ivDelCustomTypeName.setVisibility(View.GONE);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        etCustomTypeName.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    disposeFinish();
                }
                return false;
            }
        });
    }

    @Override
    public void initWidget(List<CustomType> list) {

        RecyclerView.LayoutManager manager = new GridLayoutManager(mActivity, 5);
        adapter = new AddCustomTypeRVAdapter(mActivity, list);
        rvAddExpendCategory.setLayoutManager(manager);
        rvAddExpendCategory.setAdapter(adapter);

        ckChooseType.setSelected(true);

        // 初始化默认第一个图标的信息
        mCustomType = new CustomType();
        mCustomType.setCategory("娱乐");
        mCustomType.setTypeIconUrl(R.drawable.cc_entertainmente_game_selector);
    }

    @Override
    public void getAct(Activity activity) {
        this.mActivity = activity;
    }

    @Override
    public void showTitle(String content) {
        String string = "添加" + content + "类别";
        tvAddCustomCategoryTitle.setText(string);
        category = content;
    }

    @Override
    public void hideKeyboard() {
        InputMethodManager inputMethodManager =
                (InputMethodManager) mActivity.
                        getSystemService(INPUT_METHOD_SERVICE);
        assert inputMethodManager != null;
        inputMethodManager.toggleSoftInput
                (0, InputMethodManager.HIDE_NOT_ALWAYS);
    }

    @Override
    public void disposeFinish() {
        if (etCustomTypeName.getText().toString().equals("")) {
            MyToast myToast = new MyToast(mActivity);
            myToast.showToast("请输入类别名称");
        } else {
            // 提醒其他相关界面更新类型
            NoticeUpdateUtils.noticeUpdateTypes();
            mPresenter.setHideKeyboard();
            mPresenter.setSaveCustomType(etCustomTypeName.getText().toString(),
                    mCustomType, category);
        }
    }

    @Override
    public void setPresenter(ACCContract.Presenter presenter) {
        this.mPresenter = presenter;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @OnClick({R.id.iv_add_expend_category_back, R.id.tv_add_expend_category_back, R.id.tv_finish_custom_category, R.id.ck_choose_type, R.id.et_custom_type_name, R.id.iv_del_custom_type_name})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_add_expend_category_back:
            case R.id.tv_add_expend_category_back:
                SoundShakeUtil.playSound(SoundShakeUtil.CLICK_SWOOSH1_SOUND);
                mPresenter.setFinishAct();
                break;
            case R.id.tv_finish_custom_category:
                SoundShakeUtil.playSound(SoundShakeUtil.CLICK_SWOOSH1_SOUND);
                disposeFinish();
                break;
            case R.id.ck_choose_type:
            case R.id.et_custom_type_name:
                SoundShakeUtil.playSound(SoundShakeUtil.CLICK_SWOOSH1_SOUND);
                if (isKeyBoardShow) {
                    KeyBoardUtil.toggleInput(MyApplication.sContext);
                }
                break;
            case R.id.iv_del_custom_type_name:
                SoundShakeUtil.playSound(SoundShakeUtil.CLICK_SWOOSH1_SOUND);
                etCustomTypeName.setText("");
                break;
            default:
        }
    }
}
