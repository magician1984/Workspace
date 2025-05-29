package com.auo.tester

import android.car.Car
import android.car.CarAppFocusManager
import android.car.CarOccupantZoneManager
import android.car.VehicleAreaSeat
import android.car.VehicleAreaType
import android.car.VehiclePropertyIds
import android.car.content.pm.CarPackageManager
import android.car.hardware.CarPropertyValue
import android.car.hardware.property.CarPropertyManager
import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import com.auo.tester.ui.theme.CarTestTheme
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState

class MainActivity : ComponentActivity() {
    companion object{
        private val PERMISSION = "android.car.permission.CONTROL_CAR_CLIMATE"
    }

    @OptIn(ExperimentalPermissionsApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val mContext : Context = LocalContext.current
            val carPermissionState = rememberPermissionState(Car.PERMISSION_SPEED)

            LaunchedEffect(LocalContext.current) {
                if(!carPermissionState.status.isGranted)
                    carPermissionState.launchPermissionRequest()
            }

            CarTestTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    if(carPermissionState.status.isGranted){
                        var mSpeed by remember{
                            mutableFloatStateOf(0f)
                        }

                        val mCar : Car = remember {
                            Car.createCar(mContext)
                        }
                        val mgr : CarPropertyManager = remember {
                            mCar.getCarManager(Car.PROPERTY_SERVICE) as CarPropertyManager
                        }


//                        mSpeed = mgr.getFloatProperty(VehiclePropertyIds.PERF_VEHICLE_SPEED_DISPLAY, VehicleAreaType.VEHICLE_AREA_TYPE_GLOBAL)
                        mgr.registerCallback(object : CarPropertyManager.CarPropertyEventCallback{
                            override fun onChangeEvent(p0: CarPropertyValue<*>?) {
                                mSpeed = p0?.value as Float
                                Log.d("TAG", "onChangeEvent: $mSpeed")
                            }

                            override fun onErrorEvent(p0: Int, p1: Int) {
                                Log.d("TAG", "onErrorEvent: $p0 $p1")
                            }

                        }, VehiclePropertyIds.PERF_VEHICLE_SPEED_DISPLAY, CarPropertyManager.SENSOR_RATE_UI)

                        Greeting(
                            name = "Speed : $mSpeed",
                            modifier = Modifier.padding(innerPadding)
                        )
                    }else{
                        Greeting(
                            name = "No permission",
                            modifier = Modifier.padding(innerPadding)
                        )
                    }


                }

            }
        }



    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    CarTestTheme {
        Greeting("Android")
    }
}