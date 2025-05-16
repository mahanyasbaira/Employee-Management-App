package com.vishwaagrotech.digitalhr.ui.tada.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vishwaagrotech.digitalhr.model.Tada
import com.vishwaagrotech.digitalhr.repository.TadaRepository
import com.vishwaagrotech.digitalhr.repository.network.ErrorResponse
import com.vishwaagrotech.digitalhr.utils.EventHandler
import com.google.gson.Gson
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.lang.Exception
import javax.inject.Inject

@HiltViewModel
class TadaListViewModel
@Inject
constructor(val repository: TadaRepository) : ViewModel() {

    private var _rTadaList =
        MutableStateFlow<EventHandler<ArrayList<Tada>>>(EventHandler.Empty)

    var tadaList = _rTadaList.asStateFlow()

    fun onTadaListFetch() {
        viewModelScope.launch {
            _rTadaList.value = EventHandler.Loading
            val response = try {
                repository.getTadaListResponse()
            } catch (e: Exception) {
                e.printStackTrace();
                _rTadaList.value = EventHandler.Failure("Something went wrong.")
                return@launch
            }

            if (response.isSuccessful) {
                val tadas: ArrayList<Tada> = ArrayList()
                for (tada in response.body()!!.data) {
                    tadas.add(
                        Tada(
                            tada.employee,
                            tada.id,
                            tada.remark,
                            tada.status,
                            tada.submitted_date,
                            tada.title,
                            tada.total_expense
                        )
                    )
                }

                _rTadaList.value = EventHandler.Success(tadas)
            } else {
                val responseError = Gson().fromJson(
                    response.errorBody()?.string(),
                    ErrorResponse::class.java
                )

                _rTadaList.value = EventHandler.Failure(responseError.message)
            }
        }
    }
}