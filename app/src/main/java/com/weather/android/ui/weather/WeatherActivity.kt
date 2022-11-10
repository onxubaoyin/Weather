package com.weather.android.ui.weather

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.weather.android.R
import com.weather.android.logic.model.Weather
import com.weather.android.logic.model.getSky
import kotlinx.android.synthetic.main.activity_weather.*
import kotlinx.android.synthetic.main.forecast.*
import kotlinx.android.synthetic.main.forecast_item.view.*
import kotlinx.android.synthetic.main.life_index.*
import kotlinx.android.synthetic.main.now.*
import java.text.SimpleDateFormat
import java.util.Locale

class WeatherActivity : AppCompatActivity() {

    private val viewModel by lazy {
        ViewModelProvider(this)[WeatherViewModel::class.java]
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //必须赶在布局呈现之前实现有关效果，所以用代码实现状态栏融合效果事先必须提前
        val decorView = window.decorView
        @Suppress("DEPRECATION")
        decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
        window.statusBarColor = Color.TRANSPARENT
        setContentView(R.layout.activity_weather)
        //分别判断以上位置是否为空，如何为空就给经度，纬度和位置传入有关的数值
        if (viewModel.locationLng.isEmpty()){
            viewModel.locationLng = intent.getStringExtra("location_lng") ?: ""
        }
        if (viewModel.locationLat.isEmpty()){
            viewModel.locationLat = intent.getStringExtra("location_lat") ?: ""
        }
        if (viewModel.placeName.isEmpty()){
            viewModel.placeName = intent.getStringExtra("place_name") ?: ""
        }
        //让viewModel把观察完成的数据传入observe方法
        viewModel.weatherLiveData.observe(this, Observer { result ->
            val weather = result.getOrNull()
            if (weather != null){
                showWeatherInfo(weather)
            } else{
                Toast.makeText(this, "无法成功获取天气信息", Toast.LENGTH_SHORT).show()
                result.exceptionOrNull()?.printStackTrace()
            }
        })
        viewModel.refreshWeather(viewModel.locationLng, viewModel.locationLat)
    }

    private fun showWeatherInfo(weather: Weather){
        placeName.text = viewModel.placeName
        val realTime = weather.realtime
        val daily = weather.daily
        //填充now.xml布局中的数据
        val currentTempText = "${realTime.temperature.toInt()}℃"
        currentTemp.text = currentTempText
        currentSky.text = getSky(realTime.skycon).info
        val currentPM25Text = "空气指数 ${realTime.airQuery.aqi.chn.toInt()}"
        currentAQI.text = currentPM25Text
        nowLayout.setBackgroundResource(getSky(realTime.skycon).bg)
        //填充forecast.xml布局中的数据
        forecastLayout.removeAllViews()
        val days = daily.skycon.size
        for (i in 0 until days){
            val skycon = daily.skycon[i]
            val temperature = daily.temperature[i]
            val view = LayoutInflater.from(this).inflate(R.layout.forecast_item, forecastLayout, false)
            val dateInfo = view.dateInfo as TextView
            val skyIcon = view.skyIcon as ImageView
            val skyInfo = view.skyInfo as TextView
            val temperatureInfo = view.temperatureInfo as TextView
            val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            dateInfo.text = simpleDateFormat.format(skycon.date)
            val sky = getSky(skycon.value)
            skyIcon.setImageResource(sky.icon)
            skyInfo.text = sky.info
            val tempText = "${temperature.min.toInt()} ~ ${temperature.max.toInt()}℃"
            temperatureInfo.text = tempText
            forecastLayout.addView(view)
        }
        //填充life_index.xml布局中的数据
        val lifeIndex = daily.lifeIndex
        coldRiskText.text = lifeIndex.coldRisk[0].desc
        dressingText.text = lifeIndex.dressing[0].desc
        ultravioletText.text = lifeIndex.ultraviolet[0].desc
        carWashingText.text = lifeIndex.carWashing[0].desc
        weatherLayout.visibility = View.VISIBLE
    }
}