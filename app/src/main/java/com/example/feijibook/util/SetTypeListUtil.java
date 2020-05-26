package com.example.feijibook.util;

import com.example.feijibook.R;
import com.example.feijibook.entity.record_type_bean.AddtiveType;
import com.example.feijibook.entity.record_type_bean.CustomType;
import com.example.feijibook.entity.record_type_bean.OptionalType;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 你是我的 on 2019/3/21
 */

// 设置列表数据
public class SetTypeListUtil {
    private static List<OptionalType> expendOptionList = new ArrayList<>();
    private static List<OptionalType> incomeOptionList = new ArrayList<>();
    private static List<AddtiveType> expendAddibleList = new ArrayList<>();
    private static List<AddtiveType> incomeAddibleList = new ArrayList<>();

    /**
     * 添加 自定义 类型
     *
     * @param optionalType 自定义图标信息
     */
    public static void addCustomType(OptionalType optionalType) {
        switch (optionalType.getIncomeOrExpend_O()) {
            case "收入":
                incomeOptionList.add(optionalType);
                break;
            case "支出":
                expendOptionList.add(optionalType);
                break;
            default:
        }
    }

    /**
     * 获取 记录可选择的类型
     *
     * @param type 类型
     * @return 类型列表
     */
    public static List<OptionalType> getOptionalTypeListInDB(String type) {
        switch (type) {
            case "收入":
                return incomeOptionList;
            case "支出":
                return expendOptionList;
            default:

        }
        return null;
    }

    /**
     * 获取 记录可添加类型
     *
     * @param type 支出或收入
     * @return
     */
    public static List<AddtiveType> getAddibleTypeLstInDB(String type) {
        switch (type) {
            case "支出":
                return expendAddibleList;
            case "收入":
                return incomeAddibleList;
            default:
        }
        return null;
    }


    /**
     * 设置 类型数据
     *
     * @param type          收入或支出
     * @param optionalTypes 可选择列表
     * @param addtiveTypes  可添加列表
     */
    public static void setTypeList(String type, List<OptionalType> optionalTypes, List<AddtiveType> addtiveTypes) {
        switch (type) {
            case "收入":
                incomeOptionList = optionalTypes;
                incomeAddibleList = addtiveTypes;
                break;
            case "支出":
                // 提示 支出类型列表 更新
                expendOptionList = optionalTypes;
                expendAddibleList = addtiveTypes;
                break;
            default:
        }
    }

    /**
     * 获取可选列表中数据
     *
     * @param incomeOrExpend 类型是 收入 还是支出
     * @return
     */
    public static List<OptionalType> getOptionalTypeList(String incomeOrExpend) {
        List<OptionalType> optionalTypeArrayList = new ArrayList<>(setOptionalType());
        List<OptionalType> list = new ArrayList<>();

        for (OptionalType optionalType : optionalTypeArrayList) {
            if (optionalType.getIncomeOrExpend_O().equals(incomeOrExpend)) {
                list.add(optionalType);
            }
        }

        return list;
    }

    public static List<AddtiveType> getAddibleTypeList(String incomeOrExpend) {
        List<AddtiveType> addtiveTypeList = new ArrayList<>(setAddtiveType());
        List<AddtiveType> list = new ArrayList<>();

        for (AddtiveType addtiveType : addtiveTypeList) {
            if (addtiveType.getIncomeOrExpend_A().equals(incomeOrExpend)) {
                list.add(addtiveType);
            }
        }

        return list;
    }

    // 第一次进入设置初始化可选择的记录类型
    public static List<OptionalType> setOptionalType() {
        List<OptionalType> list = new ArrayList<>();
        setOptionalTypeData(list, "d", "支出", "日用", R.drawable.category_e_commodity);
        setOptionalTypeData(list, "d", "支出", "汽车", R.drawable.category_e_car);
        setOptionalTypeData(list, "d", "支出", "服饰", R.drawable.category_e_dress);
        setOptionalTypeData(list, "d", "支出", "餐饮", R.drawable.category_e_catering);
        setOptionalTypeData(list, "d", "支出", "娱乐", R.drawable.category_e_entertainmente);
        setOptionalTypeData(list, "d", "支出", "购物", R.drawable.category_e_shopping);
        setOptionalTypeData(list, "d", "支出", "家居", R.drawable.category_e_home);
        setOptionalTypeData(list, "d", "支出", "长辈", R.drawable.category_e_elder);
        setOptionalTypeData(list, "d", "支出", "住房", R.drawable.category_e_house);
        setOptionalTypeData(list, "d", "支出", "通信", R.drawable.category_e_communicate);
        setOptionalTypeData(list, "d", "支出", "交通", R.drawable.category_e_traffic);
        setOptionalTypeData(list, "d", "支出", "蔬菜", R.drawable.category_e_vegetable);
        setOptionalTypeData(list, "d", "支出", "水果", R.drawable.category_e_fruite);
        setOptionalTypeData(list, "d", "支出", "旅行", R.drawable.category_e_travel);
        setOptionalTypeData(list, "d", "支出", "书籍", R.drawable.category_e_books);
        setOptionalTypeData(list, "d", "支出", "美容", R.drawable.category_e_beauty);
        setOptionalTypeData(list, "d", "支出", "孩子", R.drawable.category_e_child);
        setOptionalTypeData(list, "d", "支出", "数码", R.drawable.category_e_digital);
        setOptionalTypeData(list, "d", "支出", "学习", R.drawable.category_e_study);
        setOptionalTypeData(list, "d", "支出", "烟酒", R.drawable.category_e_smoke);
        setOptionalTypeData(list, "d", "支出", "医疗", R.drawable.category_e_medical);
        setOptionalTypeData(list, "d", "支出", "社交", R.drawable.category_e_social);
        setOptionalTypeData(list, "d", "支出", "快递", R.drawable.category_e_express);
        setOptionalTypeData(list, "d", "支出", "礼物", R.drawable.category_e_gift);
        setOptionalTypeData(list, "d", "支出", "办公", R.drawable.category_e_office);
        setOptionalTypeData(list, "d", "支出", "宠物", R.drawable.category_e_pet);
        setOptionalTypeData(list, "d", "支出", "运动", R.drawable.category_e_sport);

        setOptionalTypeData(list, "d", "收入", "工资", R.drawable.category_i_finance);
        setOptionalTypeData(list, "d", "收入", "礼金", R.drawable.category_e_money);
        setOptionalTypeData(list, "d", "收入", "兼职", R.drawable.category_i_parttimework);
        setOptionalTypeData(list, "d", "收入", "理财", R.drawable.category_i_finance);
        setOptionalTypeData(list, "d", "收入", "其他", R.drawable.category_i_other);

        return list;
    }

    private static void setOptionalTypeData(List<OptionalType> list, String dOrCustom, String incomeOrExpend_O,
                                            String typeName_O, int typeIconUrl_O) {
        OptionalType optionalType = new OptionalType();
        optionalType.setDefaultOrCustom_O(dOrCustom);
        optionalType.setCustomTypeName(null);
        optionalType.setIncomeOrExpend_O(incomeOrExpend_O);
        optionalType.setTypeName_O(typeName_O);
        optionalType.setTypeIconUrl_O(typeIconUrl_O);
        list.add(optionalType);
    }

    // 第一次进入设置初始化可添加的记录类型
    public static List<AddtiveType> setAddtiveType() {
        List<AddtiveType> list = new ArrayList<>();

        setAddtiveTypeData(list, "支出", "维修", R.drawable.category_e_repair);
        setAddtiveTypeData(list, "支出", "捐赠", R.drawable.category_e_donate);
        setAddtiveTypeData(list, "支出", "零食", R.drawable.category_e_snack);
        setAddtiveTypeData(list, "支出", "彩票", R.drawable.category_e_lottery);
        setAddtiveTypeData(list, "支出", "朋友", R.drawable.category_e_friend);

        return list;
    }

    private static void setAddtiveTypeData(List<AddtiveType> list, String incomeOrExpend_A,
                                           String typeName_A, int typeIconUrl_A) {
        AddtiveType addtiveType = new AddtiveType();
        addtiveType.setIncomeOrExpend_A(incomeOrExpend_A);
        addtiveType.setTypeName_A(typeName_A);
        addtiveType.setTypeIconUrl_A(typeIconUrl_A);
        list.add(addtiveType);
    }

    // 通过图标设置可自定义的类型
    public static List<CustomType> setCustomType() {
        List<CustomType> list = new ArrayList<>();

        setCustomTypeData(list, "娱乐", 0);
        setCustomTypeData(list, "娱乐", R.drawable.cc_entertainmente_game_selector);
        setCustomTypeData(list, "娱乐", R.drawable.cc_entertainmente_archery_selector);
        setCustomTypeData(list, "娱乐", R.drawable.cc_entertainmente_badminton_selector);
        setCustomTypeData(list, "娱乐", R.drawable.cc_entertainmente_baseball_selector);
        setCustomTypeData(list, "娱乐", R.drawable.cc_entertainmente_basketball_selector);
        setCustomTypeData(list, "娱乐", R.drawable.cc_entertainmente_billiards_selector);
        setCustomTypeData(list, "娱乐", R.drawable.cc_entertainmente_bowling_selector);
        setCustomTypeData(list, "娱乐", R.drawable.cc_entertainmente_chess_selector);
        setCustomTypeData(list, "娱乐", R.drawable.cc_entertainmente_movies_selector);
        setCustomTypeData(list, "娱乐", R.drawable.cc_study_piano_selector);
        setCustomTypeData(list, "娱乐", R.drawable.cc_entertainmente_poker_selector);
        setCustomTypeData(list, "娱乐", R.drawable.cc_entertainmente_racing_selector);
        setCustomTypeData(list, "娱乐", R.drawable.cc_entertainmente_roller_skating_selector);
        setCustomTypeData(list, "娱乐", R.drawable.cc_entertainmente_sailing_selector);
        setCustomTypeData(list, "娱乐", R.drawable.cc_entertainmente_skiing_selector);
        setCustomTypeData(list, "娱乐", R.drawable.cc_entertainmente_swimming_selector);
        setCustomTypeData(list, "娱乐", R.drawable.cc_entertainmente_whirligig_selector);


        setCustomTypeData(list, "饮食", 0);
        setCustomTypeData(list, "饮食", R.drawable.cc_catering_apple_selector);
        setCustomTypeData(list, "饮食", R.drawable.cc_catering_banana_selector);
        setCustomTypeData(list, "饮食", R.drawable.cc_catering_beer_selector);
        setCustomTypeData(list, "饮食", R.drawable.cc_catering_birthday_cake_selector);
        setCustomTypeData(list, "饮食", R.drawable.cc_catering_bottle_selector);
        setCustomTypeData(list, "饮食", R.drawable.cc_catering_cake_selector);
        setCustomTypeData(list, "饮食", R.drawable.cc_catering_chicken_selector);
        setCustomTypeData(list, "饮食", R.drawable.cc_catering_coffee_selector);
        setCustomTypeData(list, "饮食", R.drawable.cc_catering_drumstick_selector);
        setCustomTypeData(list, "饮食", R.drawable.cc_catering_hamburg_selector);
        setCustomTypeData(list, "饮食", R.drawable.cc_catering_hot_pot_selector);
        setCustomTypeData(list, "饮食", R.drawable.cc_catering_ice_cream_selector);
        setCustomTypeData(list, "饮食", R.drawable.cc_catering_ice_lolly_selector);
        setCustomTypeData(list, "饮食", R.drawable.cc_catering_noodle_selector);
        setCustomTypeData(list, "饮食", R.drawable.cc_catering_red_wine_selector);
        setCustomTypeData(list, "饮食", R.drawable.cc_catering_rice_selector);
        setCustomTypeData(list, "饮食", R.drawable.cc_catering_seafood_selector);
        setCustomTypeData(list, "饮食", R.drawable.cc_catering_sushi_selector);
        setCustomTypeData(list, "饮食", R.drawable.cc_catering_tea_selector);

        setCustomTypeData(list, "医疗", 0);
        setCustomTypeData(list, "医疗", R.drawable.cc_medical_child_selector);
        setCustomTypeData(list, "医疗", R.drawable.cc_catering_tea_selector);
        setCustomTypeData(list, "医疗", R.drawable.cc_medical_doctor_selector);
        setCustomTypeData(list, "医疗", R.drawable.cc_medical_echometer_selector);
        setCustomTypeData(list, "医疗", R.drawable.cc_medical_injection_selector);
        setCustomTypeData(list, "医疗", R.drawable.cc_medical_medicine_selector);
        setCustomTypeData(list, "医疗", R.drawable.cc_medical_pregnant_selector);
        setCustomTypeData(list, "医疗", R.drawable.cc_medical_tooth_selector);
        setCustomTypeData(list, "医疗", R.drawable.cc_medical_transfusion_selector);
        setCustomTypeData(list, "医疗", R.drawable.cc_medical_wheelchair_selector);

        setCustomTypeData(list, "学习", 0);
        setCustomTypeData(list, "学习", R.drawable.cc_study_blackboard_selector);
        setCustomTypeData(list, "学习", R.drawable.cc_shopping_boots_selector);
        setCustomTypeData(list, "学习", R.drawable.cc_study_calculator_selector);
        setCustomTypeData(list, "学习", R.drawable.cc_study_guitars_selector);
        setCustomTypeData(list, "学习", R.drawable.cc_shopping_hat_selector);
        setCustomTypeData(list, "学习", R.drawable.cc_study_lamp_selector);
        setCustomTypeData(list, "学习", R.drawable.cc_study_penpaper_selector);
        setCustomTypeData(list, "学习", R.drawable.cc_study_penruler_selector);
        setCustomTypeData(list, "学习", R.drawable.cc_study_piano_selector);
        setCustomTypeData(list, "学习", R.drawable.cc_study_school_selector);

        setCustomTypeData(list, "交通", 0);
        setCustomTypeData(list, "交通", R.drawable.cc_traffic_bike_selector);
        setCustomTypeData(list, "交通", R.drawable.cc_traffic_car_selector);
        setCustomTypeData(list, "交通", R.drawable.cc_traffic_car_insurance_selector);
        setCustomTypeData(list, "交通", R.drawable.cc_traffic_car_wash_selector);
        setCustomTypeData(list, "交通", R.drawable.cc_traffic_charge_selector);
        setCustomTypeData(list, "交通", R.drawable.cc_traffic_double_deck_bus_selector);
        setCustomTypeData(list, "交通", R.drawable.cc_traffic_car_wash_selector);
        setCustomTypeData(list, "交通", R.drawable.cc_traffic_expressway_selector);
        setCustomTypeData(list, "交通", R.drawable.cc_traffic_gasoline_selector);
        setCustomTypeData(list, "交通", R.drawable.cc_traffic_motorbike_selector);
        setCustomTypeData(list, "交通", R.drawable.cc_traffic_parking_selector);
        setCustomTypeData(list, "交通", R.drawable.cc_traffic_plane_selector);
        setCustomTypeData(list, "交通", R.drawable.cc_traffic_refuel_selector);
        setCustomTypeData(list, "交通", R.drawable.cc_traffic_ship_selector);
        setCustomTypeData(list, "交通", R.drawable.cc_traffic_taxi_selector);
        setCustomTypeData(list, "交通", R.drawable.cc_traffic_train_selector);
        setCustomTypeData(list, "交通", R.drawable.cc_traffic_truck_selector);

        setCustomTypeData(list, "购物", 0);
        setCustomTypeData(list, "购物", R.drawable.cc_shopping_baby_selector);
        setCustomTypeData(list, "购物", R.drawable.cc_shopping_belt_selector);
        setCustomTypeData(list, "购物", R.drawable.cc_shopping_bikini_selector);
        setCustomTypeData(list, "购物", R.drawable.cc_shopping_boots_selector);
        setCustomTypeData(list, "购物", R.drawable.cc_shopping_camera_selector);
        setCustomTypeData(list, "购物", R.drawable.cc_shopping_cosmetics_selector);
        setCustomTypeData(list, "购物", R.drawable.cc_shopping_earrings_selector);
        setCustomTypeData(list, "购物", R.drawable.cc_shopping_eye_shadow_selector);
        setCustomTypeData(list, "购物", R.drawable.cc_shopping_flower_selector);
        setCustomTypeData(list, "购物", R.drawable.cc_shopping_flower_selector);
        setCustomTypeData(list, "购物", R.drawable.cc_shopping_flowerpot_selector);
        setCustomTypeData(list, "购物", R.drawable.cc_shopping_glasses_selector);
        setCustomTypeData(list, "购物", R.drawable.cc_shopping_hand_cream_selector);
        setCustomTypeData(list, "购物", R.drawable.cc_shopping_hat_selector);
        setCustomTypeData(list, "购物", R.drawable.cc_shopping_headset_selector);
        setCustomTypeData(list, "购物", R.drawable.cc_shopping_high_heels_selector);
        setCustomTypeData(list, "购物", R.drawable.cc_shopping_kettle_selector);
        setCustomTypeData(list, "购物", R.drawable.cc_shopping_knickers_selector);
        setCustomTypeData(list, "购物", R.drawable.cc_shopping_lipstick_selector);
        setCustomTypeData(list, "购物", R.drawable.cc_shopping_mascara_selector);
        setCustomTypeData(list, "购物", R.drawable.cc_shopping_necklace_selector);
        setCustomTypeData(list, "购物", R.drawable.cc_shopping_necklace_selector);
        setCustomTypeData(list, "购物", R.drawable.cc_shopping_package_selector);
        setCustomTypeData(list, "购物", R.drawable.cc_shopping_ring_selector);
        setCustomTypeData(list, "购物", R.drawable.cc_shopping_shopping_trolley_selector);
        setCustomTypeData(list, "购物", R.drawable.cc_shopping_skirt_selector);
        setCustomTypeData(list, "购物", R.drawable.cc_shopping_sneaker_selector);
        setCustomTypeData(list, "购物", R.drawable.cc_shopping_tie_selector);
        setCustomTypeData(list, "购物", R.drawable.cc_shopping_toiletries_selector);
        setCustomTypeData(list, "购物", R.drawable.cc_shopping_trousers_selector);
        setCustomTypeData(list, "购物", R.drawable.cc_shopping_watch_selector);

        setCustomTypeData(list, "生活", 0);
        setCustomTypeData(list, "生活", R.drawable.cc_home_bathtub_selector);
        setCustomTypeData(list, "生活", R.drawable.cc_life_buddha_selector);
        setCustomTypeData(list, "生活", R.drawable.cc_life_candlelight_selector);
        setCustomTypeData(list, "生活", R.drawable.cc_life_date_selector);
        setCustomTypeData(list, "生活", R.drawable.cc_life_holiday_selector);
        setCustomTypeData(list, "生活", R.drawable.cc_life_hotel_selector);
        setCustomTypeData(list, "生活", R.drawable.cc_life_moods_of_love_selector);
        setCustomTypeData(list, "生活", R.drawable.cc_life_spa_selector);
        setCustomTypeData(list, "生活", R.drawable.cc_life_sunbath_selector);
        setCustomTypeData(list, "生活", R.drawable.cc_life_tea_selector);
        setCustomTypeData(list, "生活", R.drawable.cc_life_tent_selector);
        setCustomTypeData(list, "生活", R.drawable.cc_life_trip_selector);

        setCustomTypeData(list, "个人", 0);
        setCustomTypeData(list, "个人", R.drawable.cc_personal_bill_selector);
        setCustomTypeData(list, "个人", R.drawable.cc_personal_clap_selector);
        setCustomTypeData(list, "个人", R.drawable.cc_personal_facial_selector);
        setCustomTypeData(list, "个人", R.drawable.cc_personal_favourite_selector);
        setCustomTypeData(list, "个人", R.drawable.cc_personal_friend_selector);
        setCustomTypeData(list, "个人", R.drawable.cc_personal_friend_selector);
        setCustomTypeData(list, "个人", R.drawable.cc_personal_handshake_selector);
        setCustomTypeData(list, "个人", R.drawable.cc_personal_love_selector);
        setCustomTypeData(list, "个人", R.drawable.cc_personal_marry_selector);
        setCustomTypeData(list, "个人", R.drawable.cc_personal_money_selector);
        setCustomTypeData(list, "个人", R.drawable.cc_personal_pc_selector);
        setCustomTypeData(list, "个人", R.drawable.cc_personal_phone_selector);

        setCustomTypeData(list, "家居", 0);
        setCustomTypeData(list, "家居", R.drawable.cc_home_air_conditioner_selector);
        setCustomTypeData(list, "家居", R.drawable.cc_home_bathtub_selector);
        setCustomTypeData(list, "家居", R.drawable.cc_home_bed_selector);
        setCustomTypeData(list, "家居", R.drawable.cc_home_bread_machine_selector);
        setCustomTypeData(list, "家居", R.drawable.cc_home_bulb_selector);
        setCustomTypeData(list, "家居", R.drawable.cc_home_hair_drier_selector);
        setCustomTypeData(list, "家居", R.drawable.cc_home_microwave_oven_selector);
        setCustomTypeData(list, "家居", R.drawable.cc_home_refrigerator_selector);
        setCustomTypeData(list, "家居", R.drawable.cc_home_renovate_selector);
        setCustomTypeData(list, "家居", R.drawable.cc_home_sofa_selector);
        setCustomTypeData(list, "家居", R.drawable.cc_home_tools_selector);
        setCustomTypeData(list, "家居", R.drawable.cc_home_w_and_e_selector);
        setCustomTypeData(list, "家居", R.drawable.cc_home_wardrobe_selector);
        setCustomTypeData(list, "家居", R.drawable.cc_home_washing_machine_selector);
        setCustomTypeData(list, "家居", R.drawable.cc_home_water_selector);

        setCustomTypeData(list, "家庭", 0);
        setCustomTypeData(list, "家庭", R.drawable.cc_family_baby_selector);
        setCustomTypeData(list, "家庭", R.drawable.cc_family_baby_carriage_selector);
        setCustomTypeData(list, "家庭", R.drawable.cc_family_dog_selector);
        setCustomTypeData(list, "家庭", R.drawable.cc_family_feeding_bottle_selector);
        setCustomTypeData(list, "家庭", R.drawable.cc_family_nipple_selector);
        setCustomTypeData(list, "家庭", R.drawable.cc_family_pet_food_selector);
        setCustomTypeData(list, "家庭", R.drawable.cc_family_pet_home_selector);
        setCustomTypeData(list, "家庭", R.drawable.cc_family_teddy_bear_selector);
        setCustomTypeData(list, "家庭", R.drawable.cc_family_toy_duck_selector);
        setCustomTypeData(list, "家庭", R.drawable.cc_family_wooden_horse_selector);

        setCustomTypeData(list, "健身", 0);
        setCustomTypeData(list, "健身", R.drawable.cc_fitness_barbell_selector);
        setCustomTypeData(list, "健身", R.drawable.cc_fitness_bodybuilding_selector);
        setCustomTypeData(list, "健身", R.drawable.cc_fitness_boxing_selector);
        setCustomTypeData(list, "健身", R.drawable.cc_fitness_dumbbell_selector);
        setCustomTypeData(list, "健身", R.drawable.cc_fitness_elliptical_machine_selector);
        setCustomTypeData(list, "健身", R.drawable.cc_fitness_fitball_selector);
        setCustomTypeData(list, "健身", R.drawable.cc_fitness_hand_muscle_developer_selector);
        setCustomTypeData(list, "健身", R.drawable.cc_fitness_running_selector);
        setCustomTypeData(list, "健身", R.drawable.cc_fitness_sit_in_selector);
        setCustomTypeData(list, "健身", R.drawable.cc_fitness_skating_selector);
        setCustomTypeData(list, "健身", R.drawable.cc_fitness_treadmills_selector);
        setCustomTypeData(list, "健身", R.drawable.cc_fitness_weightlifting_selector);

        setCustomTypeData(list, "办公", 0);
        setCustomTypeData(list, "办公", R.drawable.cc_office_clip_selector);
        setCustomTypeData(list, "办公", R.drawable.cc_office_computer_selector);
        setCustomTypeData(list, "办公", R.drawable.cc_office_desk_selector);
        setCustomTypeData(list, "办公", R.drawable.cc_office_drawing_board_selector);
        setCustomTypeData(list, "办公", R.drawable.cc_office_keyboard_selector);
        setCustomTypeData(list, "办公", R.drawable.cc_office_mouse_selector);
        setCustomTypeData(list, "办公", R.drawable.cc_office_pen_container_selector);
        setCustomTypeData(list, "办公", R.drawable.cc_office_pen_ruler_selector);
        setCustomTypeData(list, "办公", R.drawable.cc_office_printer_selector);

        setCustomTypeData(list, "收入", 0);
        setCustomTypeData(list, "收入", R.drawable.cc_income_1_selector);
        setCustomTypeData(list, "收入", R.drawable.cc_income_2_selector);
        setCustomTypeData(list, "收入", R.drawable.cc_income_3_selector);
        setCustomTypeData(list, "收入", R.drawable.cc_income_4_selector);
        setCustomTypeData(list, "收入", R.drawable.cc_income_5_selector);
        setCustomTypeData(list, "收入", R.drawable.cc_income_6_selector);
        setCustomTypeData(list, "收入", R.drawable.cc_income_7_selector);
        setCustomTypeData(list, "收入", R.drawable.cc_income_8_selector);
        setCustomTypeData(list, "收入", R.drawable.cc_income_9_selector);
        setCustomTypeData(list, "收入", R.drawable.cc_income_10_selector);

        setCustomTypeData(list, "其他", 0);
        setCustomTypeData(list, "其他", R.drawable.cc_other_crown_selector);
        setCustomTypeData(list, "其他", R.drawable.cc_other_diamond_selector);
        setCustomTypeData(list, "其他", R.drawable.cc_other_firecracker_selector);
        setCustomTypeData(list, "其他", R.drawable.cc_other_flag_selector);
        setCustomTypeData(list, "其他", R.drawable.cc_other_lantern_selector);
        setCustomTypeData(list, "其他", R.drawable.cc_other_memorial_day_selector);
        setCustomTypeData(list, "其他", R.drawable.cc_other_zongzi_selector);
        return list;
    }

    private static void setCustomTypeData(List<CustomType> list, String category, int typeIconUrl) {
        CustomType customType = new CustomType();
        customType.setCategory(category);
        customType.setTypeIconUrl(typeIconUrl);
        list.add(customType);
    }

}
