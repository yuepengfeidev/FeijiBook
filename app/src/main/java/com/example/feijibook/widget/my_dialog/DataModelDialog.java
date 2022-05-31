package com.example.feijibook.widget.my_dialog;

import android.app.Dialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.view.View;

import com.example.feijibook.R;
import com.example.feijibook.app.MyApplication;
import com.example.feijibook.http.BaseObserver;
import com.example.feijibook.http.ExceptionHandle;
import com.example.feijibook.http.HttpApi;
import com.example.feijibook.http.SchedulersCompat;
import com.example.feijibook.http.Utility;
import com.example.feijibook.util.SoundShakeUtil;
import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.formatter.IValueFormatter;
import com.github.mikephil.charting.utils.ViewPortHandler;

import java.util.ArrayList;
import java.util.Map;


import javax.inject.Inject;
import javax.inject.Named;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.reactivex.Observable;
import okhttp3.ResponseBody;

/**
 * DataModelDialog
 *
 * @author PengFei Yue
 * @date 2022/4/5
 * @description
 */
public class DataModelDialog extends Dialog {
    @Named("Scalars")
    @Inject
    HttpApi mHttpApi;
    @BindView(R.id.data_model_pie_chart)
    PieChart dataModelChart;
    int[] mColors = {R.color.pie_chart_purple, R.color.pie_chart_blue,
            R.color.pie_chart_green, R.color.pie_chart_red,
            R.color.pie_chart_orange};

    public DataModelDialog(@NonNull Context context) {
        super(context);
        setContentView(R.layout.data_model_dialog);
        ButterKnife.bind(this);
        MyApplication.getHttpComponent().injectDialog(this);
        initPieChart();
    }

    private void initPieChart() {

        Legend legend = dataModelChart.getLegend();
        legend.setForm(Legend.LegendForm.LINE);
        legend.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM);
        legend.setHorizontalAlignment(Legend.LegendHorizontalAlignment.CENTER);
        legend.setOrientation(Legend.LegendOrientation.HORIZONTAL);
        legend.setFormSize(10);
        legend.setTextSize(10);

        dataModelChart.setDescription(null);
        dataModelChart.setExtraBottomOffset(5);
        dataModelChart.setDrawHoleEnabled(false);
        dataModelChart.setRotationEnabled(false);
        dataModelChart.setEntryLabelColor(ContextCompat.getColor(getContext(),R.color.color_transparent));
        dataModelChart.animateXY(750, 750);
        dataModelChart.invalidate();

        getExpendIntroduce();
    }

    private void setChartData(String[] type, int[] money){
        ArrayList<PieEntry> entries = new ArrayList<>();
        ArrayList<Integer> colors = new ArrayList<>();
        for (int i = 0; i < type.length; i++) {
            entries.add(new PieEntry(money[i], type[i]));
            colors.add(ContextCompat.getColor(getContext(), mColors[i]));
        }
        PieDataSet dataSet = new PieDataSet(entries, "");
        dataSet.setValueFormatter(new IValueFormatter() {
            private int index = -1;
            @Override
            public String getFormattedValue(float v, Entry entry, int i, ViewPortHandler viewPortHandler) {
                index ++;
               return type[index % type.length] + v + "元" ;
            }
        });
        dataSet.setColors(colors);
        dataSet.setSliceSpace(1f);
        dataSet.setValueLinePart1Length(0.55f);
        dataSet.setValueTextSize(10);
        dataSet.setXValuePosition(PieDataSet.ValuePosition.OUTSIDE_SLICE);
        dataSet.setYValuePosition(PieDataSet.ValuePosition.OUTSIDE_SLICE);

        PieData pieData = new PieData(dataSet);
        dataModelChart.setData(pieData);
        dataModelChart.invalidate();
    }

    private void getExpendIntroduce(){
        requestIntroduce().subscribe(new BaseObserver<String>() {
            @Override
            public void onSuccess(String str) {
                Map<String,Object> dataMap = Utility.handleIntroduceResponse(str);

                String[] type = (String[]) dataMap.get("type");
                int[] money = (int[]) dataMap.get("money");

                setChartData(type,money);
            }

            @Override
            public void onHttpError(ExceptionHandle.ResponseException exception) {

            }
        });
    }

    private Observable<String> requestIntroduce(){
        return mHttpApi.getExpendIntroduce().compose(SchedulersCompat.applyToSchedulers());
    }
}
