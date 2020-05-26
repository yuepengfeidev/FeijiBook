package com.example.feijibook.activity.add_record_act_from_add_icon_act.type_choose_frag;

import android.support.annotation.NonNull;

import com.example.feijibook.app.Constants;
import com.example.feijibook.entity.record_type_bean.AddtiveType;
import com.example.feijibook.entity.record_type_bean.CategoryType;
import com.example.feijibook.entity.record_type_bean.OptionalType;
import com.example.feijibook.util.SetTypeListUtil;
import com.example.feijibook.util.SharedPreferencesUtils;

import java.util.List;

import io.realm.Realm;
import io.realm.RealmList;

/**
 * TCModel
 *
 * @author PengFei Yue
 * @date 2019/10/5
 * @description
 */
public class TCModel implements TCContract.Model {
    @Override
    public List<OptionalType> getIncomeTypeListData() {
        return SetTypeListUtil.getOptionalTypeListInDB("收入");
    }


    @Override
    public void saveTypeSettingToDB(String type) {
        String t;
        if (type.equals(Constants.EXPEND)) {
            t = "支出";
        } else {
            t = "收入";
        }
        Realm realm = Realm.getDefaultInstance();
        final RealmList<OptionalType> optionalTypeRealmList = new RealmList<>();
        optionalTypeRealmList.addAll(SetTypeListUtil.getOptionalTypeListInDB(t));
        final RealmList<AddtiveType> addibleTypeRealmList = new RealmList<>();
        addibleTypeRealmList.addAll(SetTypeListUtil.getAddibleTypeLstInDB(t));
        realm.executeTransactionAsync(new Realm.Transaction() {
            @Override
            public void execute(@NonNull Realm realm) {
                CategoryType categoryType = new CategoryType();
                categoryType.setType(t);
                categoryType.setAddtiveTypeRealmList(addibleTypeRealmList);
                categoryType.setOptionalTypeRealmList(optionalTypeRealmList);
                realm.copyToRealmOrUpdate(categoryType);
            }
        });
    }

    @Override
    public List<OptionalType> getExpendTypeListData() {
        return SetTypeListUtil.getOptionalTypeListInDB("支出");
    }
}
