package com.example.feijibook.activity.category_setting_act.category_edit_frag;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.feijibook.R;
import com.example.feijibook.adapter.TypeEditRVAdapter;
import com.example.feijibook.app.MyApplication;
import com.example.feijibook.entity.record_type_bean.AddtiveType;
import com.example.feijibook.entity.record_type_bean.OptionalType;
import com.example.feijibook.helper.ItemDragHelperCallBack;
import com.example.feijibook.util.NoticeUpdateUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * A simple {@link Fragment} subclass.
 */
public class CategoryEditFragment extends Fragment implements CEContract.View {
    CEContract.Presenter mPresenter;
    @BindView(R.id.rv_category_edit)
    RecyclerView rvCategoryEditExpend;
    Unbinder unbinder;
    TypeEditRVAdapter adapter;
    List<OptionalType> mOptionalTypeList = new ArrayList<>();
    List<AddtiveType> mAddtiveTypeList = new ArrayList<>();

    public CategoryEditFragment() {
        // Required empty public constructor
    }

    public static CategoryEditFragment newInstance(){
        return new CategoryEditFragment();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_category_edit, container, false);
        unbinder = ButterKnife.bind(this, view);
        NoticeUpdateUtils.updateTypesPresenters.add(mPresenter);
        mPresenter.setInit();
        return view;
    }

    @Override
    public void setPresenter(CEContract.Presenter presenter) {
        mPresenter = presenter;
    }

    @Override
    public void initListener() {
        adapter.setUpdateTypeList(new TypeEditRVAdapter.UpdateTypeList() {
            @Override
            public void update(List<OptionalType> optionalTypes, List<AddtiveType> addtiveTypes) {
                NoticeUpdateUtils.noticeUpdateTypes();
                mPresenter.setTypeList(optionalTypes,addtiveTypes);
            }
        });
    }

    @Override
    public void initWidget() {
        LinearLayoutManager manager = new LinearLayoutManager(MyApplication.sContext);
        rvCategoryEditExpend.setLayoutManager(manager);

        ItemDragHelperCallBack callBack = new ItemDragHelperCallBack();
        final ItemTouchHelper helper = new ItemTouchHelper(callBack);
        helper.attachToRecyclerView(rvCategoryEditExpend);

        adapter = new TypeEditRVAdapter(MyApplication.sContext,
                helper);

        rvCategoryEditExpend.setAdapter(adapter);

    }

    @Override
    public void initRVData(List<OptionalType> optionalTypes, List<AddtiveType> addtiveTypes) {
        mOptionalTypeList = optionalTypes;
        mAddtiveTypeList = addtiveTypes;
        adapter.setList(mOptionalTypeList,mAddtiveTypeList);
    }

    @Override
    public void updateRecord() {
        mPresenter.setInitRVData();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
        NoticeUpdateUtils.updateTypesPresenters.remove(mPresenter);
    }

    @Override
    public void onResume() {
        super.onResume();
        mPresenter.checkUpdateRecord();
    }
}
