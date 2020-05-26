package com.example.feijibook.http;

import com.example.feijibook.activity.add_record_act_from_add_icon_act.ARFAModel;
import com.example.feijibook.activity.add_record_from_calendar_icon_act.ARFCModel;
import com.example.feijibook.activity.album_act.AlbumModel;
import com.example.feijibook.activity.camera_act.CameraModel;
import com.example.feijibook.activity.login_in_act.LIModel;
import com.example.feijibook.activity.main_act.MainModel;
import com.example.feijibook.activity.main_act.detail_frag.DetailModel;
import com.example.feijibook.activity.main_act.find_frag.FindModel;
import com.example.feijibook.activity.main_act.me_frag.MeModel;
import com.example.feijibook.activity.record_detail_act.RDModel;
import com.example.feijibook.activity.sign_up_act.SUModel;
import com.example.feijibook.activity.user_info_act.UIModel;
import com.example.feijibook.activity.weather_act.WeatherModel;

import javax.inject.Singleton;

import dagger.Component;

/**
 * HttpComponent
 *
 * @author PengFei Yue
 * @date 2019/10/18
 * @description Http Dagger2 Component
 */
@Singleton
@Component(modules = HttpModule.class)
public interface HttpComponent {
    /**
     * 将Module中HttpApi接口注入到该界面Model层，方便调用网络请求接口
     */
    void injectSignUpModel(SUModel model);

    void injectLoginInModel(LIModel model);

    void injectMainModel(MainModel model);

    void injectUserInfoModel(UIModel model);

    void injectWeatherModel(WeatherModel model);

    void injectFindModel(FindModel model);

    void injectAddRecordFromAddIcon(ARFAModel model);

    void injectDetailModel(DetailModel model);

    void injectRDModel(RDModel model);

    void injectMeModel(MeModel meModel);

    void injectAlbumModel(AlbumModel model);

    void injectCameraModel(CameraModel model);

    void injectARFCModel(ARFCModel model);
}
